package com.ec.survey.tools.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.ec.survey.model.Form;
import com.ec.survey.model.evote.DHondtEntry;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.evote.ElectedCandidate;
import com.ec.survey.model.evote.SeatCounting;
import com.ec.survey.model.evote.SeatDistribution;
import com.ec.survey.tools.ConversionTools;

import static java.lang.String.format;

@Service("xlsxExportCreator")
@Scope("prototype")
public class XlsxExportCreator {

	public static byte[] createSeatTestSheet(SeatCounting result) throws IOException {
		Workbook workbook = new XSSFWorkbook();
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
		if (!result.getTemplate().equals("l")) {
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
	private static CellStyle percentStyle = null;
	private static CellStyle boldPercentStyle = null;
	private static CellStyle redStyle = null;
	private static CellStyle redPercentStyle = null;
	private static XSSFCellStyle yellowStyle = null;
	private static XSSFCellStyle greenStyle = null;
	
	private static void initStyles(Workbook workbook) throws Exception {
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);

		percentStyle = workbook.createCellStyle();
		percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
		percentStyle.setAlignment(CellStyle.ALIGN_LEFT);
		percentStyle.setBorderTop(CellStyle.BORDER_THIN);
		percentStyle.setBorderBottom(CellStyle.BORDER_THIN);
		percentStyle.setBorderLeft(CellStyle.BORDER_THIN);
		percentStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		boldPercentStyle = workbook.createCellStyle();
		boldPercentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
		boldPercentStyle.setAlignment(CellStyle.ALIGN_LEFT);
		boldPercentStyle.setBorderTop(CellStyle.BORDER_THIN);
		boldPercentStyle.setBorderBottom(CellStyle.BORDER_THIN);
		boldPercentStyle.setBorderLeft(CellStyle.BORDER_THIN);
		boldPercentStyle.setBorderRight(CellStyle.BORDER_THIN);
		boldPercentStyle.setFont(boldFont);
		
		redStyle = workbook.createCellStyle();
		Font redFont = workbook.createFont();
		redFont.setColor(Font.COLOR_RED);
		redStyle.setFont(redFont);
		redStyle.setAlignment(CellStyle.ALIGN_LEFT);
		redStyle.setBorderTop(CellStyle.BORDER_THIN);
		redStyle.setBorderBottom(CellStyle.BORDER_THIN);
		redStyle.setBorderLeft(CellStyle.BORDER_THIN);
		redStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		redPercentStyle = workbook.createCellStyle();
		redPercentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
		redPercentStyle.setFont(redFont);
		redPercentStyle.setAlignment(CellStyle.ALIGN_LEFT);
		redPercentStyle.setBorderTop(CellStyle.BORDER_THIN);
		redPercentStyle.setBorderBottom(CellStyle.BORDER_THIN);
		redPercentStyle.setBorderLeft(CellStyle.BORDER_THIN);
		redPercentStyle.setBorderRight(CellStyle.BORDER_THIN);

		yellowStyle = (XSSFCellStyle) workbook.createCellStyle();
		String hexColorYellow = "FFC000";
		XSSFColor colorYellow = new XSSFColor(Hex.decodeHex(hexColorYellow.toCharArray()));
		yellowStyle.setFillForegroundColor(colorYellow);
		yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		yellowStyle.setAlignment(CellStyle.ALIGN_LEFT);
		yellowStyle.setBorderTop(CellStyle.BORDER_THIN);
		yellowStyle.setBorderBottom(CellStyle.BORDER_THIN);
		yellowStyle.setBorderLeft(CellStyle.BORDER_THIN);
		yellowStyle.setBorderRight(CellStyle.BORDER_THIN);

		greenStyle = (XSSFCellStyle) workbook.createCellStyle();
		String hexColorGreen = "00E266";
		XSSFColor colorGreen = new XSSFColor(Hex.decodeHex(hexColorGreen.toCharArray()));
		greenStyle.setFillForegroundColor(colorGreen);
		greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		greenStyle.setAlignment(CellStyle.ALIGN_LEFT);
		greenStyle.setBorderTop(CellStyle.BORDER_THIN);
		greenStyle.setBorderBottom(CellStyle.BORDER_THIN);
		greenStyle.setBorderLeft(CellStyle.BORDER_THIN);
		greenStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		borderStyle = workbook.createCellStyle();
		borderStyle.setAlignment(CellStyle.ALIGN_LEFT);
		borderStyle.setBorderTop(CellStyle.BORDER_THIN);
		borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		borderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		borderStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		boldStyle = workbook.createCellStyle();
		boldStyle.setFont(boldFont);
		boldStyle.setAlignment(CellStyle.ALIGN_LEFT);
		boldStyle.setBorderTop(CellStyle.BORDER_THIN);
		boldStyle.setBorderBottom(CellStyle.BORDER_THIN);
		boldStyle.setBorderLeft(CellStyle.BORDER_THIN);
		boldStyle.setBorderRight(CellStyle.BORDER_THIN);
	}
	
	public static byte[] createSeatContribution(SeatCounting result, Form form) throws Exception {
				
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Votes counting");
		boolean luxTemplate = result.getTemplate().equals("l");
		
		int rowCounter = 0;
		
		initStyles(workbook);
		
		addCountingTable(form, result, sheet, luxTemplate, 0);
		if (luxTemplate) {
			sheet = workbook.createSheet(form.getMessage("label.seats.DHondtSeatDistribution"));
			rowCounter = addDHondtSeatDistribution(form, result, sheet, 0);
			addDistributionPreferentialSeats(form, result, sheet, luxTemplate, rowCounter);
			sheet = workbook.createSheet("D'Hondt table");
			addDHondtTable(form, result, sheet, 0);
			sheet = workbook.createSheet("Elected candidates");
			addElectedCandidatesFromPreferentialVotes(form, result, sheet, luxTemplate, 0);
			sheet = workbook.createSheet(form.getMessage("label.seats.NbVotesPerCandidate"));
			addNumberOfVotesPerCandidate(form, result, sheet, luxTemplate, 0);
		} else {
			sheet = workbook.createSheet(form.getMessage("label.seats.WeightingOfVotes"));
			addWeightingOfVotes(form, result, sheet, 0);
			sheet = workbook.createSheet("Allocation of seats");
			addAllocationOfSeats(form, result, sheet, 0);
			sheet = workbook.createSheet(form.getMessage("label.seats.DistributionListSeats"));
			addDistributionListSeats(form, result, sheet, 0);
			sheet = workbook.createSheet("Distribution of pref. seats"); // we do not use the label as it is too long (31 character maximum in Excel)
			addDistributionPreferentialSeats(form, result, sheet, luxTemplate, rowCounter);
			sheet = workbook.createSheet("Elected candidates list votes"); // we do not use the label as it is too long (31 character maximum in Excel)
			addElectedCandidatesFromListVotes(form, result, sheet, rowCounter);
			sheet = workbook.createSheet("Elected candidates pref. votes"); // we do not use the label as it is too long (31 character maximum in Excel)
			addElectedCandidatesFromPreferentialVotes(form, result, sheet, luxTemplate, 0);
			sheet = workbook.createSheet(form.getMessage("label.seats.NbVotesPerCandidate"));
			addNumberOfVotesPerCandidate(form, result, sheet, luxTemplate, 0);
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

	private static int addAllocationOfSeats(Form form, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 3, form.getMessage("label.seats.Prorata"), false);

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.ListVotes"),false);
		addNumberCell(row, 1, result.getListVotes(), false);
		addStringCell(row, 2, format("%.2f", 100 * (double)result.getListVotes() / (double)(result.getListVotes() + result.getPreferentialVotes())) + "%", false);
		addNumberCell(row, 3, result.getListVotesSeats(), false);

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.PreferentialVotes"),false);
		addNumberCell(row, 1, result.getPreferentialVotes(), false);
		addStringCell(row, 2, format("%.2f", 100 * (double)result.getPreferentialVotes() / (double)(result.getListVotes() + result.getPreferentialVotes())) + "%", false);
		addNumberCell(row, 3, result.getPreferentialVotesSeats(), false);

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Total"),false);
		addNumberCell(row, 1, result.getListVotes() + result.getPreferentialVotes(), false);
		addPercentCell(row, 2, 100, false);
		addNumberCell(row, 3, result.getMaxSeats(), false);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		return ++rowCounter;
	}
	
	private static int addCountingTable(Form form, SeatCounting result, Sheet sheet, boolean luxTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.Quorum"), false);
		addNumberCell(row, 1, result.getQuorum(), false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.ParticipationRate"), false);
		addStringCell(row, 1, format("%.2f", 100 * (double)result.getTotal() / (double)result.getVoterCount()) + "% (" + result.getTotal() + " / " + result.getVoterCount() + ")", false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.Votes"), false);
		addNumberCell(row, 1, result.getVotes(), false);
		
		if (!luxTemplate) {
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, form.getMessage("label.seats.ListVotes"), false);
			addNumberCell(row, 1, result.getListVotes(), false);
			
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, form.getMessage("label.seats.PreferentialVotes"), false);
			addNumberCell(row, 1, result.getPreferentialVotes(), false);
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.BlankVotes"), false);
		addNumberCell(row, 1, result.getBlankVotes(), false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.SpoiltVotes"), false);
		addNumberCell(row, 1, result.getSpoiltVotes(), false);
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Total"),false);
		addNumberCell(row, 1, result.getTotal(), false);
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		
		return ++rowCounter;
	}
	
	private static int addDHondtSeatDistribution(Form form, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 1, form.getMessage("label.Votes"), true);
		addStringCell(row, 2, "%", true);
		
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, list.getName(), false);
			addNumberCell(row, 1, list.getLuxListVotes(), false);
			addPercentCell(row, 2, list.getListPercent(), false);
			
			if (list.getListPercentWeighted() < result.getMinListPercent()) {
				row.getCell(0).setCellStyle(redStyle);
				row.getCell(1).setCellStyle(redStyle);
				row.getCell(2).setCellStyle(redPercentStyle);
			}
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Total"), true);
		addNumberCell(row, 1, result.getLuxListVotes(), true);
		addPercentCell(row, 2, 100, true);
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		
		return ++rowCounter;
	}

	private static int addDHondtTable(Form form, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Round"), false);

		int counter = 1;
		for(SeatDistribution list : result.getListSeatDistribution()) {
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				addStringCell(row, counter++, list.getName(), false);
			}
		}

		DHondtEntry[][] entries = result.getDHondtEntries();
		for (int r = 0; r < result.getMaxSeats(); r++) {  // r = round = divisor
			row = sheet.createRow(rowCounter++);
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
		
		for (int i = 0; i < counter; i++) {
			sheet.autoSizeColumn(i);
		} 		

		return ++rowCounter;
	}

	private static int addDistributionListSeats(Form form, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.List"), false);
		addStringCell(row, 1, form.getMessage("label.Votes"), false);
		addStringCell(row, 2, "%", false);
		addStringCell(row, 3, form.getMessage("label.seats.Seats"), false);

		for (SeatDistribution list : result.getListSeatDistribution()) {
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				row = sheet.createRow(rowCounter++);
				addStringCell(row, 0, list.getName(), false);
				addNumberCell(row, 1, list.getListVotes(), false);
				addPercentCell(row, 2, list.getListPercentFinal(), false);
				addNumberCell(row, 3, list.getListSeats(), false);
			}
		}

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Total"), true);
		addNumberCell(row, 1, result.getListVotesFinal(), true);
		addPercentCell(row, 2, 100, true);
		addNumberCell(row, 3, result.getListVotesSeats(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		return ++rowCounter;
	}
	
	private static int addDistributionPreferentialSeats(Form form, SeatCounting result, Sheet sheet, boolean luxTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.List"), true);
		addStringCell(row, 1, form.getMessage("label.Votes"), true);
		addStringCell(row, 2, "%", true);
		addStringCell(row, 3, form.getMessage("label.seats.Seats"), true);
		
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
		addStringCell(row, 0, form.getMessage("label.seats.Total"), true);
		addNumberCell(row, 1, result.getPreferentialVotesFinal(), true);
		addPercentCell(row, 2, 100, true);
		addNumberCell(row, 3, result.getPreferentialVotesSeats(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		return ++rowCounter;
	}

	private static int addElectedCandidatesFromListVotes(Form form, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.List"), true);
		addStringCell(row, 1, form.getMessage("label.seats.Candidate"), true);
		addStringCell(row, 2, form.getMessage("label.Votes"), true);
		addStringCell(row, 3, form.getMessage("label.seats.Seats"), true);

		for(ElectedCandidate candidate : result.getCandidatesFromListVotes()) {
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, candidate.getList(), false);
			addStringCell(row, 1, candidate.getName(), false);
			addNumberCell(row, 2, candidate.getVotes(), false);
			addNumberCell(row, 3, candidate.getSeats(), false);
		}

		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Total"), true);
		addEmptyCell(row, 1);
		addNumberCell(row, 2, result.getSumListVotes(), true);
		addNumberCell(row, 3, result.getListVotesSeats(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		return ++rowCounter;
	}

	private static int addElectedCandidatesFromPreferentialVotes(Form form, SeatCounting result, Sheet sheet, boolean luxTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.List"), true);
		addStringCell(row, 1, form.getMessage("label.seats.Candidate"), true);
		addStringCell(row, 2, form.getMessage("label.Votes"), true);
		addStringCell(row, 3, form.getMessage("label.seats.Seats"), true);
		
		for (ElectedCandidate candidate : result.getCandidatesFromPreferentialVotes())
		{
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, candidate.getList(), false);
			addStringCell(row, 1, candidate.getName(), false);
			addNumberCell(row, 2, candidate.getVotes(), false);
			addNumberCell(row, 3, candidate.getSeats(), false);
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.Total"), true);
		addEmptyCell(row, 1);
		addNumberCell(row, 2, result.getSumPreferentialVotes(), true);
		addNumberCell(row, 3, result.getPreferentialVotesSeats(), true);

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		
		return ++rowCounter;
	}

	private static int addNumberOfVotesPerCandidate(Form form, SeatCounting result, Sheet sheet, boolean luxTemplate, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, form.getMessage("label.seats.CandidateNumber"), true);
		
		int counter = 1;
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				addStringCell(row, counter++, list.getName(), true);				
			}
		}
		
		if (!luxTemplate) {
			row = sheet.createRow(rowCounter++);
			addStringCell(row, 0, form.getMessage("label.seats.TotalVL"), true);
			counter = 1;
			for (SeatDistribution list : result.getListSeatDistribution())
			{
				if (list.getListPercentWeighted() >= result.getMinListPercent()) {
					addNumberCell(row, counter++, list.getListVotes(), true);				
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
				if (candidate.getSeats() > 0) {
					if (candidate.isPreferentialSeat()) {
						row.getCell(counter).setCellStyle(yellowStyle);
					} else {
						row.getCell(counter).setCellStyle(greenStyle);
					}
				}
				counter++;
			}
		}
		
		row = sheet.createRow(rowCounter++);
		addStringCell(row, 0, luxTemplate ? form.getMessage("label.seats.Total") : form.getMessage("label.seats.TotalVP"), true);
		counter = 1;
		for (SeatDistribution list : result.getListSeatDistribution())
		{
			if (list.getListPercentWeighted() >= result.getMinListPercent()) {
				addNumberCell(row, counter++, list.getLuxListVotes(), true);				
			}
		}
		
		for (int i = 0; i < counter; i++) {
			sheet.autoSizeColumn(i);
		}

		sheet.createRow(rowCounter++);	//empty row

		row = sheet.createRow(rowCounter++);
		addEmptyCell(row, 0);
		row.getCell(0).setCellStyle(greenStyle);
		addStringCell(row, 1, form.getMessage("label.seats.ElectedFromListVotes"), false);

		row = sheet.createRow(rowCounter++);
		addEmptyCell(row, 0);
		row.getCell(0).setCellStyle(yellowStyle);
		addStringCell(row, 1, form.getMessage("label.seats.ElectedFromPreferentialVotes"), false);

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

	private static int addWeightingOfVotes(Form form, SeatCounting result, Sheet sheet, int rowCounter) {
		Row row = sheet.createRow(rowCounter++);
		addStringCell(row, 1, form.getMessage("label.seats.ListVotes"), true);
		addStringCell(row, 2, form.getMessage("label.seats.ListVotesWeighted"), true);
		addStringCell(row, 3, form.getMessage("label.seats.PreferentialVotes"), true);
		addStringCell(row, 4, form.getMessage("label.seats.Total"), true);
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
		addStringCell(row, 0, form.getMessage("label.seats.Total"), true);
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
}
