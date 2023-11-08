package com.colbertlum.contentHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.colbertlum.entity.OnlineSalesInfo;

public class OnlineSalesInfoContentHandler extends DefaultHandler{
    enum dataType {
        NUMBER, SSTINDEX,
    }

    private static final String PARENT_SKU = "Parent SKU";
    private static final String SKU = "SKU";
    private static final String PRICE = "Price";
    private static final String STOCK = "Stock";
    private static final String PRODUCT_ID = "Product ID";
    private static final String VARIATION_ID = "Variation ID";
    private static final String PRODUCT_NAME = "Product Name";
    private static final String VARIATION_NAME = "Variation Name";


    private StylesTable stylesTable;
    private SharedStrings sharedStringsTable;
    private Map<String, String> headerPosition;
    private boolean isValue;
    private dataType readingVDataType;
    private int formatIndex;
    private String formatString;
    private int readingRow = 0;
    private String columString;
    private StringBuilder value;
    private DataFormatter dataFormatter;

    private OnlineSalesInfo info;
    private List<OnlineSalesInfo> infoList;

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

            if (readingRow == 2 && string != null) {
                switch (string) {
                    case PARENT_SKU:
                        headerPosition.put(columString, PARENT_SKU);
                        break;
                    case SKU:
                        headerPosition.put(columString, SKU);
                        break;
                    case STOCK:
                        headerPosition.put(columString, STOCK);
                        break;
                    case PRODUCT_ID:
                        headerPosition.put(columString, PRODUCT_ID);
                        break;
                    case PRICE:
                        headerPosition.put(columString, PRICE);
                        break;
                    case VARIATION_ID:
                        headerPosition.put(columString, VARIATION_ID);
                        break;
                    case PRODUCT_NAME:
                        headerPosition.put(columString, PRODUCT_NAME);
                        break;
                    case VARIATION_NAME:
                        headerPosition.put(columString, VARIATION_NAME);
                }
                return;
            }

            String column = "";
            
            if (readingRow >= 6 && headerPosition.containsKey(columString))
                column = headerPosition.get(columString);
            switch (column) {
                case PARENT_SKU:
                    info.setParentSku(string);
                    break;
                case SKU:
                    info.setSku(string);
                    break;
                case PRICE:
                    info.setPrice(Double.parseDouble(string));
                    break;
                case STOCK:
                    info.setQuantity(Integer.parseInt(string));
                    break;
                case PRODUCT_ID:
                    info.setProductId(string);
                    break;
                case VARIATION_ID:
                    info.setVariationId(string);
                    break;
                case PRODUCT_NAME:
                    info.setProductName(string);
                    break;
                case VARIATION_NAME:
                    info.setVariationName(string);
                    break;
                default:
                    break;
            }
        }

        if ("row".equals(qName)) {
            if (readingRow >= 6)
                if(info.getProductId() != null && info.getProductId() != ""){

                    info.setFoundRow(readingRow);
                    infoList.add(info);
                }
            this.readingRow += 1;
            this.info = null;
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
            // this.columnPosition = columnReferenceToPosition(references.substring(0, firstDigit));
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
            this.info = new OnlineSalesInfo();
        }
    }

    public OnlineSalesInfoContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            ArrayList<OnlineSalesInfo> onlineSalesInfos) {
        this.sharedStringsTable = sharedStrings;
        this.stylesTable = stylesTable;
        this.infoList = onlineSalesInfos;

        this.dataFormatter = new DataFormatter();
        headerPosition = new HashMap<String, String>();
    }
}
