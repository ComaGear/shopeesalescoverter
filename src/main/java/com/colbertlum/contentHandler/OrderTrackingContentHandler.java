package com.colbertlum.contentHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.colbertlum.entity.OrderStatusTracking;

public class OrderTrackingContentHandler extends DefaultHandler {
    
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";


    enum dataType {
        NUMBER, SSTINDEX,
    }

    public static final String STATUS_CANCEL = "Cancelled";
    public static final String STATUS_COMPLETE = "Completed";
    public static final String STATUS_TO_SHIP = "To ship";
    public static final String STATUS_UNPAID = "Unpaid";
    public static final String STATUS_SHIPPING = "Shipping";


    private static final String SHIP_TIME = "Ship Time";
    private static final String ORDER_ID = "Order ID";
    private static final String REPORT_ORDER_STATUS = "Order Status";
    private static final String RETURN_REFUND_STATUS = "Return / Refund Status";
    private static final String CANCEL_REASON = "Cancel reason";
    private static final String TRACKING_NUMBER = "Tracking Number*";
    private static final String ORDER_COMPLETE_TIME = "Order Complete Time";


    private StylesTable stylesTable;
    private SharedStrings sharedStringsTable;
    private Map<String, String> headerPosition;
    private boolean isValue;
    private dataType readingVDataType;
    private int formatIndex;
    private String formatString;
    private int readingRow = 0;
    private String columnString;
    private StringBuilder value;
    private DataFormatter dataFormatter;

    private List<OrderStatusTracking> trackings;
    private OrderStatusTracking tracking;

    // private ArrayList<MoveOut> moveOuts;
    // private Map<String, Order> orderMap;
    // private MoveOut moveOut;
    // private Order order;

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
                        headerPosition.put(columnString, ORDER_ID);
                        break;
                    case SHIP_TIME:
                        headerPosition.put(columnString, SHIP_TIME);
                        break;
                    case REPORT_ORDER_STATUS:
                        headerPosition.put(columnString, REPORT_ORDER_STATUS);
                        break;
                    case RETURN_REFUND_STATUS:
                        headerPosition.put(columnString, RETURN_REFUND_STATUS);
                        break;
                    case CANCEL_REASON:
                        headerPosition.put(columnString, CANCEL_REASON);
                        break;
                    case TRACKING_NUMBER:
                        headerPosition.put(columnString, TRACKING_NUMBER);
                        break;
                    case ORDER_COMPLETE_TIME:
                        headerPosition.put(columnString, ORDER_COMPLETE_TIME);
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
            
            if (headerPosition.containsKey(columnString))
                column = headerPosition.get(columnString);
            switch (column) {
                case ORDER_ID:
                    tracking.setOrderId(string);
                    break;
                case SHIP_TIME:
                    if(string.isEmpty()) break;
                    tracking.setShipOutDate(LocalDateTime.parse(string, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                    break;
                case REPORT_ORDER_STATUS:
                    tracking.setReportStatus(string);
                    break;
                case RETURN_REFUND_STATUS:
                    if(!string.isEmpty()) tracking.setRequestApproved(true);
                    break;
                case CANCEL_REASON:
                    if(string != null && !string.isEmpty() && string.equals(OrderStatusTracking.CANCEL_REASON_FAILED_DELIVERY)){
                        tracking.setCancelled(true);
                    }
                    break;
                case TRACKING_NUMBER:
                    tracking.setTrackingNumber(string);
                    break;
                case ORDER_COMPLETE_TIME:
                    if(string.isEmpty()) break;
                    tracking.setOrderCompletedTime(LocalDateTime.parse(string, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                    break;
                default:
                    break;
            }
        }

        if ("row".equals(qName)) {
            if (readingRow > 0) {
                OrderStatusTracking lastTracking = null;
                if(!trackings.isEmpty()) {
                    lastTracking = trackings.get(trackings.size() - 1);
                }

                if(tracking.getOrderId() != null && !lastTracking.getOrderId().equals(tracking.getOrderId())){
                    trackings.add(tracking);
                }
            }
            this.readingRow += 1;
            this.tracking = null;
        }

        if (value != null && value.length() > 0) {
            value.delete(0, value.length());
        }
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
        if ("row".equals(qName)) {
            if (readingRow == 0)
                return;
            this.tracking = new OrderStatusTracking();
        }
    }

    public List<OrderStatusTracking> getTracking(){
        return this.trackings;
    }

    public OrderTrackingContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            ArrayList<OrderStatusTracking> trackings) {
        this.sharedStringsTable = sharedStrings;
        this.stylesTable = stylesTable;
        this.trackings = trackings;

        this.dataFormatter = new DataFormatter();
        headerPosition = new HashMap<String, String>();
    }
}
