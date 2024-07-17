package com.colbertlum.Imputer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hpsf.Array;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.contentHandler.BigSellerReportContentHandler;
import com.colbertlum.contentHandler.OrderTrackingContentHandler;
import com.colbertlum.contentHandler.RepositoryItemMovementStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryOrderStatusContentHandler;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.entity.ItemMovementStatus;
import com.colbertlum.entity.OrderStatusTracking;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class OrderTrackingImputer {

    public static String STATUS_COMPLETED = "COMPLETED";
    public static String STATUS_SHIPPING = "SHIPPING";
    public static String STATUS_PENDING = "PENDING";
    public static String STATUS_CANCELLED = "CANCELLED";
    public static String STATUS_RETURNING = "RETURNING";

    private Map<String, List<OrderStatusTracking>> trackingsMap;

    // private Map<String, status> orders;

    public List<OrderStatusTracking> getTrackings(String key){
        if(!trackingsMap.containsKey(key)) return null;

        return trackingsMap.get(key);
    }

    public void setTracking(String key, OrderStatusTracking orderStatusTracking) {
        if(key == null || key.isEmpty() || orderStatusTracking == null) return;

        trackingsMap.get(key).add(orderStatusTracking);
    }

    public void putTrackings(List<OrderStatusTracking> trackings){
        for(OrderStatusTracking tracking : trackings){
            if(tracking.getReportStatus().equals(OrderTrackingContentHandler.STATUS_COMPLETE)
                && !tracking.isRequestApproved()){

                trackingsMap.get(STATUS_COMPLETED).add(tracking);
                return;
            }
            if(tracking.getReportStatus().equals(OrderTrackingContentHandler.STATUS_SHIPPING)){

                trackingsMap.get(STATUS_SHIPPING).add(tracking);
                return;
            }
            if(tracking.getReportStatus().equals(OrderTrackingContentHandler.STATUS_TO_SHIP)
                || tracking.getReportStatus().equals(OrderTrackingContentHandler.STATUS_UNPAID)){

                trackingsMap.get(STATUS_PENDING).add(tracking);
                return;
            }
            if(tracking.getReportStatus().equals(OrderTrackingContentHandler.STATUS_CANCEL)){

                trackingsMap.get(STATUS_CANCELLED).add(tracking);
                return;
            }
            if(tracking.getReportStatus().equals(OrderTrackingContentHandler.STATUS_COMPLETE)
                && tracking.isRequestApproved()){

                trackingsMap.get(STATUS_RETURNING).add(tracking);
                return;
            }
        }
    }

    private void lookupValidInReturn(Map<String, List<OrderStatusTracking>> reportTrackingMap, Map<String, List<OrderStatusTracking>> repositoryTrackingMap
        , List<ItemMovementStatus> repositoryMovementStatusList){

        List<OrderStatusTracking> InReturnList = new ArrayList<OrderStatusTracking>();
        List<OrderStatusTracking> newReturnList = new ArrayList<OrderStatusTracking>();
        
        List<OrderStatusTracking> reportReturnList = reportTrackingMap.get(STATUS_RETURNING);
        List<OrderStatusTracking> repositoryReturnList = repositoryTrackingMap.get(STATUS_RETURNING);
        Comparator<OrderStatusTracking> comparator = new Comparator<OrderStatusTracking>() {

            @Override
            public int compare(OrderStatusTracking o1, OrderStatusTracking o2) {
                return o1.getOrderId().compareTo(o2.getOrderId());
            }
            
        };
        repositoryMovementStatusList.sort(new Comparator<ItemMovementStatus>() {

            @Override
            public int compare(ItemMovementStatus o1, ItemMovementStatus o2) {
                if(o1.getOrderId().equals(o2.getOrderId())) {
                    return o1.getSku().compareTo(o2.getSku());
                } else {
                    return o1.getOrderId().compareTo(o2.getOrderId());
                }
            }
            
        });
        reportReturnList.sort(comparator);
        repositoryReturnList.sort(comparator);

        reportReturnList.forEach((e) -> {
            OrderStatusTracking result = Lookup.lookupOrderStatusTracking(repositoryReturnList, e.getOrderId());
            
        });

    }

    public void loadRepository() throws InvalidFormatException, IOException, OpenXML4JException, SAXException, ParserConfigurationException{
        File file = new File("./OrderStatusRepository.xlsx");
        if(!file.exists()) {
            new Alert(AlertType.ERROR, "OrderStatusRepository File is not exists").showAndWait();
            throw new RuntimeException("OrderStatusRepository File is not exists");
        }

        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        XMLReader xmlReader = XMLHelper.newXMLReader();

        Iterator<InputStream> sheets = xssfReader.getSheetsData();
        SheetIterator iterator = null;

        if(sheets instanceof SheetIterator){
            iterator = (SheetIterator) sheets;
        }

        InputStream sheetData = null;
        String sheetName;
        Map<String, List<ItemMovementStatus>> ItemStatusMap = new HashMap<String, List<ItemMovementStatus>>();
        Map<String, List<OrderStatusTracking>> trackingMap = new HashMap<String, List<OrderStatusTracking>>();
        if(iterator.hasNext()){
            sheetData = iterator.next();
            sheetName = iterator.getSheetName();

            if(sheetName.equals("OrderStatus")){

                RepositoryOrderStatusContentHandler contentHandler = 
                    new RepositoryOrderStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), trackingMap);
                xmlReader.setContentHandler(contentHandler);
                xmlReader.parse(new InputSource(sheetData));
            }

            if(sheetName.equals("movementStatusDetail")){

                RepositoryItemMovementStatusContentHandler contectHandler = 
                    new RepositoryItemMovementStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), ItemStatusMap);
                xmlReader.setContentHandler(contectHandler);
                xmlReader.parse(new InputSource(sheetData));
            }
        }

    }

    public void saveFile(){
        
    }

    public void createFile(){
        
    }

    public OrderTrackingImputer(){
        trackingsMap = new HashMap<String, List<OrderStatusTracking>>();
        trackingsMap.put(STATUS_COMPLETED, new ArrayList<OrderStatusTracking>());
        trackingsMap.put(STATUS_SHIPPING, new ArrayList<OrderStatusTracking>());
        trackingsMap.put(STATUS_PENDING, new ArrayList<OrderStatusTracking>());
        trackingsMap.put(STATUS_CANCELLED, new ArrayList<OrderStatusTracking>());
        trackingsMap.put(STATUS_RETURNING, new ArrayList<OrderStatusTracking>());
    }
}
