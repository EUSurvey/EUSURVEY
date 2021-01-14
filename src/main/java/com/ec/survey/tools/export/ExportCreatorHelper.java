package com.ec.survey.tools.export;

import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.FilesByTypes;
import com.ec.survey.model.survey.base.File;

import java.util.Map;

public class ExportCreatorHelper {

	public static String retrieveExplanationWithFilesFromReportingAnswer(final String cellContent,
			final String answerSetUid, final String questionUid,
			final FilesByTypes<String, String> explanationFilesToExport) {

		String explanation;
		String filesPart = "";
		final int lastLineBreakPosition = cellContent.lastIndexOf("\n");
		if (lastLineBreakPosition == -1) {
			//either no explanation text or no files
			
			explanation = cellContent;
			
			if (explanation.contains("|"))
			{
				filesPart = cellContent;
				explanation = "";
			}
			
		} else {
			explanation = cellContent.substring(0, lastLineBreakPosition);
			filesPart = cellContent.substring(lastLineBreakPosition + 1);			
		}
	
		final String[] filesParts = filesPart.split(";");
		for (final String part : filesParts) {
			if (part.contains("|")) {
				final String fileUid = part.substring(0, part.indexOf("|"));
				final String fileName = part.substring(part.indexOf("|") + 1);
				final File file = new File();
				file.setUid(fileUid);
				file.setName(fileName);
				explanationFilesToExport.addFile(answerSetUid, questionUid, file);
			}
		}
		
		return explanation;
	}

	public static String retrieveExplanationWithFilesFromAnswerSetAndExistingFiles(final AnswerSet answerSet,
			final Map<Integer, Map<String, String>> explanations, final String questionUid,
			final FilesByTypes<Integer, String> explanationFilesOfSurvey,
			final FilesByTypes<String, String> explanationFilesToExport) {

		String explanation = "";
		final int answerSetId = answerSet.getId();
		final String answerSetUid = answerSet.getUniqueCode();

		if (explanations.containsKey(answerSetId) && explanations.get(answerSetId).containsKey(questionUid)) {
			explanation = explanations.get(answerSetId).get(questionUid);
		}

		for (final File file : explanationFilesOfSurvey.getFiles(answerSetId, questionUid)) {
			explanationFilesToExport.addFile(answerSetUid, questionUid, file);
		}
		return explanation;
	}

}
