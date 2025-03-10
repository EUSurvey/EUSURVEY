package com.ec.survey.tools.export;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.ec.survey.model.evote.DHondtEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.evote.ElectedCandidate;
import com.ec.survey.model.evote.SeatCounting;
import com.ec.survey.model.evote.SeatDistribution;
import com.ec.survey.tools.ConversionTools;

import static java.lang.String.format;

@Service("xlsxExportCreator")
@Scope("prototype")
public class XlsxExportCreator extends CommonExcelExportCreator {

	@Override
	Workbook createWorkbook() {
		return new SXSSFWorkbook();
	}

	@Override
	String getFileExtension() {
		return ".xlsx";
	}

	public static byte[] createSeatTestSheet(SeatCounting result) throws IOException {
		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet("Testdata");
		
		int rowCounter = 0;
		Row row = sheet.createRow(rowCounter++);
		Cell cell = row.createCell(0);
		cell.setCellValue("Blank votes");
		cell = row.createCell(1);
		cell.setCellValue(0);
		row = sheet.createRow(rowCounter++);
		cell = row.createCell(0);
		cell.setCellValue("Spoilt votes");
		cell = row.createCell(1);
		cell.setCellValue(0);
		row = sheet.createRow(rowCounter++);
		cell = row.createCell(0);
		cell.setCellValue("Preferential votes");
		cell = row.createCell(1);
		cell.setCellValue(0);
		
		int listCount = result.getListSeatDistribution().size();
		
		int colCounter = 1;
		row = sheet.createRow(rowCounter++);
		for (SeatDistribution list : result.getListSeatDistribution()) {
			cell = row.createCell(colCounter++);
			cell.setCellValue(ConversionTools.removeHTMLNoEscape(list.getName()));
		}
		if (!result.getTemplate().equals("l") && !result.getTemplate().equals("p") && !result.getTemplate().equals("o")) {
			row = sheet.createRow(rowCounter++);
			cell = row.createCell(0);
			cell.setCellValue("List votes");
			for (int i = 1; i <= listCount; i++) {
				cell = row.createCell(i);
				cell.setCellValue(0);
			}
		}
		
		for (int c = 1; c <= result.getMaxCandidatesInLists(); c++) {
			row = sheet.createRow(rowCounter++);
			cell = row.createCell(0);
			cell.setCellValue("Candidate " + c);
			for (int i = 1; i <= listCount; i++) {
				cell = row.createCell(i);
				cell.setCellValue(0);
			}
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		    workbook.write(bos);
		} finally {
		    bos.close();
		}
		byte[] bytes = bos.toByteArray();
		
		workbook.close();
		
		return bytes;
	}
	
	private static CellStyle borderStyle = null;
	private static CellStyle boldStyle = null;
	private static CellStyle greyStyle = null;
	private static CellStyle boldgreyStyle = null;
	private static CellStyle percentStyle = null;
	private static CellStyle boldPercentStyle = null;
	private static CellStyle redStyle = null;
	private static CellStyle redPercentStyle = null;
	private static XSSFCellStyle yellowBackgroundStyle = null;
	private static XSSFCellStyle greenBackgroundStyle = null;
	private static XSSFCellStyle purpleBackgroundStyle = null;
	private static XSSFCellStyle redBackgroundStyle = null;
	
	private static void initStyles(Workbook workbook) throws Exception {
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);

		percentStyle = createCellStyle(workbook);
		percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

		boldPercentStyle = createCellStyle(workbook);
		boldPercentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
		boldPercentStyle.setFont(boldFont);

		greyStyle = createCellStyle(workbook);
		Font greyFont = workbook.createFont();
		greyFont.setColor(IndexedColors.GREY_50_PERCENT.index);
		greyStyle.setFont(greyFont);

		boldgreyStyle = createCellStyle(workbook);
		Font boldgreyFont = workbook.createFont();
		boldgreyFont.setColor(IndexedColors.GREY_50_PERCENT.index);
		boldgreyFont.setBold(true);
		boldgreyStyle.setFont(boldgreyFont);

		redStyle = createCellStyle(workbook);
		Font redFont = workbook.createFont();
		redFont.setColor(Font.COLOR_RED);
		redStyle.setFont(redFont);

		redPercentStyle = createCellStyle(workbook);
		redPercentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
		redPercentStyle.setFont(redFont);

		yellowBackgroundStyle = (XSSFCellStyle) createCellStyle(workbook);
		XSSFColor colorYellow = new XSSFColor(new Color(0xFFC000));
		yellowBackgroundStyle.setFillForegroundColor(colorYellow);
		yellowBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		greenBackgroundStyle = (XSSFCellStyle) createCellStyle(workbook);
		XSSFColor colorGreen = new XSSFColor(new Color(0x00E266));
		greenBackgroundStyle.setFillForegroundColor(colorGreen);
		greenBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		purpleBackgroundStyle = (XSSFCellStyle) createCellStyle(workbook);
		XSSFColor colorPurple = new XSSFColor(new Color(0xE064E3));
		purpleBackgroundStyle.setFillForegroundColor(colorPurple);
		purpleBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		redBackgroundStyle = (XSSFCellStyle) createCellStyle(workbook);
		XSSFColor colorRed = new XSSFColor(new Color(0xFFABAB));
		redBackgroundStyle.setFillForegroundColor(colorRed);
		redBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		borderStyle = createCellStyle(workbook);

		boldStyle = createCellStyle(workbook);
		boldStyle.setFont(boldFont);
	}

	private static CellStyle createCellStyle(Workbook wb){
		var style = wb.createCellStyle();

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		return style;
	}

	private static Sheet createAutoSizingSheet(SXSSFWorkbook workbook, String name) {
		var sheet = workbook.createSheet(name);
		sheet.trackAllColumnsForAutoSizing();
		return sheet;
	}
	
	public static byte[] createSeatContribution(SeatCounting result, MessageSource resource) throws Exception {
				
		var workbook = new SXSSFWorkbook();
		var sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.Counting"));

		boolean luxTemplate = result.getTemplate().equals("l");
		boolean outsideTemplate = result.getTemplate().equals("o");
		
		int rowCounter = 0;
		
		initStyles(workbook);
		
		addCountingTable(resource, result, sheet, luxTemplate, outsideTemplate, 0);
		if (luxTemplate) {
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.DHondtSeatDistribution"));
			rowCounter = addDHondtSeatDistribution(resource, result, sheet, 0, outsideTemplate);
			addDistributionPreferentialSeats(resource, result, sheet, luxTemplate, rowCounter);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.DHondtTable"));
			addDHondtTable(resource, result, sheet, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.ElectedCandidates"));
			addElectedCandidatesFromPreferentialVotes(resource, result, sheet, luxTemplate, outsideTemplate, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.NbVotesPerCandidate"));
			addNumberOfVotesPerCandidate(resource, result, sheet, luxTemplate, outsideTemplate, 0);
		} else if (outsideTemplate) {
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.AllocationFirstExport"));
			addElectedCandidatesFromPreferentialVotes(resource, result, sheet, luxTemplate, outsideTemplate, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.DistributionListSeats"));
			rowCounter = addDHondtSeatDistribution(resource, result, sheet, 0, outsideTemplate);
			addDistributionListSeats(resource, result, sheet, rowCounter, outsideTemplate);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.DHondtTable"));
			addDHondtTable(resource, result, sheet, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.NbVotesPerCandidate"));
			addNumberOfVotesPerCandidate(resource, result, sheet, luxTemplate, outsideTemplate, 0);
		} else {
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.WeightingOfVotes"));
			addWeightingOfVotes(resource, result, sheet, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.AllocationOfSeats"));
			addAllocationOfSeats(resource, result, sheet, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.DistributionListSeats"));
			addDistributionListSeats(resource, result, sheet, 0, outsideTemplate);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.DistributionPreferentialSeatsExport"));
			addDistributionPreferentialSeats(resource, result, sheet, luxTemplate, rowCounter);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.ElectedCandidatesListVotesExport"));
			addElectedCandidatesFromListVotes(resource, result, sheet, rowCounter);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.ElectedCandidatesPreferentialVotesExport"));
			addElectedCandidatesFromPreferentialVotes(resource, result, sheet, luxTemplate, outsideTemplate, 0);
			sheet = createAutoSizingSheet(workbook, getMessage(resource, "label.seats.NbVotesPerCandidate"));
			addNumberOfVotesPerCandidate(resource, result, sheet, luxTemplate, outsideTemplate, 0);
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		    workbook.write(bos);
		} finally {
		    bos.close();
		}
		byte[] bytes = bos.toByteArray();
		
		workbook.close();
		
		return bytes;
	}

	private static int addAllocationOfSeats(MessageSource resource, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 3, getMessage(resource, "label.seats.Prorata"), false);

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.ListVotes"),false);
		addNumberCell(row, 1, result.getListVotes(), false);
		addStringCell(row, 2, format("%.2f", 100 * (double)result.getListVotes() / (double)(result.getListVotes() + result.getPreferentialVotes())) + "%", false);
		addNumberCell(row, 3, result.getListVotesSeats(), false);

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.PreferentialVotes"),false);
		addNumberCell(row, 1, result.getPreferentialVotes(), false);
		addStringCell(row, 2, format("%.2f", 100 * (double)result.getPreferentialVotes() / (double)(result.getListVotes() + result.getPreferentialVotes())) + "%", false);
		addNumberCell(row, 3, result.getPreferentialVotesSeats(), false);

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"),false);
		addNumberCell(row, 1, result.getListVotes() + result.getPreferentialVotes(), false);
		addPercentCell(row, 2, 100, false);
		addNumberCell(row, 3, result.getMaxSeats(), false);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		return ++rowCounter;
	}
	
	private static int addCountingTable(MessageSource resource, SeatCounting result, Sheet sheet, boolean luxTemplate, boolean outsideTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.Quorum"), false);
		addNumberCell(row, 1, result.getQuorum(), false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.ParticipationRate"), false);
		addStringCell(row, 1, format("%.2f", 100 * (double)result.getTotal() / (double)result.getVoterCount()) + "% (" + result.getTotal() + " / " + result.getVoterCount() + ")", false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.Votes"), false);
		addNumberCell(row, 1, result.getVotes(), false);
		
		if (!luxTemplate && !outsideTemplate) {
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, getMessage(resource, "label.seats.ListVotes"), false);
			addNumberCell(row, 1, result.getListVotes(), false);
			
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, getMessage(resource, "label.seats.PreferentialVotes"), false);
			addNumberCell(row, 1, result.getPreferentialVotes(), false);
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.BlankVotes"), false);
		addNumberCell(row, 1, result.getBlankVotes(), false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.SpoiltVotes"), false);
		addNumberCell(row, 1, result.getSpoiltVotes(), false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"),false);
		addNumberCell(row, 1, result.getTotal(), false);
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		
		return ++rowCounter;
	}
	
	private static int addDHondtSeatDistribution(MessageSource resource, SeatCounting result, Sheet sheet, int rowCounter, boolean outsideTemplate) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 1, getMessage(resource, "label.Votes"), true);
		addStringCell(row, 2, "%", true);
		
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, list.getName(), false);
			addNumberCell(row, 1, outsideTemplate ? list.getPreferentialVotes() : list.getLuxListVotes(), false);
			addPercentCell(row, 2, list.getListPercent(), false);
			
			if (list.getListPercentWeighted() < result.getMinListPercent()) {
				row.getCell(0).setCellStyle(redStyle);
				row.getCell(1).setCellStyle(redStyle);
				row.getCell(2).setCellStyle(redPercentStyle);
			}
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"), true);
		addNumberCell(row, 1, outsideTemplate ? result.getListVotes() : result.getLuxListVotes(), true);
		addPercentCell(row, 2, 100, true);
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		
		return ++rowCounter;
	}

	private static int addDHondtTable(MessageSource resource, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Round"), false);

		int counter = 1;
		for(SeatDistribution list : result.getListSeatDistribution()) {
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				addStringCell(row, counter++, list.getName(), false);
			}
		}

		DHondtEntry[][] entries = result.getDHondtEntries();
		for (int r = 0; r < entries.length; r++) {  // r = round = divisor
			row = sheet.createRow(rowCounter++);
			if (entries[r].length > 0) {
				addNumberCell(row, 0, entries[r][0].getRound(), false);
				for (int i = 0; i < entries[r].length; i++) {
					DHondtEntry entry = entries[r][i];
					
					if (entry.getSeat() > 0) {
						addStringCell(row, i + 1, format("%.2f", entry.getValue()) + " (" + entry.getSeat() + ")" , false);
					} else {
						addNumberCell(row, i + 1, entry.getValue(), false);
					}
				}
			}
		}
		
		for (int i = 0; i < counter; i++) {
			sheet.autoSizeColumn(i);
		} 		

		return ++rowCounter;
	}

	private static int addDistributionListSeats(MessageSource resource, SeatCounting result, Sheet sheet, int rowCounter, boolean outsideTemplate) {
		Row row;
		
		for (String message: result.getReallocationMessagesForLists()) {
			row = sheet.createRow(rowCounter++);
			addMessageStringCell(row, 0, message, true);
		}
		
		if (result.getReallocationMessagesForLists().size() > 0) {
			rowCounter++;
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.List"), true);
		addStringCell(row, 1, getMessage(resource, "label.Votes"), true);
		addStringCell(row, 2, "%", true);
		addStringCell(row, 3, getMessage(resource, "label.seats.Seats"), true);

		for (SeatDistribution list : result.getListSeatDistribution()) {
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				row = sheet.createRow(rowCounter++);
				addStringCell(row, 0, list.getName(), false);
				addNumberCell(row, 1, outsideTemplate ? list.getPreferentialVotes() : list.getListVotes(), false);
				addPercentCell(row, 2, list.getListPercentFinal(), false);
				addNumberCell(row, 3, list.getListSeats(), false);
			}
		}

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"), true);
		addNumberCell(row, 1, result.getListVotesFinal(), true);
		addPercentCell(row, 2, 100, true);
		addNumberCell(row, 3, result.getListVotesSeatsReal(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);	
	
		return ++rowCounter;
	}
	
	private static int addDistributionPreferentialSeats(MessageSource resource, SeatCounting result, Sheet sheet, boolean luxTemplate, int rowCounter) {
		Row row;

		for (String message: result.getReallocationMessages()) {
			row = sheet.createRow(rowCounter++);
			addMessageStringCell(row, 0, message, true);
		}
		
		if (result.getReallocationMessages().size() > 0) {
			rowCounter++;
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.List"), true);
		addStringCell(row, 1, getMessage(resource, "label.Votes"), true);
		addStringCell(row, 2, "%", true);
		addStringCell(row, 3, getMessage(resource, "label.seats.Seats"), true);
		
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				row = sheet.createRow(rowCounter++);
				addStringCell(row, 0, list.getName(), false);
				addNumberCell(row, 1, luxTemplate ? list.getLuxListVotes() : list.getPreferentialVotes(), false);
				addPercentCell(row, 2, list.getPreferentialPercentFinal(), false);
				addNumberCell(row, 3, list.getPreferentialSeats(), false);
			}
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"), true);
		addNumberCell(row, 1, result.getPreferentialVotesFinal(), true);
		addPercentCell(row, 2, 100, true);
		addNumberCell(row, 3, result.getPreferentialVotesSeatsReal(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		
		return ++rowCounter;
	}

	private static int addElectedCandidatesFromListVotes(MessageSource resource, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.List"), true);
		addStringCell(row, 1, getMessage(resource, "label.seats.Candidate"), true);
		addStringCell(row, 2, getMessage(resource, "label.Votes"), true);
		addStringCell(row, 3, getMessage(resource, "label.seats.Seats"), true);

		for(ElectedCandidate candidate : result.getCandidatesFromListVotes()) {
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, candidate.getList(), false);
			addStringCell(row, 1, candidate.getName(), false);
			addNumberCell(row, 2, candidate.getVotes(), false);
			addNumberCell(row, 3, candidate.getSeats(), false);
		}

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"), true);
		addEmptyCell(row, 1);
		addNumberCell(row, 2, result.getSumListVotes(), true);
		addNumberCell(row, 3, result.getListVotesSeatsReal(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		return ++rowCounter;
	}

	private static int addElectedCandidatesFromPreferentialVotes(MessageSource resource, SeatCounting result, Sheet sheet, boolean luxTemplate, boolean outsideTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.List"), true);
		addStringCell(row, 1, getMessage(resource, "label.seats.Candidate"), true);
		addStringCell(row, 2, getMessage(resource, "label.Votes"), true);
		addStringCell(row, 3, getMessage(resource, "label.seats.Seats"), true);
		
		for (ElectedCandidate candidate : result.getCandidatesFromPreferentialVotes())
		{
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, candidate.getList(), false);
			addStringCell(row, 1, candidate.getName(), false);
			addNumberCell(row, 2, candidate.getVotes(), false);
			addNumberCell(row, 3, candidate.getSeats(), false);
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"), true);
		addEmptyCell(row, 1);
		addNumberCell(row, 2, result.getSumPreferentialVotes(), true);
		addNumberCell(row, 3, outsideTemplate ? 8 : result.getPreferentialVotesSeatsReal(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		
		return ++rowCounter;
	}

	private static int addNumberOfVotesPerCandidate(MessageSource resource, SeatCounting result, Sheet sheet, boolean luxTemplate, boolean outsideTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);

		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() < result.getMinListPercent()) {
				addMessageStringCell(row, 0, getMessage(resource, "help.EligibleLists"), false);
				row.getCell(0).setCellStyle(greyStyle);

				sheet.createRow(rowCounter++);	//empty row

				row = sheet.createRow(rowCounter++);
				break;
			}
		}

		if (result.isAmbiguous()) {
			addMessageStringCell(row, 0, getMessage(resource, "info.seats.ambiguous"), true);
			row.getCell(0).setCellStyle(redStyle);
			rowCounter++;
			row = sheet.createRow(rowCounter++);
		}
	
		addStringCell(row, 0, getMessage(resource, "label.seats.CandidateNumber"), true);
		
		int counter = 1;
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				addStringCell(row, counter++, list.getName(), true);				
			}
		}
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() < result.getMinListPercent()) {
				addStringCell(row, counter, list.getName(), true);
				row.getCell(counter).setCellStyle(boldgreyStyle);
				counter++;
			}
		}
		
		if (!luxTemplate && !outsideTemplate) {
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, getMessage(resource, "label.seats.TotalListVotes"), true);
			counter = 1;
			for (SeatDistribution list : result.getListSeatDistribution())
			{
				if (list.getListPercentWeighted() >= result.getMinListPercent()) {
					addNumberCell(row, counter++, list.getListVotes(), true);				
				}
			}
			for (SeatDistribution list : result.getListSeatDistribution())
			{
				if (list.getListPercentWeighted() < result.getMinListPercent()) {
					addNumberCell(row, counter, list.getListVotes(), true);
					row.getCell(counter).setCellStyle(boldgreyStyle);
					counter++;
				}
			}
		}
		
		int candidateCounter = 1;
		for (List<ElectedCandidate> candidates : result.getCandidateVotes())
		{
			row = sheet.createRow(rowCounter++);
			addNumberCell(row, 0, candidateCounter++, false);
			counter = 1;
			for (ElectedCandidate candidate : candidates) {
				addNumberCell(row, counter, candidate.getVotes(), false);
				if (candidate.isListNotAccepted()) {
					row.getCell(counter).setCellStyle(greyStyle);
				} else if (candidate.isAmbiguous()) {
					row.getCell(counter).setCellStyle(purpleBackgroundStyle);
				} else if (candidate.getSeats() > 0) {
					if (candidate.isPreferentialSeat()) {
						row.getCell(counter).setCellStyle(yellowBackgroundStyle);
					} else {
						row.getCell(counter).setCellStyle(greenBackgroundStyle);
					}
				} else if (candidate.isReallocatedSeat()) {
					row.getCell(counter).setCellStyle(redBackgroundStyle);
				}
				counter++;
			}
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, (luxTemplate || outsideTemplate) ? getMessage(resource, "label.seats.Total") : getMessage(resource, "label.seats.TotalPreferentialVotes"), true);
		counter = 1;
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				addNumberCell(row, counter++, luxTemplate ? list.getLuxListVotes() : list.getPreferentialVotes(), true);				
			}
		}
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() < result.getMinListPercent()) {
				addNumberCell(row, counter, luxTemplate ? list.getLuxListVotes() : list.getPreferentialVotes(), true);
				row.getCell(counter).setCellStyle(boldgreyStyle);
				counter++;
			}
		}
		
		for (int i = 0; i < counter; i++) {
			sheet.autoSizeColumn(i);
		}

		sheet.createRow(rowCounter++);	//empty row

		row = sheet.createRow(rowCounter++);
		addEmptyCell(row, 0);
		row.getCell(0).setCellStyle(greenBackgroundStyle);
		addStringCell(row, 1, getMessage(resource, "label.seats.ElectedFromListVotes"), false);

		row = sheet.createRow(rowCounter++);
		addEmptyCell(row, 0);
		row.getCell(0).setCellStyle(yellowBackgroundStyle);
		addStringCell(row, 1, outsideTemplate ? getMessage(resource, "label.seats.ElectedByHighest") : getMessage(resource, "label.seats.ElectedFromPreferentialVotes"), false);

		row = sheet.createRow(rowCounter++);
		addEmptyCell(row, 0);
		row.getCell(0).setCellStyle(redBackgroundStyle);
		addStringCell(row, 1, getMessage(resource, "label.seats.Reallocated"), false);

		if (outsideTemplate) {
			row = sheet.createRow(rowCounter++);
			addEmptyCell(row, 0);
			row.getCell(0).setCellStyle(purpleBackgroundStyle);
			addStringCell(row, 1, getMessage(resource, "label.seats.Ambiguous"), false);
		}
		
		for (int i = 0; i < counter; i++) {
			sheet.autoSizeColumn(i);
		}

		return ++rowCounter;
	}
	
	private static void addStringCell(Row row, int column, String content, boolean bold) {
		Cell cell = row.createCell(column);
		cell.setCellStyle(bold ? boldStyle : borderStyle);
		cell.setCellValue(ConversionTools.removeHTMLNoEscape(content));
	}
	
	private static void addMessageStringCell(Row row, int column, String content, boolean bold) {		
		Cell cell = row.createCell(column);
		cell.setCellStyle(bold ? boldStyle : borderStyle);
		cell.setCellValue(ConversionTools.removeHTMLNoEscape(content));
		
		for (int i = column+1; i < column+9; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(bold ? boldStyle : borderStyle);
		}
		
		row.getSheet().addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), column, column+8));
	}

	private static void addEmptyCell(Row row, int column) {
		Cell cell = row.createCell(column);
		cell.setCellStyle(borderStyle);
	}

	private static void addNumberCell(Row row, int column, double content, boolean bold) {
		Cell cell = row.createCell(column);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellStyle(bold ? boldStyle : borderStyle);
		cell.setCellValue(content);
	}
	
	private static void addPercentCell(Row row, int column, double content, boolean bold) {
		Cell cell = row.createCell(column);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellStyle(bold ? boldPercentStyle : percentStyle);
		cell.setCellValue(content / 100);
	}

	private static int addWeightingOfVotes(MessageSource resource, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 1, getMessage(resource, "label.seats.ListVotes"), true);
		addStringCell(row, 2, getMessage(resource, "label.seats.ListVotesWeighted"), true);
		addStringCell(row, 3, getMessage(resource, "label.seats.CandidateVotes"), true);
		addStringCell(row, 4, getMessage(resource, "label.seats.Total"), true);
		addStringCell(row, 5, "%", true);

		for (SeatDistribution list : result.getListSeatDistribution())
		{
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, list.getName(), false);
			addNumberCell(row, 1, list.getListVotes(), false);
			addNumberCell(row, 2, list.getListVotesWeighted(), false);
			addNumberCell(row, 3, list.getPreferentialVotes(), false);
			addNumberCell(row, 4, list.getTotalWeighted(), false);
			addPercentCell(row, 5, list.getListPercentWeighted(), false);

			if (list.getListPercentWeighted() < result.getMinListPercent()) {
				row.getCell(0).setCellStyle(redStyle);
				row.getCell(1).setCellStyle(redStyle);
				row.getCell(2).setCellStyle(redStyle);
				row.getCell(3).setCellStyle(redStyle);
				row.getCell(4).setCellStyle(redStyle);
				row.getCell(5).setCellStyle(redPercentStyle);
			}
		}

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, getMessage(resource, "label.seats.Total"), true);
		addNumberCell(row, 1, result.getListVotes(), true);
		addNumberCell(row, 2, result.getListVotesWeighted(), true);
		addNumberCell(row, 3, result.getTotalPreferentialVotes(), true);
		addNumberCell(row, 4, result.getTotalVotesWeighted(), true);
		addPercentCell(row, 5, 100, true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);

		return ++rowCounter;
	}
	
	private static Locale locale = new Locale("EN");
	
	private static String getMessage(MessageSource resource, String key) {
		return resource.getMessage(key, null, key, locale);
	}
}
