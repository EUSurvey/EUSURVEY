package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.*;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.CleanupWorker;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.MutableInteger;
import com.ec.survey.tools.RecreateWorker;
import com.ec.survey.tools.Tools;
import com.ec.survey.tools.export.FileExportCreator;
import edu.emory.mathcs.backport.java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.Map.Entry;

@Service("fileService")
public class FileService extends BasicService {

	@Resource(name = "pdfService")
	protected PDFService pdfService;

	public static final String[] filetypes = { "results", "statistics", "charts", "tokens", "contacts", "activities",
			"uploaded file", "download", "image", "logo", "background document", "survey", "contribution" };
	public static final String[] fileextensions = { "PDF", "XLS", "ODS", "DOC", "ODT", "XML", "CSV", "JPG", "PNG",
			"ZIP", "OTHER" };

	public void logOldFileSystemUse(String path) {
		logger.info("OLD FILESYSTEM ACCESS: " + path);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<File> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM File");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<File> getAllInvalid() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("FROM File f where f.deletionDate != null AND f.deletionDate <= :deletionDate");
		query.setDate("deletionDate", new Date());
		return query.list();
	}

	@Transactional(readOnly = true)
	public File get(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (File) session.get(File.class, id);
	}

	@Transactional(readOnly = true)
	public File get(String uid) throws FileNotFoundException {

		if (uid.contains("/")) {
			// the new file system structure has the survey uid as subfolder
			uid = uid.substring(uid.indexOf('/') + 1);
		}

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM File u where u.uid = :uid").setString("uid", uid);
		@SuppressWarnings("unchecked")
		List<File> list = query.list();
		if (list.isEmpty()) {
			throw new FileNotFoundException("No file found for uid " + uid);
		}

		return list.get(0);
	}

	@Transactional(readOnly = true)
	public List<File> getAll(String uid) {

		if (uid.contains("/")) {
			// the new file system structure has the survey uid as subfolder
			uid = uid.substring(uid.indexOf('/') + 1);
		}

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM File u where u.uid = :uid").setString("uid", uid);
		@SuppressWarnings("unchecked")
		List<File> list = query.list();

		return list;
	}

	@Transactional(readOnly = true)
	public File get(String uid, Integer id) throws FileNotFoundException {
		if (id != null) {
			File result = get(id);
			if (result != null)
				return result;
		}

		return get(uid);
	}

	@Transactional(readOnly = false)
	public void add(File file) {
		Session session = sessionFactory.getCurrentSession();
		session.save(file);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addNewTransaction(File file) {
		Session session = sessionFactory.getCurrentSession();
		session.save(file);
	}

	@Transactional(readOnly = false)
	public void update(File file) {
		Session session = sessionFactory.getCurrentSession();
		session.update(file);
	}

	@Transactional(readOnly = false)
	public void delete(File file) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(file);
	}

	@Transactional(readOnly = false)
	public void delete(Integer fileId) {
		if (fileId != null) {
			Session session = sessionFactory.getCurrentSession();
			File file = (File) session.get(File.class, fileId);
			if (file != null) {
				session.delete(file);
			}
		}
	}

	@Transactional(readOnly = false)
	public void save(ExportCache ec) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(ec);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveNewTransaction(ExportCache ec) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(ec);
	}

	@Transactional(readOnly = true)
	public ExportCache getCachedExport(Integer surveyId, String hash, String type) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery(
						"FROM ExportCache c WHERE c.surveyId = :surveyId AND c.filterHash = :hash AND c.type = :type")
				.setInteger("surveyId", surveyId).setString("hash", hash).setString("type", type);
		@SuppressWarnings("unchecked")
		List<ExportCache> list = query.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Map<String, String> getFileNamesForBackgroundDocuments(Map<String, String> backgroundDocuments) {
		Map<String, String> result = new HashMap<>();
		try {
			for (Entry<String, String> entry : backgroundDocuments.entrySet()) {
				String fileUID = getFileUIDFromUrl(entry.getValue());
				File f = get(fileUID);
				result.put(entry.getKey(), f.getName());
			}
		} catch (Exception e) {
			//ignore
		}
		return result;
	}

	public List<FileResult> getFiles2(FileFilter filter) throws Exception {
		final List<FileResult> result = new ArrayList<>();
		final MutableInteger counter = new MutableInteger(0);
		final int skip = filter.getPage() > 1 ? (filter.getPage() - 1) * filter.getItemsPerPage() : 0;
		if (filter.isSearchInFileSystem()) {
			java.io.File dir = null;
			if (filter.getSurveyUid() != null && filter.getSurveyUid().length() > 0) {
				if (filter.isArchivedSurveys()) {
					dir = getArchiveFolder(filter.getSurveyUid());
				} else {
					dir = getSurveyFolder(filter.getSurveyUid());
				}
			} else if (filter.getUserId() > 0) {
				dir = getUsersFolder(filter.getUserId(), false);
				if (dir == null) {
					return result;
				}
			} else if (filter.isArchivedSurveys()) {
				dir = new java.io.File(archiveDir);
			}

			if (dir != null) {
				try {
					Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs)
								throws IOException {
							if (!filter.isSystemExports() && file.endsWith("EXPORTS")) {
								return FileVisitResult.SKIP_SUBTREE;
							}

							if (!filter.isSurveyFiles() && file.endsWith("FILES")) {
								return FileVisitResult.SKIP_SUBTREE;
							}

							if (!filter.isTemporaryFiles() && file.endsWith("UPLOADS")) {
								return FileVisitResult.SKIP_SUBTREE;
							}

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (!file.toFile().isDirectory()) {
								if (skip - counter.getValue() > 0) {
									counter.setValue(counter.getValue() + 1);
								} else {
									FileResult fileResult = getFileResult(file, null,
											new java.io.File(archiveDir).toPath());
									result.add(fileResult);
								}
								if (result.size() >= filter.getItemsPerPage()) {
									return FileVisitResult.TERMINATE;
								}
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
							// Skip folders that can't be traversed
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
					return result;
				}
			}
		} else {
			result.addAll(getFilesForSurvey(filter, filter.getPage(), filter.getItemsPerPage(),
					new java.io.File(archiveDir).toPath()));
		}

		return result;
	}

	public List<FileResult> getFiles(FileFilter inputfilter) throws Exception {
		final List<FileResult> result = new ArrayList<>();

		final Path dir = Paths.get(tempFileDir);
		final Path filedir = Paths.get(fileDir);
		final Path archivedir = Paths.get(archiveFileDir);

		final FileFilter filter = inputfilter;

		int page = filter.getPage();
		final int itemsperpage = filter.getItemsPerPage();

		if (!filter.isOnlyUnreferenced() && ((filter.getSurveyUid() != null && filter.getSurveyUid().length() > 0)
				|| (filter.getSurveyShortname() != null && filter.getSurveyShortname().length() > 0))) {
			// search database for files
			return getFilesForSurvey(filter, page, itemsperpage, archivedir);
		} else {
			// search file system
			final MutableInteger counter = new MutableInteger(0);
			final int skip = page > 1 ? (page - 1) * itemsperpage : 0;

			if (filter.isSystemExports() || filter.isSurveyUploads() || filter.isTemporaryFiles()
					|| filter.isUnknownFiles()) {
				try {
					Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs)
								throws IOException {
							if (file.equals(dir) || file.equals(filedir)
									|| (file.equals(archivedir) && filter.isArchivedSurveys())) {
								return FileVisitResult.CONTINUE;
							} else if (filter.isTemporaryFiles() && !file.equals(archivedir)) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.SKIP_SUBTREE;
							}
						}

						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (!file.toFile().isDirectory()) {
								FileResult fileResult = getFileResult(file, null, archivedir);
								if (checkResult(fileResult, filter)) {
									if (skip - counter.getValue() > 0) {
										counter.setValue(counter.getValue() + 1);
									} else {
										result.add(fileResult);
									}
									if (result.size() >= itemsperpage) {
										return FileVisitResult.TERMINATE;
									}
								}
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
							// Skip folders that can't be traversed
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
					return result;
				}
			} else if (filter.isArchivedSurveys()) {
				try {
					Files.walkFileTree(archivedir, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

							FileResult fileResult = getFileResult(file, null, archivedir);
							if (checkResult(fileResult, filter)) {
								if (skip - counter.getValue() > 0) {
									counter.setValue(counter.getValue() + 1);
								} else {
									result.add(fileResult);
								}
								if (result.size() >= itemsperpage) {
									return FileVisitResult.TERMINATE;
								}
							}

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
							// Skip folders that can't be traversed
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage(), e);
					return result;
				}
			}
		}

		return result;
	}

	private boolean checkResult(FileResult fileResult, FileFilter filter) {
		if (!filter.isSystemExports() && fileResult.getFileType() != null
				&& "results,statistics,charts,tokens,contacts,activities".contains(fileResult.getFileType())) {
			if (fileResult.isArchive() && filter.isArchivedSurveys()) {
				// ok
			} else {
				return false;
			}
		}

		if (!filter.isSurveyUploads() && fileResult.getFileType() != null
				&& fileResult.getFileType().equals("uploaded file")) {
			return false;
		}

		if (!filter.isUnknownFiles()
				&& (fileResult.getSurveyUid() == null || fileResult.getSurveyUid().length() == 0)) {
			return false;
		}

		if (filter.getSurveyUid() != null && filter.getSurveyUid().length() > 0
				&& !fileResult.getSurveyUid().contains(filter.getSurveyUid())) {
			return false;
		}

		if (filter.getSurveyShortname() != null && filter.getSurveyShortname().length() > 0
				&& !fileResult.getSurveyShortname().contains(filter.getSurveyShortname())) {
			return false;
		}

		if (filter.getFilePath() != null && filter.getFilePath().length() > 0
				&& !fileResult.getFilePath().contains(filter.getFilePath())) {
			return false;
		}

		if (filter.getFileName() != null && filter.getFileName().length() > 0
				&& !fileResult.getFileName().contains(filter.getFileName())) {
			return false;
		}

		if (filter.getFileUid() != null && filter.getFileUid().length() > 0
				&& (fileResult.getFileUid() == null || !fileResult.getFileUid().contains(filter.getFileUid()))) {
			return false;
		}

		if (filter.getFileTypes() != null && !filter.getFileTypes().isEmpty()) {
			boolean found = false;
			for (String type : filter.getFileTypes()) {
				if (type.equals(fileResult.getFileType())) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}

		if (filter.getFileExtensions() != null && !filter.getFileExtensions().isEmpty()) {
			boolean found = false;
			for (String ext : filter.getFileExtensions()) {
				if (ext.equals(fileResult.getFileExtension()) || (ext.equalsIgnoreCase("OTHER")
						&& !Arrays.asList(fileextensions).contains(fileResult.getFileExtension()))) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}

		if (filter.getCreatedFrom() != null && filter.getCreatedFrom().after(fileResult.getCreated())) {
			logger.info("file filtered out by creation date: "
					+ Tools.formatDate(filter.getCreatedFrom(), ConversionTools.DateFormat) + " : "
					+ Tools.formatDate(fileResult.getCreated(), ConversionTools.DateFormat));
			return false;
		}

		if (filter.getCreatedTo() != null && filter.getCreatedTo().before(fileResult.getCreated())) {
			return false;
		}

		if (filter.isOnlyUnreferenced() && fileResult.getSurveyShortname() != null && fileResult.getSurveyShortname().length() > 0) {
			return false;
		}

		return !(filter.getFilterApplied() != null && (fileResult.getFilterApplied() == null
				|| !filter.getFilterApplied().equalsIgnoreCase(fileResult.getFilterApplied())));
	}

	private boolean add(FileResult item, Map<String, FileResult> map) {
		if (item.getFileUid() != null && map.containsKey(item.getFileUid())) {
			return false;
		} else if (item.getFileUid() != null) {
			map.put(item.getFileUid(), item);
		} else {
			map.put(UUID.randomUUID().toString(), item);
		}
		return true;
	}

	private List<FileResult> getFilesForSurvey(FileFilter filter, int page, int itemsperpage, Path archivedir)
			throws Exception {
		List<Integer> ids = surveyService.getAllSurveyVersions(filter.getSurveyShortname(), filter.getSurveyUid());
		Map<String, FileResult> result = new HashMap<>();

		int counter = 0;
		int skip = 0;
		if (page > 1)
			skip = (page - 1) * itemsperpage;

		for (int id : ids) {
			// exports
			if (filter.isSystemExports()) {
				List<Export> exports = exportService.getSurveyExports(id);
				for (Export export : exports) {
					boolean skipexport = false;
					switch (export.getType()) {
					case Content:
						if (!filter.isVisible("results")) {
							skipexport = true;
						}
						break;
					case Statistics:
						if (!filter.isVisible("statistics")) {
							skipexport = true;
						}
						break;
					case Charts:
						if (!filter.isVisible("charts")) {
							skipexport = true;
						}
						break;
					case Activity:
						if (!filter.isVisible("activities")) {
							skipexport = true;
						}
						break;
					case AddressBook:
						if (!filter.isVisible("contacts")) {
							skipexport = true;
						}
						break;
					case Tokens:
						if (!filter.isVisible("tokens")) {
							skipexport = true;
						}
						break;
					default:
						break;
					}
					if (!skipexport) {
						String filePath = exportService.getTempExportFilePath(export, null);
						Path file = Paths.get(filePath);
						if (file.toFile().exists() && filter.isValidExtension(export.getFormat().toString().toUpperCase())) {
							FileResult fresult = getFileResult(file, export, archivedir);
							if (checkResult(fresult, filter)) {
								if (skip - counter > 0) {
									counter++;
								} else {
									add(fresult, result);
								}
								if (result.size() >= itemsperpage) {
									return new ArrayList<>(result.values());
								}
							}
						}
					}
				}
			}

			Survey survey = surveyService.getSurvey(id);
			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());

			// contribution pdfs
			if (filter.isVisible("contribution") && filter.isValidExtension("PDF")) {
				ResultFilter r = new ResultFilter();
				r.setSurveyId(id);
				if (filter.getCreatedFrom() != null) {
					r.setGeneratedFrom(filter.getCreatedFrom());
				}
				boolean stop = false;
				int answerpage = 0;

				while (!stop) {
					Set<String> caseids = answerService.getCaseIds(id, r, answerpage++, 1000, false);
					if (caseids != null && !caseids.isEmpty()) {
						logger.debug(
								"found case ids for survey " + id + ": " + StringUtils.join(caseids.toArray(), ","));
					}
					if (caseids != null && caseids.size() < 1000)
						stop = true;

					for (String caseid : caseids) {
						String filePath = String.format("%s/answer%s.pdf", folder.getPath(), caseid);

						Path file = Paths.get(filePath);
						if (file.toFile().exists()) {
							FileResult fresult = getFileResult(file, null, archivedir);
							if (checkResult(fresult, filter)) {
								if (skip - counter > 0) {
									counter++;
								} else {
									add(fresult, result);
								}
								if (result.size() >= itemsperpage) {
									return new ArrayList<>(result.values());
								}
							}
						}
					}
				}
			}

			// survey images, logos, download, background documents
			if (filter.isVisible("logo") && survey.getLogo() != null) {
				String uid = survey.getLogo().getUid();
				Path file = getSurveyFile(survey.getUniqueId(), uid).toPath();
				if (file.toFile().exists()) {
					FileResult fresult = getFileResult(file, null, archivedir);
					if (checkResult(fresult, filter)) {
						if (skip - counter > 0) {
							counter++;
						} else {
							add(fresult, result);
						}
						if (result.size() >= itemsperpage) {
							return new ArrayList<>(result.values());
						}
					}
				}
			}

			if (filter.isVisible("background document")) {
				for (String key : survey.getBackgroundDocuments().keySet()) {
					String fileUID = getFileUIDFromUrl(survey.getBackgroundDocuments().get(key));
					Path file = getSurveyFile(survey.getUniqueId(), fileUID).toPath();

					if (file.toFile().exists()) {
						FileResult fresult = getFileResult(file, null, archivedir);
						if (checkResult(fresult, filter)) {
							if (skip - counter > 0) {
								counter++;
							} else {
								add(fresult, result);
							}
							if (result.size() >= itemsperpage) {
								return new ArrayList<>(result.values());
							}
						}
					}
				}
			}

			if (filter.isVisible("download") || filter.isVisible("image")) {
				for (Element question : survey.getElements()) {
					if (filter.isVisible("download") && question instanceof Download) {
						Download download = (Download) question;
						for (File f : download.getFiles()) {
							Path file = getSurveyFile(survey.getUniqueId(), f.getUid()).toPath();

							if (file.toFile().exists()) {
								FileResult fresult = getFileResult(file, null, archivedir);
								if (checkResult(fresult, filter)) {
									if (skip - counter > 0) {
										counter++;
									} else {
										add(fresult, result);
									}
									if (result.size() >= itemsperpage) {
										return new ArrayList<>(result.values());
									}
								}
							}
						}
					} else if (filter.isVisible("image") && question instanceof Image) {
						Image image = (Image) question;
						String fileUID = getFileUIDFromUrl(image.getUrl());

						Path file = getSurveyFile(survey.getUniqueId(), fileUID).toPath();
						if (file.toFile().exists()) {
							FileResult fresult = getFileResult(file, null, archivedir);
							if (checkResult(fresult, filter)) {
								if (skip - counter > 0) {
									counter++;
								} else {
									add(fresult, result);
								}
								if (result.size() >= itemsperpage) {
									return new ArrayList<>(result.values());
								}
							}
						}
					} else if (filter.isVisible("image") && question instanceof GalleryQuestion) {
						GalleryQuestion gallery = (GalleryQuestion) question;
						for (File f : gallery.getFiles()) {
							Path file = getSurveyFile(survey.getUniqueId(), f.getUid()).toPath();

							if (file.toFile().exists()) {
								FileResult fresult = getFileResult(file, null, archivedir);
								if (checkResult(fresult, filter)) {
									if (skip - counter > 0) {
										counter++;
									} else {
										add(fresult, result);
									}
									if (result.size() >= itemsperpage) {
										return new ArrayList<>(result.values());
									}
								}
							}
						}
					}
				}
			}

			// survey pdf
			if (filter.isVisible("survey") && filter.isValidExtension("PDF")) {
				for (String lang : translationService.getTranslationLanguagesForSurvey(id, false)) {
					String filePath = String.format("%s/survey%s%s.pdf", folder.getPath(), id, lang);
					Path path = Paths.get(filePath);
					if (path.toFile().exists()) {
						FileResult fresult = getFileResult(path, null, archivedir);
						if (checkResult(fresult, filter)) {
							if (skip - counter > 0) {
								counter++;
							} else {
								add(fresult, result);
							}
							if (result.size() >= itemsperpage) {
								return new ArrayList<>(result.values());
							}
						}
					}
				}
			}

			// uploaded files
			if (filter.isSurveyUploads()) {
				ResultFilter r = new ResultFilter();
				r.setSurveyId(id);
				if (filter.getCreatedFrom() != null) {
					r.setGeneratedFrom(filter.getCreatedFrom());
				}
				boolean stop = false;
				int answerpage = 0;
				while (!stop) {
					List<File> files = answerService.getAllUploadedFiles(id, r, answerpage++, 1000);
					if (files.size() < 1000)
						stop = true;

					for (File f : files) {
						Path file = getSurveyFile(survey.getUniqueId(), f.getUid()).toPath();
						if (file.toFile().exists()) {
							FileResult fresult = getFileResult(file, null, archivedir);
							fresult.setSurveyShortname(survey.getShortname());
							fresult.setSurveyUid(survey.getUniqueId());
							fresult.setFileType("uploaded file");
							if (checkResult(fresult, filter)) {
								if (skip - counter > 0) {
									counter++;
								} else {
									add(fresult, result);
								}
								if (result.size() >= itemsperpage) {
									return new ArrayList<>(result.values());
								}
							}
						}
					}
				}
			}
		}

		return new ArrayList<>(result.values());
	}

	public FileResult getFileResult(Path file, Export export, Path archivedir) throws IOException {

		FileResult fileResult = new FileResult();
		fileResult.setFilePath(file.toAbsolutePath().toString());
		if (file.toFile().exists()) {
			fileResult.setFileSize(ConversionTools.getStringForBytes(Files.size(file)));
			fileResult.setCreated(new Date(Files.getLastModifiedTime(file).toMillis()));
		}

		String name = file.getFileName().toString();
		fileResult.setFileName(name); // default
		fileResult.setSurveyShortname("");
		fileResult.setSurveyUid("");
		fileResult.setFileType("");
		fileResult.setFileExtension("");

		if (name.startsWith(Constants.ANSWER) && name.endsWith(".pdf")) {
			fileResult.setFileName(name);
			fileResult.setFileExtension("PDF");
			fileResult.setFileType("contribution");
			String caseid = name.substring(6).replace(".pdf", "");
			AnswerSet answerSet = answerService.get(caseid);
			if (answerSet != null) {
				fileResult.setSurveyUid(answerSet.getSurvey().getUniqueId());
				fileResult.setSurveyShortname(answerSet.getSurvey().getShortname());
			} else {
				fileResult.setError("unknown contribution id");
			}
		} else if (name.startsWith("draft") && name.endsWith(".pdf")) {
			fileResult.setFileName(name);
			fileResult.setFileExtension("PDF");
			fileResult.setFileType("draft");
			String draftid = name.substring(5).replace(".pdf", "");
			Draft draft = answerService.getDraft(draftid);
			if (draft != null) {
				fileResult.setSurveyUid(draft.getAnswerSet().getSurvey().getUniqueId());
				fileResult.setSurveyShortname(draft.getAnswerSet().getSurvey().getShortname());
			} else {
				fileResult.setError("unknown draft id");
			}
		} else if (name.startsWith("survey") && name.endsWith(".pdf")) {
			fileResult.setFileName(name);
			fileResult.setFileExtension("PDF");
			fileResult.setFileType("survey");
			// the name pattern is survey[id][lang].pdf
			String surveyid = name.substring(6).replace(".pdf", "");
			surveyid = surveyid.substring(0, surveyid.length() - 2);
			try {
				Survey survey = surveyService.getSurvey(Integer.parseInt(surveyid));
				if (survey != null) {
					fileResult.setSurveyUid(survey.getUniqueId());
					fileResult.setSurveyShortname(survey.getShortname());
				} else {
					fileResult.setError("unknown survey id");
				}
			} catch (NumberFormatException e) {
				// invalid file
				fileResult.setError("wrong file pattern");
			}
		} else if (name.startsWith("Export")) {
			fileResult.setFileName(name);
			fileResult.setFileExtension(name.substring(name.lastIndexOf('.') + 1).toUpperCase());
			String exportid = name.substring(6);
			exportid = exportid.substring(0, exportid.lastIndexOf('.'));

			// special case: zip archive for exports with uploaded files
			if (exportid.endsWith(".xls")) {
				exportid = exportid.substring(0, exportid.lastIndexOf('.'));
			}

			try {
				if (export == null)
					export = exportService.get(Integer.parseInt(exportid), false);
				if (export != null) {
					if (export.getSurvey() != null) {
						fileResult.setSurveyUid(export.getSurvey().getUniqueId());
						fileResult.setSurveyShortname(export.getSurvey().getShortname());
					}

					if (export.getResultFilter() != null) {
						if (export.getResultFilter().isEmpty()) {
							fileResult.setFilterApplied("No");
						} else {
							fileResult.setFilterApplied("Yes");
						}
					}

					switch (export.getType()) {
					case Activity:
						fileResult.setFileType("activities");
						break;
					case AddressBook:
						fileResult.setFileType("contacts");
						break;
					case Statistics:
						fileResult.setFileType("statistics");
						break;
					case Tokens:
						fileResult.setFileType("tokens");
						break;
					case Content:
						fileResult.setFileType("results");
						break;
					default:
						break;
					}

				} else {
					fileResult.setError("unknown export id");
				}
			} catch (NumberFormatException e) {
				// invalid file
				fileResult.setError("wrong file pattern");
			}
		} else if (archivedir != null && file.startsWith(archivedir)) {
			fileResult.setFileName(name);

			String uid = name.substring(0, 36);
			fileResult.setSurveyUid(uid);
			fileResult.setArchive(true);

			if (name.endsWith("results.xls")) {
				fileResult.setFileExtension("XLS");
				fileResult.setFileType("results");
			} else if (name.endsWith("results.xls.zip")) {
				fileResult.setFileExtension("ZIP");
				fileResult.setFileType("results");
			} else if (name.endsWith("statistics.xls")) {
				fileResult.setFileExtension("XLS");
				fileResult.setFileType("statistics");
			} else if (name.endsWith("statistics.pdf")) {
				fileResult.setFileExtension("PDF");
				fileResult.setFileType("statistics");
			} else if (name.endsWith(".pdf")) {
				fileResult.setFileExtension("PDF");
				fileResult.setFileType("survey");
			} else {
				fileResult.setFileType("archive");
			}

		} else {

			try {
				File f = get(name);
				fileResult.setFileName(f.getName());
				fileResult.setFileExtension(f.getName().substring(f.getName().lastIndexOf('.') + 1).toUpperCase());
				fileResult.setFileUid(f.getUid());

				String[] surveyData = surveyService.getSurveyForFile(f, contextpath, null);
				if (surveyData != null) {
					fileResult.setSurveyShortname(surveyData[1]);
					fileResult.setSurveyUid(surveyData[0]);
					fileResult.setFileType(surveyData[2]);
				}

			} catch (FileNotFoundException e) {
				fileResult.setError("unknown file uid");
			}

		}

		return fileResult;
	}

	public void cleanup(String[] options, Date pdfbefore, Date tempbefore, String email) {
		CleanupWorker c = (CleanupWorker) context.getBean("cleanupWorker");
		c.init(options, pdfbefore, tempbefore, email);
		getPool().execute(c);
	}

	public Set<java.io.File> getFilesForSurveys(List<Integer> surveyIDs, boolean onlySurveyFiles) throws Exception {
		Set<java.io.File> result = new HashSet<>();

		if (!onlySurveyFiles) {

			// get exports
			for (int surveyID : surveyIDs) {
				List<Export> exports = exportService.getSurveyExports(surveyID);
				for (Export export : exports) {
					java.io.File file = fileService.getSurveyExportFile(export.getSurvey().getUniqueId(),
							export.getId(), export.getFormat().toString());
					if (file.exists()) {
						result.add(file);
					} else {
						String filePath = String.format("%sExport%s.%s", tempFileDir, export.getId(),
								export.getFormat());
						file = new java.io.File(filePath);
						result.add(file);
					}

					if (export.getZipped()) {
						file = fileService.getSurveyExportFile(export.getSurvey().getUniqueId(), export.getId(),
								export.getFormat().toString() + ".zip");
						if (file.exists()) {
							result.add(file);
						} else {
							String filePath = String.format("%sExport%s.%s.zip", tempFileDir, export.getId(),
									export.getFormat());
							file = new java.io.File(filePath);
							result.add(file);
						}
					}
				}
			}

			// get contribution pdfs
			for (int surveyID : surveyIDs) {
				Survey survey = surveyService.getSurvey(surveyID);

				ResultFilter r = new ResultFilter();
				r.setSurveyId(surveyID);
				boolean stop = false;
				int answerpage = 0;
				while (!stop) {
					Set<String> caseids = answerService.getCaseIds(surveyID, r, answerpage++, 1000, false);
					if (caseids.size() < 1000)
						stop = true;

					for (String caseid : caseids) {
						java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
						java.io.File file = new java.io.File(
								String.format("%s/answer%s.pdf", folder.getPath(), caseid));

						if (!file.exists()) {
							String filePath = String.format("%sanswer%s.pdf", tempFileDir, caseid);
							file = new java.io.File(filePath);
						}
						result.add(file);
					}
				}
			}
		}

		// get survey files
		for (int surveyID : surveyIDs) {
			Survey survey = surveyService.getSurvey(surveyID);

			if (!onlySurveyFiles) {
				for (String lang : translationService.getTranslationLanguagesForSurvey(surveyID, false)) {
					java.io.File file = fileService.getSurveyPDFFile(survey.getUniqueId(), survey.getId(), lang);
					result.add(file);
				}
			}

			if (survey.getLogo() != null) {
				String uid = survey.getLogo().getUid();

				java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), uid);
				if (!file.exists()) {
					String filePath = String.format("%s%s", fileDir, uid);
					file = new java.io.File(filePath);
				}
				result.add(file);
			}

			for (String key : survey.getBackgroundDocuments().keySet()) {
				String fileUID = getFileUIDFromUrl(survey.getBackgroundDocuments().get(key));

				java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), fileUID);
				if (!file.exists()) {
					String filePath = String.format("%s%s", fileDir, fileUID);
					file = new java.io.File(filePath);
				}
				result.add(file);
			}

			for (Element question : survey.getElements()) {
				if (question instanceof Download) {
					Download download = (Download) question;
					for (File f : download.getFiles()) {
						java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), f.getUid());
						if (!file.exists()) {
							String filePath = String.format("%s%s", fileDir, f.getUid());
							file = new java.io.File(filePath);
						}

						result.add(file);
					}
				} else if (question instanceof Image) {
					Image image = (Image) question;
					if (image.getUrl() != null && !image.getUrl().contains("photo_scenery.png")) {
						String fileUID = getFileUIDFromUrl(image.getUrl());

						java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), fileUID);
						if (!file.exists()) {
							String filePath = String.format("%s%s", fileDir, fileUID);
							file = new java.io.File(filePath);
						}

						result.add(file);
					}
				} else if (question instanceof GalleryQuestion) {
					GalleryQuestion gallery = (GalleryQuestion) question;
					for (File f : gallery.getFiles()) {
						java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), f.getUid());
						if (!file.exists()) {
							String filePath = String.format("%s%s", fileDir, f.getUid());
							file = new java.io.File(filePath);
						}

						result.add(file);
					}
				}
			}

			if (!onlySurveyFiles) {
				// uploaded files
				ResultFilter r = new ResultFilter();
				r.setSurveyId(surveyID);
				boolean stop = false;
				int answerpage = 0;
				while (!stop) {
					List<File> files = answerService.getAllUploadedFiles(surveyID, r, answerpage++, 1000);
					if (files.size() < 1000)
						stop = true;

					for (File f : files) {
						java.io.File file = fileService.getSurveyFile(survey.getUniqueId(), f.getUid());
						if (!file.exists()) {
							String filePath = String.format("%s%s", fileDir, f.getUid());
							file = new java.io.File(filePath);
						}

						result.add(file);
					}
				}
			}
		}

		return result;
	}

	public int deleteFilesForSurveys(List<Integer> surveyIDs) throws Exception {
		int deletecounter = 0;

		Set<java.io.File> files = getFilesForSurveys(surveyIDs, false);
		for (java.io.File file : files) {
			if (file.exists() && file.delete()) {
				deletecounter++;
			}
		}

		return deletecounter;
	}

	public int deleteFilesForArchivedSurveys() throws Exception {

		logger.info("starting deleteFilesForArchivedSurveys: " + ConversionTools.getFullString(new Date()));

		ArchiveFilter filter = new ArchiveFilter();
		filter.setFinished(true);
		List<Archive> archives = archiveService.getAllArchives(filter, 1, Integer.MAX_VALUE, false);
		int deletecounter = 0;

		for (Archive a : archives) {
			List<Integer> surveyIDs = surveyService.getAllSurveyVersions(a.getSurveyShortname(), a.getSurveyUID());
			deletecounter += deleteFilesForSurveys(surveyIDs);
		}

		logger.info("finish deleteFilesForArchivedSurveys: " + deletecounter + " files deleted "
				+ ConversionTools.getFullString(new Date()));

		return deletecounter;
	}

	public int deleteFilesForDeletedElements() throws Exception {
		// all files that have no link in the database
		FileFilter filter = new FileFilter();
		filter.setPage(1);
		filter.setItemsPerPage(10000);
		filter.setOnlyUnreferenced(true);
		boolean stop = false;
		int deletecounter = 0;
		while (!stop) {
			List<FileResult> results = getFiles(filter);
			if (results.size() < 100)
				stop = true;

			for (FileResult result : results) {
				java.io.File file = new java.io.File(result.getFilePath());
				if (file.delete()) {
					deletecounter++;
				} else {
					throw new MessageException("not possible to delete file " + result.getFilePath());
				}
			}
		}

		return deletecounter;
	}

	public int deleteContributions(Date pdfbefore) throws IOException {
		logger.info("starting deleteContributions: " + ConversionTools.getFullString(new Date()));

		int deletecounter = 0;

		java.io.File dir = new java.io.File(tempFileDir);
		java.io.FileFilter fileFilter = new WildcardFileFilter("answer*.pdf");
		java.io.File[] files = dir.listFiles(fileFilter);
		if (files != null) {
			for (java.io.File file : files) {
				if (file.exists()) {
					Date modified = new Date(file.lastModified());
					if (modified.before(pdfbefore)) {
						Files.delete(file.toPath());						
					}
				}
			}

		}
		logger.info("finish deleteContributions: " + deletecounter + " files deleted "
				+ ConversionTools.getFullString(new Date()));

		return deletecounter;
	}

	public int deleteTemporaryFiles(final Date tempbefore) throws IOException {
		logger.info("starting deleteContributions: " + ConversionTools.getFullString(new Date()));
		final MutableInteger deletecounter = new MutableInteger(0);

		final Path dir = Paths.get(tempFileDir);
		final Path filedir = Paths.get(fileDir);
		final Path archivedir = Paths.get(archiveFileDir);

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path p, BasicFileAttributes attrs) throws IOException {
				if (p.toFile().isDirectory() && !p.equals(dir) && !p.equals(filedir) && !p.equals(archivedir)) {
					java.io.File candidate = p.toFile();
					Date modified = new Date(candidate.lastModified());
					if (modified.before(tempbefore)) {
						for (java.io.File file : candidate.listFiles()) {
							if (file.isFile()) {
								if (file.delete()) {
									deletecounter.setValue(deletecounter.getValue() + 1);
								} else {
									logger.error("not possible to delete file " + file.getAbsolutePath());
								}
							} else {
								for (java.io.File file2 : file.listFiles()) {
									if (file2.delete()) {
										deletecounter.setValue(deletecounter.getValue() + 1);
									} else {
										logger.error("not possible to delete file " + file.getAbsolutePath());
									}
								}
								if (!file.delete()) {
									logger.error(
											"not possible to delete folder " + candidate.getAbsolutePath());
								}
							}
						}
						if (!candidate.delete()) {
							logger.error("not possible to delete folder " + candidate.getAbsolutePath());
						}
					}					
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// Skip folders that can't be traversed
				return FileVisitResult.CONTINUE;
			}
		});

		logger.info("finish deleteContributions: " + deletecounter.getValue() + " files deleted "
				+ ConversionTools.getFullString(new Date()));
		return deletecounter.getValue();
	}

	public void createDummyFiles(int files) throws IOException {

		// create unreferenced dummy files
		String uid = UUID.randomUUID().toString();
		for (int i = 0; i < files; i++) {
			java.io.File file = new java.io.File(tempFileDir + uid + i);
			FileUtils.writeStringToFile(file, "dummy");
		}
	}

	public void startExport(FileFilter filter, String[] files, User user) {
		FileExportCreator fileExportCreator = (FileExportCreator) context.getBean("fileExportCreator");
		fileExportCreator.init(filter, files, user);
		getPool().execute(fileExportCreator);
	}

	public void recreateAll(FileFilter filter, String[] files, User user) {
		RecreateWorker c = (RecreateWorker) context.getBean("recreateWorker");
		c.init(files, filter, user.getEmail());
		getPool().execute(c);
	}

	public int deleteAll(FileFilter filter, String[] files) throws Exception {
		int counter = 0;

		if (files == null) {
			List<FileResult> fileresults = getFiles(filter);
			for (FileResult fileresult : fileresults) {
				java.io.File source = new java.io.File(fileresult.getFilePath());
				if (source.exists() && source.delete()) {
					counter++;
				}
			}
		} else {
			for (String path : files) {
				java.io.File source = new java.io.File(path);
				if (source.exists() && source.delete()) {
					counter++;
				}
			}
		}
		return counter;
	}

	public boolean recreate(java.io.File file, Locale locale, MessageSource resources)
			throws IOException {
		// exports and contribution/survey pdfs can be recreated
		String name = file.getName();

		if (name.startsWith(Constants.ANSWER) && name.endsWith(".pdf")) {
			String uid = name.substring(6).replace(".pdf", "");
			if (file.delete()) {
				AnswerSet answerSet = answerService.get(uid);
				if (answerSet != null)
					pdfService.createAnswerPDF(null, uid, answerSet.getSurvey().getUniqueId(), answerSet.getIsDraft());
				return true;
			}
		} else if (name.startsWith("draft") && name.endsWith(".pdf")) {
			String uid = name.substring(5).replace(".pdf", "");
			if (file.delete()) {
				Draft draft = answerService.getDraft(uid);
				if (draft != null)
					pdfService.createAnswerPDF(null, uid, draft.getAnswerSet().getSurvey().getUniqueId(),
							draft.getAnswerSet().getIsDraft());
				return true;
			}
		} else if (name.startsWith("survey") && name.endsWith(".pdf")) {
			String language = name.substring(name.length() - 6, name.length() - 4);
			String id = name.substring(6);
			id = id.substring(0, id.length() - 6);
			if (file.delete()) {
				Survey survey = surveyService.getSurvey(Integer.parseInt(id));
				pdfService.createSurveyPDF(survey, language, file);
				return true;
			}
		} else if (name.startsWith("Export")) {
			String exportid = file.getName().substring(6);
			exportid = exportid.substring(0, exportid.indexOf('.'));
			Export export = exportService.get(Integer.parseInt(exportid), false);
			if (export != null) {
				exportService.recreateExport(export, locale, resources);
				return true;
			}
		}

		return false;
	}

	@Transactional(readOnly = false)
	public void deleteIfNotReferenced(String fileuid, String surveyuid) throws IOException {
		try {
			List<File> fs = getAll(fileuid);
			if (fs.size() > 1) {
				// this means multiple surveys use this file
				return;
			}

			if (fs.size() == 1) {
				String[] surveydata = surveyService.getSurveyForFile(fs.get(0), contextpath, surveyuid);
				if (surveydata == null) {
					delete(fs.get(0));
				} else {
					return;
				}
			}
		} catch (Exception fnf) {
			// file does not exist
		}

		java.io.File file = fileService.getSurveyFile(surveyuid, fileuid);
		Files.deleteIfExists(file.toPath());
		
		file = new java.io.File(fileDir + fileuid);
		Files.deleteIfExists(file.toPath());
	}

	public Map<String, String> getMissingFiles(String uniqueId) throws Exception {
		final Path archivedir = Paths.get(archiveFileDir);

		Map<String, String> result = new HashMap<>();

		List<Integer> ids = surveyService.getAllSurveyVersions(null, uniqueId);

		Set<java.io.File> files = getFilesForSurveys(ids, true);
		for (java.io.File f : files) {
			if (!f.exists()) {
				FileResult fresult = getFileResult(f.toPath(), null, archivedir);
				result.put(fresult.getFileName(), fresult.getFileType());
			}
		}

		return result;

	}

	public File copyFile(String uid, String surveyUID) throws IOException {
		File file = fileService.get(uid);

		java.io.File folder = getSurveyFilesFolder(surveyUID);

		File copy = file.copy(folder.getPath());

		String newuid = UUID.randomUUID().toString();

		copy.setUid(newuid);
		fileService.add(copy);

		java.io.File f = getSurveyFile(surveyUID, uid);

		if (!f.exists()) {
			f = new java.io.File(fileDir + uid);
			if (f.exists())
				fileService.logOldFileSystemUse(fileDir + uid);
		}

		if (f.exists()) {
			Files.copy(f.toPath(), getSurveyFile(surveyUID, newuid).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		return copy;
	}

	/////////////// new file system ///////////////////////////

	public java.io.File getSurveyFolder(String surveyUID) {
		java.io.File folder = new java.io.File(surveysDir + surveyUID.substring(0, 1) + "/" + surveyUID + "/");
		if (!folder.exists())
			folder.mkdirs();
		return folder;
	}

	public java.io.File getSurveyFilesFolder(String surveyUID) {
		java.io.File folder = new java.io.File(surveysDir + surveyUID.substring(0, 1) + "/" + surveyUID + "/FILES/");
		if (!folder.exists())
			folder.mkdirs();
		return folder;
	}

	public java.io.File getSurveyExportsFolder(String surveyUID) {
		return getSurveyExportsFolder(surveyUID, true);
	}

	public java.io.File getSurveyExportsFolder(String surveyUID, boolean create) {
		java.io.File folder = new java.io.File(surveysDir + surveyUID.substring(0, 1) + "/" + surveyUID + "/EXPORTS/");
		if (!folder.exists() && create)
			folder.mkdirs();
		return folder;
	}

	public java.io.File getSurveyUploadsFolder(String surveyUID, boolean create) {
		java.io.File folder = new java.io.File(surveysDir + surveyUID.substring(0, 1) + "/" + surveyUID + "/UPLOADS/");
		if (!folder.exists() && create)
			folder.mkdirs();
		return folder;
	}

	public java.io.File getSurveyFile(String surveyUID, String fileUID) {
		java.io.File folder = getSurveyFilesFolder(surveyUID);
		return new java.io.File(folder.getPath() + "/" + fileUID);
	}

	public java.io.File getSurveyExportFile(String surveyUID, Integer id, String format) {
		java.io.File folder = getSurveyExportsFolder(surveyUID);
		return new java.io.File(String.format("%s/Export%s.%s", folder.getPath(), id, format));
	}

	public java.io.File getSurveyExportFile(String surveyUID, String fileUID) {
		return getSurveyExportFile(surveyUID, fileUID, true);
	}

	public java.io.File getSurveyExportFile(String surveyUID, String fileUID, boolean create) {
		java.io.File folder = getSurveyExportsFolder(surveyUID, create);
		return new java.io.File(folder.getPath() + "/" + fileUID);
	}

	public java.io.File getSurveyUploadFile(String surveyUID, String fileUID) {
		java.io.File folder = getSurveyUploadsFolder(surveyUID, false);
		return new java.io.File(folder.getPath() + "/" + fileUID);
	}

	public java.io.File getSurveyPDFFile(String surveyUID, Integer surveyID, String lang) {
		java.io.File folder = fileService.getSurveyExportsFolder(surveyUID);
		return new java.io.File(String.format("%s/survey%s%s.pdf", folder.getPath(), surveyID, lang));
	}

	public java.io.File getLocalTemporaryFile() throws IOException {
		return java.io.File.createTempFile("temp", "");
	}

	public java.io.File getTemporaryFile() {
		return getTemporaryFile(UUID.randomUUID().toString());
	}

	public java.io.File getTemporaryFile(String uid) {
		java.io.File folder = fileService.getUsersFolder(0, true);
		return new java.io.File(String.format("%s/%s", folder.getPath(), uid));
	}

	public java.io.File getUsersFolder(int userId) {
		return getUsersFolder(userId, true);
	}

	public java.io.File getUsersFolder(int userId, boolean create) {
		int userfolderNumber = (userId / 1000) * 1000;
		String userfolder = "Users";
		if (userfolderNumber > 0)
			userfolder += userfolderNumber;

		java.io.File folder = new java.io.File(usersDir + userfolder + "/" + userId + "/");

		if (!folder.exists() && !create)
			return null;

		if (!folder.exists())
			folder.mkdirs();
		return folder;
	}

	public java.io.File getUsersFile(int userId, String name) {
		java.io.File folder = fileService.getUsersFolder(userId);
		return new java.io.File(String.format("%s/%s", folder.getPath(), name));
	}

	public java.io.File getArchiveFolder(String surveyUID) {
		java.io.File folder = new java.io.File(archiveDir + surveyUID.substring(0, 1) + "/" + surveyUID + "/");
		if (!folder.exists())
			folder.mkdirs();
		return folder;
	}

	public java.io.File getArchiveFile(String surveyUID, String name) {
		java.io.File folder = getArchiveFolder(surveyUID);
		return new java.io.File(folder.getPath() + "/" + name);
	}

	private boolean migrateSurveyFile(String surveyuid, String fileUID) throws IOException {
		java.io.File original = new java.io.File(fileDir + fileUID);
		if (original.exists()) {
			java.io.File copy = getSurveyFile(surveyuid, fileUID);
			Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		}
		return false;
	}

	@Transactional
	public void migrateAllSurveyFiles(Survey survey) throws Exception {
		migrateSurveyFiles(survey);

		List<Integer> ids = surveyService.getAllPublishedSurveyVersions(survey.getId());

		for (int id : ids) {
			Survey published = surveyService.getSurvey(id);
			migrateSurveyFiles(published);
		}
	}

	@Transactional
	public void migrateSurveyFiles(Survey survey) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		survey = (Survey) session.merge(survey);

		for (Element element : survey.getElements()) {
			if (element instanceof Image) {
				Image image = (Image) element;
				if (image.getUrl() != null && image.getUrl().length() > 0) {
					String fileUID = image.getUrl().replace(contextpath + "/files/", "");
					if (migrateSurveyFile(survey.getUniqueId(), fileUID)) {
						image.setUrl(
								servletContext.getContextPath() + "/files/" + survey.getUniqueId() + "/" + fileUID);
					}
				}
			} else if (element instanceof Download) {
				Download download = (Download) element;
				for (File file : download.getFiles()) {
					String fileUID = file.getUid();
					migrateSurveyFile(survey.getUniqueId(), fileUID);
				}
			} else if (element instanceof Confirmation) {
				Confirmation confirmation = (Confirmation) element;
				for (File file : confirmation.getFiles()) {
					String fileUID = file.getUid();
					migrateSurveyFile(survey.getUniqueId(), fileUID);
				}
			} else if (element instanceof GalleryQuestion) {
				GalleryQuestion gallery = (GalleryQuestion) element;
				for (File file : gallery.getFiles()) {
					String fileUID = file.getUid();
					migrateSurveyFile(survey.getUniqueId(), fileUID);
				}
			}
		}

		if (survey.getLogo() != null) {
			String fileUID = survey.getLogo().getUid();
			migrateSurveyFile(survey.getUniqueId(), fileUID);
		}

		for (String label : survey.getBackgroundDocuments().keySet()) {
			String url = survey.getBackgroundDocuments().get(label);
			String fileUID = url.replace(sessionService.getContextPath() + "/files/", "");
			if (migrateSurveyFile(survey.getUniqueId(), fileUID)) {
				survey.getBackgroundDocuments().put(label,
						sessionService.getContextPath() + "/files/" + survey.getUniqueId() + "/" + fileUID);
			}
		}

		ResultFilter filter = new ResultFilter();
		filter.setSurveyId(survey.getId());
		List<File> uploadedFiles = answerService.getAllUploadedFiles(survey.getId(), filter, 1, Integer.MAX_VALUE);

		for (File file : uploadedFiles) {
			java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
			if (!f.exists()) {
				f = new java.io.File(fileDir + file.getUid());
				if (f.exists()) {
					migrateSurveyFile(survey.getUniqueId(), file.getUid());
				}
			}
		}

		List<Export> exports = exportService.getSurveyExports(survey.getId());
		for (Export export : exports) {
			String filePath = exportService.getTempExportFilePath(export, null);

			if (export.getZipped() != null && export.getZipped()) {
				filePath += ".zip";
			}

			java.io.File copy = new java.io.File(filePath);

			if (!copy.exists()) {
				filePath = tempFileDir + copy.getName();
				java.io.File original = new java.io.File(filePath);

				if (original.exists())
					Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	@Transactional
	public void migrateAllUserFiles(int user) throws IOException {
		List<Export> exports = exportService.getExports(user, "", false, false, false);
		for (Export export : exports) {
			if (export.getSurvey() == null) {
				String filePath = exportService.getTempExportFilePath(export, null);

				if (export.getZipped() != null && export.getZipped()) {
					filePath += ".zip";
				}

				java.io.File copy = new java.io.File(filePath);

				if (!copy.exists()) {
					filePath = tempFileDir + copy.getName();
					java.io.File original = new java.io.File(filePath);

					if (original.exists())
						Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	@Transactional
	public void migrateAllUserFiles() throws IOException {
		List<Integer> userids = administrationService.getAllUserIDs();

		for (int userid : userids) {
			List<Export> exports = exportService.getExports(userid, "", false, false, false);
			for (Export export : exports) {
				if (export.getSurvey() == null) {
					String filePath = exportService.getTempExportFilePath(export, null);

					if (export.getZipped() != null && export.getZipped()) {
						filePath += ".zip";
					}

					java.io.File copy = new java.io.File(filePath);

					if (!copy.exists()) {
						filePath = tempFileDir + copy.getName();
						java.io.File original = new java.io.File(filePath);

						if (original.exists())
							Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
		}
	}

	@Transactional
	public void migrateAllArchiveFiles() throws Exception {
		List<Archive> archives = archiveService.getAllArchives(new ArchiveFilter(), 1, Integer.MAX_VALUE, true);

		for (Archive archive : archives) {
			java.io.File original = new java.io.File(archiveFileDir + archive.getSurveyUID());
			java.io.File folder = fileService.getArchiveFolder(archive.getSurveyUID());
			if (original.exists()) {
				java.io.File copy = new java.io.File(folder.getPath() + "/" + archive.getSurveyUID());
				Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			original = new java.io.File(archiveFileDir + archive.getSurveyUID() + ".pdf");
			if (original.exists()) {
				java.io.File copy = new java.io.File(folder.getPath() + "/" + archive.getSurveyUID() + ".pdf");
				Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			original = new java.io.File(archiveFileDir + archive.getSurveyUID() + "statistics.pdf");
			if (original.exists()) {
				java.io.File copy = new java.io.File(
						folder.getPath() + "/" + archive.getSurveyUID() + "statistics.pdf");
				Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			original = new java.io.File(archiveFileDir + archive.getSurveyUID() + "statistics.xls");
			if (original.exists()) {
				java.io.File copy = new java.io.File(
						folder.getPath() + "/" + archive.getSurveyUID() + "statistics.xls");
				Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			original = new java.io.File(archiveFileDir + archive.getSurveyUID() + "results.xls");
			if (original.exists()) {
				java.io.File copy = new java.io.File(folder.getPath() + "/" + archive.getSurveyUID() + "results.xls");
				Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			original = new java.io.File(archiveFileDir + archive.getSurveyUID() + "results.xls.zip");
			if (original.exists()) {
				java.io.File copy = new java.io.File(
						folder.getPath() + "/" + archive.getSurveyUID() + "results.xls.zip");
				Files.copy(original.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	public int deleteOldAnswerPDFs(String surveyUID, final Date before) throws IOException {
		java.io.File folder = getSurveyExportsFolder(surveyUID);
		Path dir = folder.toPath();
		final MutableInteger deletecounter = new MutableInteger(0);

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path p, BasicFileAttributes attrs) throws IOException {
				if (!p.toFile().isDirectory()) {
					java.io.File candidate = p.toFile();

					if ((candidate.getName().startsWith(Constants.ANSWER) || candidate.getName().startsWith("draft"))
							&& candidate.getName().endsWith(".pdf")) {
						Date modified = new Date(candidate.lastModified());
						if (modified.before(before) && candidate.isFile()) {
							if (candidate.delete()) {
								deletecounter.setValue(deletecounter.getValue() + 1);
							} else {
								logger.error("not possible to delete file " + candidate.getAbsolutePath());
							}
						}
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// Skip folders that can't be traversed
				return FileVisitResult.CONTINUE;
			}
		});

		return deletecounter.getValue();
	}

	public int deleteOldTemporaryFolders(Date before) throws IOException {
		List<String> surveyUIDs = surveyService.getAllSurveyUIDs(false);
		int deletecounter = 0;

		for (String surveyUID : surveyUIDs) {
			if (surveyUID != null && surveyUID.length() > 0) {
				java.io.File folder = getSurveyUploadsFolder(surveyUID, false);
				if (folder.exists()) {
					java.io.File[] fList = folder.listFiles();
					for (java.io.File file : fList) {
						if (file.isDirectory()) {
							Date modified = new Date(file.lastModified());
							if (modified.before(before)) {
								try {
									FileUtils.deleteDirectory(file);
									deletecounter++;
								} catch (Exception e) {
									logger.error("not possible to delete folder " + file.getAbsolutePath());
								}
							}
						}
					}
				}
			}
		}

		return deletecounter;
	}

	public int deleteOldSurveyPDFs(String surveyUID, int id) throws IOException {
		java.io.File folder = getSurveyExportsFolder(surveyUID);
		Path dir = folder.toPath();
		final MutableInteger deletecounter = new MutableInteger(0);

		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path p, BasicFileAttributes attrs) throws IOException {
				if (!p.toFile().isDirectory()) {
					java.io.File candidate = p.toFile();

					if (candidate.getName().startsWith("survey" + id) && candidate.getName().endsWith(".pdf")) {

						if (candidate.delete()) {
							deletecounter.setValue(deletecounter.getValue() + 1);
						} else {
							logger.error("not possible to delete file " + candidate.getAbsolutePath());
						}
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// Skip folders that can't be traversed
				return FileVisitResult.CONTINUE;
			}
		});

		return deletecounter.getValue();
	}

	public java.io.File getTempFolder() {
		java.io.File folder = new java.io.File(usersDir + "temp");
		if (!folder.exists())
			folder.mkdirs();
		return folder;
	}

	public java.io.File createTempFile(String filename, String suffix) {
		return new java.io.File(getTempFolder().getAbsolutePath() + "/" + filename + (suffix != null ? suffix : ""));
	}

	public int deleteOldTempFiles(Date before) {
		java.io.File folder = getTempFolder();
		int deletecounter = 0;
		if (folder.exists()) {
			java.io.File[] fList = folder.listFiles();
			for (java.io.File file : fList) {
				if (!file.isDirectory()) {
					Date modified = new Date(file.lastModified());
					if (modified.before(before)) {
						try {
							if (file.delete()) {
								deletecounter++;
							}
						} catch (Exception e) {
							logger.error("not possible to delete file " + file.getAbsolutePath());
						}
					}
				}
			}
		}
		return deletecounter;
	}

}
