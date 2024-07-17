package com.colbertlum.contentHandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandler extends DefaultHandler {
    
    
    enum dataType {
        NUMBER, SSTINDEX,
    }

    private boolean isValue;
    private String columnString;
    private dataType readingVDataType;
    private int formatIndex;
    private String formatString;
    private StylesTable stylesTable;
    private SharedStrings sharedStringsTable;
    private StringBuilder value;
    private DataFormatter dataFormatter;
    private int readingRow;
    private HashMap<String, String> columnHeaderMapper;

    protected void onRow(int row){

    }

    protected void onCell(String header, int row, String value){
        
    }

    public Map<String, String> getColumnHeaderMapper(){
        return columnHeaderMapper;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (value == null)
            this.value = new StringBuilder();

        if (isValue)
            value.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        String string = null;

        if ("v".equals(qName)) {
            switch (readingVDataType) {
                case NUMBER:
                    if (this.formatString == null)
                        string = value.toString();
                    else
                        string = dataFormatter.formatRawCellContents(Float.parseFloat(value.toString()),
                                this.formatIndex, this.formatString);
                    break;
                case SSTINDEX:
                    // String sstIndex = value.toString();
                    String sstIndex = value.toString().replaceAll("\\D+","");
                    try {
                        RichTextString rts = sharedStringsTable.getItemAt(Integer.parseInt(sstIndex));
                        string = rts.toString();
                    } catch (NumberFormatException e) {
                    }
                    break;
                default:
                    string = "(TODO: Unexpected type: " + readingVDataType + ")";
                    break;
            }

            if (readingRow == 0 && string != null) {
                columnHeaderMapper.put(columnString, string);
                return;
            }

            String column = "";
            if(columnHeaderMapper.containsKey(columnString)){
                column = columnHeaderMapper.get(columnString);
            }
            onCell(column, readingRow, string);
        }

        if ("row".equals(qName)) {
            if (readingRow > 0)
                onRow(readingRow);

            this.readingRow += 1;
        }

        if (value != null && value.length() > 0)
            value.delete(0, value.length());

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if ("v".equals(qName)) { // 'v' is a tag name that contains cell's value.
            isValue = true;
            return;
        }

        if ("c".equals(qName)) { // 'c' is tag name parent node of 'v'. this is cell itself.

            int firstDigit = 0;
            String references = attributes.getValue("r"); // 'r' is reference like A1, C3.
            for (int i = 0; i < references.length(); i++) {
                if (Character.isDigit(references.charAt(i))) {
                    firstDigit = i;
                    break;
                }
            }
            this.columnString = references.substring(0, firstDigit);

            readingVDataType = dataType.NUMBER;
            this.formatIndex = -1;
            this.formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleString = attributes.getValue("s");

            if ("s".equals(cellType)) {
                this.readingVDataType = dataType.SSTINDEX;
                return;
            }
            if (cellStyleString != null) {
                XSSFCellStyle style = stylesTable.getStyleAt(Integer.parseInt(cellStyleString));
                this.formatString = style.getDataFormatString();
                this.formatIndex = style.getDataFormat();
                if (this.formatString == null)
                    this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                return;
            }

        }
        // if ("row".equals(qName)) {
        //     if (readingRow == 0)
        //         return;
        //     this.moveOut = new MoveOut();
        //     this.order = new Order();
        // }
    }

    public ContentHandler(SharedStrings sharedStrings, StylesTable stylesTable) {
        this.sharedStringsTable = sharedStrings;
        this.stylesTable = stylesTable;

        columnHeaderMapper = new HashMap<String, String>();
    }

}
