package com.ec.survey.tools;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.*;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ec.survey.model.KeyValue;
import com.ec.survey.model.Property;
import com.ec.survey.service.LdapDBService;
import com.ec.survey.service.PropertiesService;
import com.ec.survey.service.SessionService;
import com.sun.source.util.Trees;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service("departmentWorker")
@Scope("singleton")
public class DepartmentUpdater implements Runnable {

	protected static final Logger logger = Logger.getLogger(DepartmentUpdater.class);

	@Resource(name = "propertiesService")
	protected PropertiesService propertiesService;

	@Resource(name = "sessionService")
	protected SessionService sessionService;

	@Resource(name = "ldapDBService")
	protected LdapDBService ldapDBService;

	@Autowired
	public ServletContext servletContext;

	public @Value("${comref.url:#{null}}") String comrefURL;
	public @Value("${comref.url.domains:#{null}}") String comrefURLDomains;
	public @Value("${comref.certificatepath:#{null}}") String certificatepath;
	public @Value("${comref.keystorepassword:#{null}}") String keystorepassword;
	public @Value("${comref.keypassword:#{null}}") String keypassword;

	@Override
	public void run() {
		try {
			logger.info("DepartmentUpdater started");

			TreeMap<String, String> domains = getDomains();
			ldapDBService.UpdateDomains(domains);

            String departments = getDepartments();
			propertiesService.update(Property.DEPARTMENTS, departments);

			logger.info("DepartmentUpdater: departments updated");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.info("DepartmentUpdater completed");
	}

	private static class DepartmentsEntry implements Comparable<DepartmentsEntry> {
		public String orgcd;
		public long orgid;
		public long orgidparent;
		public boolean deleted;
		public List<DepartmentsEntry> children = new ArrayList<>();

		@Override
		public int compareTo(DepartmentsEntry o) {
			return this.orgcd.compareTo(o.orgcd);
		}
	}

	private String readData(HttpURLConnection conn) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;) {
			sb.append((char)c);
		}
		in.close();
		return sb.toString();
	}

	private String getDepartments() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		sessionService.initializeProxy();

		if (comrefURL == null) {
			throw new Exception("comref url is null");
		}
		if (certificatepath == null) {
			throw new Exception("comref certificatepath is null");
		}
		if (keystorepassword == null) {
			throw new Exception("comref keystorepassword is null");
		}
		if (keypassword == null) {
			throw new Exception("comref keypassword is null");
		}

		// Step 1: Load the PKCS12 keystore
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		InputStream keyStoreInput = servletContext.getResourceAsStream(certificatepath);

		keyStore.load(keyStoreInput, keystorepassword.toCharArray());

		// Step 2: Initialize SSL Context with the keystore
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keypassword.toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);

		// Step 3: Set the default SSL context
		SSLContext.setDefault(sslContext);

		// Step 4: Create a connection
		int start = 0;
		boolean stop = false;

		Map<Long, Set<DepartmentsEntry>> map = new HashMap<>();
		Set<DepartmentsEntry> roots = new HashSet<>();
		Set<Long> handledIds = new HashSet<>();

		while (!stop) {
			logger.info("calling comref, start = " + start);

			URL url = new URL(comrefURL + "&length=1000&start=" + start);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();

			if (responseCode != 200) {
				throw new Exception("responseCode "  + responseCode);
			}

			String departments = readData(connection);

			Document document = builder.parse(new InputSource(new StringReader(departments)));

			Node recordCount = document.getElementsByTagName("recordCount").item(0);
			if (!recordCount.getTextContent().equals("1000")) {
				stop = true; // we reached the last call
			}

			NodeList nodeList = document.getElementsByTagName("_");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				DepartmentsEntry departmentsEntry = new DepartmentsEntry();

				for (int j = 0; j < node.getChildNodes().getLength(); j++) {
					Node child = node.getChildNodes().item(j);
					switch (child.getNodeName()) {
						case "orgcd":
							departmentsEntry.orgcd = child.getTextContent();
							break;
						case "orgid":
							departmentsEntry.orgid = Long.parseLong(child.getTextContent());
							break;
						case "orgidparent":
							departmentsEntry.orgidparent = Long.parseLong(child.getTextContent());
							break;
						case "dtfin":
							if (!child.getTextContent().equals("31/12/9999 00:00:00")) {
								departmentsEntry.deleted = true;
							}
					}
				}

				if (!departmentsEntry.deleted && departmentsEntry.orgcd != null && !departmentsEntry.orgcd.trim().isEmpty()) {
					if (departmentsEntry.orgidparent == 0) {
						roots.add(departmentsEntry);
					}

					if (!map.containsKey(departmentsEntry.orgidparent)) {
						map.put(departmentsEntry.orgidparent, new HashSet<>());
					}

					map.get(departmentsEntry.orgidparent).add(departmentsEntry);
				}
			}

			start+=1000;

			if (start > 20000) {
				throw new Exception("too many calls");
			}
		}

		DepartmentsEntry main = new DepartmentsEntry();
		for (DepartmentsEntry root : roots) {
			main.children.add(root);
			handledIds.add(root.orgid);
			recursiveAddChildren(root, map, handledIds);
		}

		StringBuilder sbuilder = new StringBuilder();

		sbuilder.append("<nodes>");
		for (DepartmentsEntry root : roots) {
			recursivePrintChildren(root, 0, sbuilder);
		}
		sbuilder.append("</nodes>");

        return sbuilder.toString();
	}

	private void recursivePrintChildren(DepartmentsEntry element, int indent, StringBuilder builder) throws IOException {
		builder.append("<node name='").append(element.orgcd).append("'>");
		Collections.sort(element.children);
		for (DepartmentsEntry child : element.children) {
			recursivePrintChildren(child, indent + 1, builder);
		}
		builder.append("</node>");
	}

	private void recursiveAddChildren(DepartmentsEntry element, Map<Long, Set<DepartmentsEntry>> map, Set<Long> handledIds) throws Exception {
		if (map.containsKey(element.orgid)) {
			for (DepartmentsEntry child : map.get(element.orgid)) {
				if (handledIds.contains(child.orgid)) {
					return;
				}

				element.children.add(child);
				handledIds.add(child.orgid);
				recursiveAddChildren(child, map, handledIds);
			}
		}
	}

	private TreeMap<String, String> getDomains() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		sessionService.initializeProxy();

		if (comrefURLDomains == null) {
			throw new Exception("comref domains url is null");
		}
		if (certificatepath == null) {
			throw new Exception("comref certificatepath is null");
		}
		if (keystorepassword == null) {
			throw new Exception("comref keystorepassword is null");
		}
		if (keypassword == null) {
			throw new Exception("comref keypassword is null");
		}

		// Step 1: Load the PKCS12 keystore
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		InputStream keyStoreInput = servletContext.getResourceAsStream(certificatepath);

		keyStore.load(keyStoreInput, keystorepassword.toCharArray());

		// Step 2: Initialize SSL Context with the keystore
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keypassword.toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);

		// Step 3: Set the default SSL context
		SSLContext.setDefault(sslContext);

		// Step 4: Create a connection
		TreeMap<String, String> domains = new TreeMap<>();

		logger.info("calling comref domains");

		URL url = new URL(comrefURLDomains);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();

		if (responseCode != 200) {
			throw new Exception("responseCode " + responseCode);
		}

		String sdomains = readData(connection);

		Document document = builder.parse(new InputSource(new StringReader(sdomains)));

		NodeList nodeList = document.getElementsByTagName("_");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			String domaine = null;
			String nomen = null;
			boolean deleted = false;

			for (int j = 0; j < node.getChildNodes().getLength(); j++) {
				Node child = node.getChildNodes().item(j);
				switch (child.getNodeName()) {
					case "domaine":
						domaine = child.getTextContent();
						break;
					case "nomen":
						nomen = child.getTextContent();
						break;
					case "dtfin":
						if (!child.getTextContent().equals("31/12/9999 00:00:00")) {
							deleted = true;
						}
				}
			}

			if (!deleted && domaine != null && !domaine.trim().isEmpty()) {
				domains.put(domaine, nomen);
			}
		}

		return domains;
	}
}
