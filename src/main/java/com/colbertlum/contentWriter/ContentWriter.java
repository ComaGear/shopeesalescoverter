package com.colbertlum.contentWriter;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ContentWriter<T> {
    
    private XSSFSheet sheet;
    private ContentHeaderMapperInterface<T> mapper;
    private List<T> list;
    private List<String> headerList;

    public void cleanTable(){
        // clean
        int lastRowNum = sheet.getLastRowNum();
        for (int i = lastRowNum; i >= 1; i--) {
            Row row = sheet.getRow(i);
            if (row != null) {
                sheet.removeRow(row);
            }
        }
    }

    public void writeHeader(){
        if (list == null || list.isEmpty()) return;
        int rowIndex = 0;
        XSSFRow row = sheet.getRow(rowIndex);
        if(row == null) row = sheet.createRow(rowIndex);
        int columnIndex = 0;
        for(String header : headerList){
            Cell cell = row.getCell(columnIndex);
            if(cell == null) cell = row.createCell(columnIndex);
            cell.setCellValue(header);
        }
    }

    public void writeCells(){
        if (list == null || list.isEmpty()) return;

        int rowIndex = 1;
        for(T o : list){
            XSSFRow row = sheet.createRow(rowIndex);
            rowIndex++;

            int columnIndex = 0;
            for(String header : headerList){
                Cell cell = row.getCell(columnIndex);
                if(cell == null) cell = row.createCell(columnIndex);
                cell.setCellValue(mapper.onCell(header, o));
            }
        }
    }
    

    public void writeAll() {
        if (list == null || list.isEmpty()) return;
        cleanTable();
        writeHeader();
        writeCells();
    }
    
    public ContentWriter(XSSFSheet sheet, ContentHeaderMapperInterface<T> mapper, List<T> list) {
        this.sheet = sheet;
        this.mapper = mapper;
        this.list = list;
        headerList = mapper.onHeader();

    }
}
