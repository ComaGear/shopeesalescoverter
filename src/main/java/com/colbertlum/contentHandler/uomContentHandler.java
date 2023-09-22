package com.colbertlum.contentHandler;

import java.util.ArrayList;
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
import com.colbertlum.entity.UOM;

public class uomContentHandler extends DefaultHandler{
   enum dataType {
        NUMBER, SSTINDEX,
    }

    private static final String ID = "IM_ITEMNO";
    private static final String UOM = "IU_UOM";
    private static final String RATE = "IU_RATE";
    private static final String DESCIPTION = "IM_DESCRIPTION";

    private ArrayList<UOM> UOMs;
    private StylesTable stylesTable;
    private SharedStrings sharedStringsTable;
    private Map<String, Integer> headerPosition;
    private boolean isValue;
    private dataType readingVDataType;
    private int formatIndex;
    private String formatString;
    private int readingRow = 0;
    private UOM uom;
    private String columString;
    private StringBuilder value;
    private DataFormatter dataFormatter;

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
                    String sstIndex = value.toString();
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
                switch (string) {
                    case ID:
                        headerPosition.put(columString, 0);
                        break;
                    case UOM:
                        headerPosition.put(columString, 1);
                        break;
                    case RATE:
                        headerPosition.put(columString, 2);
                        break;
                    case DESCIPTION:
                        headerPosition.put(columString, 3);
                }
                return;
            }

            Integer integer = -1;
            if (headerPosition.containsKey(columString))
                integer = headerPosition.get(columString);
            switch (integer) {
                case 0:
                    uom.setProductId(string);
                    break;
                case 1:
                    uom.setUom(string);
                    break;
                case 2:
                    uom.setRate(Double.parseDouble(string));
                    break;
                case 3:
                    uom.setDescription(string);
                default:
                    break;
            }
            return;
        }

        if ("row".equals(qName)) {
            if (readingRow > 0)
                if(uom.getProductId() != null && uom.getRate() == 1){
                    UOMs.add(uom);
                } 
            this.readingRow += 1;
            this.uom = null;
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
            this.columString = references.substring(0, firstDigit);

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
        if ("row".equals(qName)) {
            if (readingRow == 0)
                return;
            this.uom = new UOM();
        }
    }

    public uomContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            ArrayList<UOM> UOMs) {
        this.sharedStringsTable = sharedStrings;
        this.stylesTable = stylesTable;
        this.UOMs = UOMs;

        this.dataFormatter = new DataFormatter();
        headerPosition = new HashMap<String, Integer>();
    }
}

