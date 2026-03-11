package com.ec.survey.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.ec.survey.model.Property;
import org.springframework.stereotype.Service;

import com.ec.survey.model.KeyValue;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Service("ecService")
public class ECService extends BasicService {

	@Resource(name = "propertiesService")
	protected PropertiesService propertiesService;

	private List<DepartmentNode> departmentNodes = null;

	private static class DepartmentNode {
		public String name;
		public List<DepartmentNode> children = new ArrayList<>();
	}

	private void parse() {
		String departmentsXML = propertiesService.get(Property.DEPARTMENTS);

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(departmentsXML)));

			NodeList nodeList = document.getDocumentElement().getChildNodes();

			this.departmentNodes = new ArrayList<>();

			for (int i = 0; i < nodeList.getLength(); i++) {
				new DepartmentNode();
				Node node = nodeList.item(i);
				this.departmentNodes.add(recursiveLoadNodes(node));
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private DepartmentNode recursiveLoadNodes(Node node) {
		DepartmentNode departmentNode = new DepartmentNode();
		departmentNode.name = node.getAttributes().getNamedItem("name").getNodeValue();

		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			departmentNode.children.add(recursiveLoadNodes(node.getChildNodes().item(j)));
		}

		return departmentNode;
	}
	
	public List<KeyValue> GetEntities(String term, Boolean isDGs)  {
		
		if (this.departmentNodes == null) {
			parse();
		}
		
		List<KeyValue> dgs = new ArrayList<>();
		
		boolean topEntities = "dgs".equals(term) || "aex".equals(term);

		if (topEntities) {
			DepartmentNode node = isDGs ? this.departmentNodes.get(1) : this.departmentNodes.get(0).children.get(0);
			for (DepartmentNode child : node.children) {
				dgs.add(new KeyValue(child.name, "0"));
			}
			return dgs;
		}

		DepartmentNode found = findDepartmentNode(term,isDGs ? this.departmentNodes.get(1) : this.departmentNodes.get(0));
		if (found != null) {
			for (DepartmentNode child : found.children) {
				dgs.add(new KeyValue(child.name, child.children.isEmpty() ? "1" : "0"));
			}
		}

		return dgs;
	}

	private DepartmentNode findDepartmentNode(String name, DepartmentNode node) {
		if (node.name.equals(name)) {

			// special case: for executive agencies, there is sometimes a child with the same name
			// -> use child instead
			for (DepartmentNode child : node.children) {
				if (child.name.equals(name)) {
					return child;
				}
			}

			return node;
		}

		for (DepartmentNode child : node.children) {
			DepartmentNode found = findDepartmentNode(name, child);
			if (found != null) {
				return found;
			}
		}

		return null;
	}

	public String[] getDepartments(boolean dgs, boolean aex)  {
		if (this.departmentNodes == null) {
			parse();
		}
		
		List<String> result = new ArrayList<>();

		if (dgs) {
			recursiveAddDepartments(this.departmentNodes.get(1), result);
		}

		if (aex) {
			recursiveAddDepartments(this.departmentNodes.get(0), result);
		}

		return result.toArray(new String[0]);
	}

	private void recursiveAddDepartments(DepartmentNode node, List<String> result ) {
		result.add(node.name);
		for (DepartmentNode child : node.children) {
			recursiveAddDepartments(child, result);
		}
	}
	
	public String[] getDepartments() throws ParserConfigurationException, IOException, SAXException {
		return getDepartments(true, true);
	}
}
