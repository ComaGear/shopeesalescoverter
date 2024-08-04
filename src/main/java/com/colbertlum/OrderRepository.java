package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.contentHandler.MeasContentHandler;
import com.colbertlum.contentHandler.OrderTrackingContentHandler;
import com.colbertlum.contentHandler.RepositoryItemMovementStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryOrderStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryReturnMovementContentHandler;
import com.colbertlum.entity.ItemMovementStatus;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class OrderRepository {

    List<Order> orders;
    List<Order> shippingOrders;
    List<Order> completedOrders;
    List<Order> returnAfterShippingOrders;
    List<Order> returnAfterCompletedOrders;
    private List<MoveOut> moveOutList;
    private List<ReturnMoveOut> returnMoveOuts;

    public List<Order> getReturnAfterShippingOrders() {
        return returnAfterShippingOrders;
    }

    public List<Order> getReturnAfterCompletedOrders() {
        return returnAfterCompletedOrders;
    }

    public void setShippingOrders(List<Order> shippingOrders) {
        this.shippingOrders = shippingOrders;
    }

    public void addCompletedOrders(List<Order> newCompletedOrders){
        completedOrders.addAll(newCompletedOrders);
    }
    
    public List<Order> getShippingOrders() {
        return shippingOrders;
    }
    public List<Order> getCompletedOrders() {
        return completedOrders;
    }

    private void loadRepository(){


        orders = new ArrayList<Order>();
        shippingOrders = new ArrayList<Order>();;
        completedOrders = new ArrayList<Order>();;
        returnAfterShippingOrders = new ArrayList<Order>();;
        returnAfterCompletedOrders = new ArrayList<Order>();;

        moveOutList = new ArrayList<MoveOut>();
        returnMoveOuts = new ArrayList<ReturnMoveOut>();

        try {
            File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            // MeasContentHandler contentHandler = new MeasContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(),
            //     measList);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            // xmlReader.setContentHandler(contentHandler);
            Iterator<InputStream> inputIterator = xssfReader.getSheetsData();
            if(inputIterator instanceof XSSFReader.SheetIterator){
                XSSFReader.SheetIterator sheetIterator = (SheetIterator) inputIterator;
                while (sheetIterator.hasNext()) {
                    InputStream inputStream = sheetIterator.next();
                    String sheetName = sheetIterator.getSheetName();
                    if(sheetName.equals("Order Status")){

                        RepositoryOrderStatusContentHandler contentHandler = new RepositoryOrderStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), orders);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals("Movement")) {

                        RepositoryItemMovementStatusContentHandler contentHandler = new RepositoryItemMovementStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOutList);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals("Return Movement")){

                        RepositoryReturnMovementContentHandler contentHandler = new RepositoryReturnMovementContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), );
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));
                        
                    }
                }
            }
        } catch (IOException | OpenXML4JException e) {
            Alert warningStage = new Alert(AlertType.ERROR, "you must select meas file");
            warningStage.showAndWait();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        orders.sort((o1, o2) -> {
            return o1.getId().compareTo(o2.getId());
        });
        for(MoveOut moveOut : moveOutList){
            SoftReference<MoveOut> softMoveOut = new SoftReference<MoveOut>(moveOut);
            Order lookupOrder = Lookup.lookupOrder(orders, moveOut.getOrderId());
            if(lookupOrder != null && lookupOrder.getMoveOutList() != null){
                lookupOrder.getMoveOutList().add(softMoveOut);
            } else if(lookupOrder != null){
                lookupOrder.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
                lookupOrder.getMoveOutList().add(softMoveOut);
            }
        }
        for(Order order : orders){
            if(order.getStatus().equals(OrderService.STATUS_SHIPPING)){
                shippingOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_COMPLETE) && order.isRequestApproved()){
                returnAfterCompletedOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null){
                returnAfterShippingOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_COMPLETE)){
                completedOrders.add(order);
            }
        }
    }

    public void saveToRepository(List<Order> order){

        // ask user comfirm process will update to repository or skip following part.
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure this process is valid and save which order to repository");
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() && result.get() != ButtonType.OK){
            return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(
                new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH)));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet orderSheet = workbook.getSheetAt(0);
            Sheet movementSheet = workbook.getSheetAt(1);
            Sheet returnMovementSheet = workbook.getSheetAt(2);

            

        }
    }

    public OrderRepository(){
        loadRepository();
    }

    public void addInReturnMoveOut(ArrayList<ReturnMoveOut> returningMoveOuts) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addInReturnMoveOut'");
    }

    public void removeCompletedOrders(ArrayList<Order> newReturnAfterShippingOrders) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeCompletedOrders'");
    }

    public void addReturnAfterShippingOrder(ArrayList<Order> newReturnAfterShippingOrders) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addReturnAfterShippingOrder'");
    }

    public void addReturnAfterCompletedOrder(ArrayList<Order> newReturnAfterCompletedOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addReturnAfterCompletedOrder'");
    }

    public void removeShippingOrders(ArrayList<Order> newReturnAfterShippingOrders) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeShippingOrders'");
    }

    public List<MoveOut> getAllMoveOuts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllMoveOuts'");
    }

    public void submitTransaction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'submitTransaction'");
    }
}
