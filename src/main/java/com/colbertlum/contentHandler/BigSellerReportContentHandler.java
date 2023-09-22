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

import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class BigSellerReportContentHandler extends DefaultHandler {

    enum dataType {
        NUMBER, SSTINDEX,
    }

    private static final String ORDER_ID = "Order No";
    private static final String ORDER_TOTAL = "Order Total";
    private static final String MANAGEMENT_FEE = "Management Fee";
    private static final String TRANSACTION_FEE = "Transaction Fee";
    private static final String SHIPPING_FEE = "Shipping Fee";


    private static final String SKU = "SKU";
    private static final String VARIATION_NAME = "Variation Name";
    private static final String PRODUCT_NAME = "Product Name";
    private static final String PRICE = "Price";
    private static final String QUANTITY = "Quantity";


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

    private ArrayList<MoveOut> moveOuts;
    private Map<String, Order> orderMap;
    private MoveOut moveOut;
    private Order order;

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
                switch (string) {
                    case ORDER_ID:
                        headerPosition.put(columString, ORDER_ID);
                        break;
                    case ORDER_TOTAL:
                        headerPosition.put(columString, ORDER_TOTAL);
                        break;
                    case MANAGEMENT_FEE:
                        headerPosition.put(columString, MANAGEMENT_FEE);
                        break;
                    case TRANSACTION_FEE:
                        headerPosition.put(columString, TRANSACTION_FEE);
                        break;
                    case SHIPPING_FEE:
                        headerPosition.put(columString, SHIPPING_FEE);
                        break;
                    case SKU:
                        headerPosition.put(columString, SKU);
                        break;
                    case VARIATION_NAME:
                        headerPosition.put(columString, VARIATION_NAME);
                        break;
                    case PRODUCT_NAME:
                        headerPosition.put(columString, PRODUCT_NAME);
                        break;
                    case PRICE:
                        headerPosition.put(columString, PRICE);
                        break;
                    case QUANTITY:
                        headerPosition.put(columString, QUANTITY);
                        break;
                }
                return;
            }

            String column = "";
            // integer = this.columnPosition;
            // switch (integer) {
            //     case 0:
            //         moveOut.setProductId(string);
            //         break;
            //     case 2:
            //         moveOut.setUom(string);
            //         break;
            //     case 4:
            //         moveOut.setQuantity(Float.parseFloat(string));
            //         break;
            //     default:
            //         break;
            // }
            
            if (headerPosition.containsKey(columString))
                column = headerPosition.get(columString);
            switch (column) {
                case ORDER_ID:
                    order.setId(string);
                    break;
                case ORDER_TOTAL:
                    order.setOrderTotalAmount(Double.parseDouble(string));
                    break;
                case MANAGEMENT_FEE:
                    order.setManagementFee(Double.parseDouble(string));
                    break;
                case TRANSACTION_FEE:
                    order.setTransactionFee(Double.parseDouble(string));
                    break;
                case SHIPPING_FEE:
                    order.setShippingFee(Double.parseDouble(string));
                    break;
                case SKU:
                    moveOut.setSku(string);
                    break;
                case VARIATION_NAME:
                    moveOut.setVariationName(string);
                    break;
                case PRODUCT_NAME:
                    moveOut.setProductName(string);
                    break;
                case PRICE:
                    moveOut.setPrice(Double.parseDouble(string));
                    break;
                case QUANTITY:
                    moveOut.setQuantity(Double.parseDouble(string));
                    break;
                default:
                    break;
            }
        }

        if ("row".equals(qName)) {
            if (readingRow > 0)
                if(moveOut.getProductName() != null && moveOut.getProductName() != ""){

                    if(!orderMap.containsKey(order.getId())){
                        orderMap.put(order.getId(), order);
                    }

                    moveOut.setOrder(orderMap.get(order.getId()));
                    moveOut.setFoundRow(readingRow);
                    moveOuts.add(moveOut);
                }
            this.readingRow += 1;
            this.moveOut = null;
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
            this.moveOut = new MoveOut();
            this.order = new Order();
        }
    }

    // private int columnReferenceToPosition(String reference) {
    //     int column = -1;
    //     for (int i = 0; i < reference.length(); ++i) {
    //         int c = reference.charAt(i);
    //         column = (column + 1) * 26 + c - 'A';
    //     }
    //     return column;
    // }

    public List<MoveOut> getMoveOuts(){
        return this.moveOuts;
    }

    public List<Order> getOrders(){
        return new ArrayList<>(orderMap.values());
    }


    public BigSellerReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            ArrayList<MoveOut> moveOuts) {
        this.sharedStringsTable = sharedStrings;
        this.stylesTable = stylesTable;
        this.moveOuts = moveOuts;

        this.dataFormatter = new DataFormatter();
        headerPosition = new HashMap<String, String>();
        this.orderMap = new HashMap<String, Order>();
    }
}
