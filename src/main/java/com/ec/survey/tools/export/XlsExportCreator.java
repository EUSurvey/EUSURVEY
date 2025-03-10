package com.ec.survey.tools.export;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("xlsExportCreator")
@Scope("prototype")
public class XlsExportCreator extends CommonExcelExportCreator {
    @Override
    Workbook createWorkbook() {
        return new HSSFWorkbook();
    }

    @Override
    String getFileExtension() {
        return ".xls";
    }
}
