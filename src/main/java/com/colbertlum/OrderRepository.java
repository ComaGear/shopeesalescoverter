package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.Imputer.MeasImputer;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.contentHandler.RepositoryItemMovementStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryOrderStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryReturnMovementContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;
import javafx.scene.control.ButtonType;

public class OrderRepository {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private List<Order> orders;
    private List<Order> shippingOrders;
    private List<Order> completedOrders;
    private List<Order> returnAfterShippingOrders;
    private List<Order> returnAfterCompletedOrders;
    private List<MoveOut> moveOutList;
    private List<ReturnOrder> returnOrders;
    private List<ReturnMoveOut> returnMoveOuts;

    public List<Order> getOrders(){
        return orders;
    }

    public List<ReturnMoveOut> getReturnMoveOuts() {
        return returnMoveOuts;
    }

    public List<ReturnOrder> getReturnOrders() {
        return returnOrders;
    }

    public List<Order> getReturnAfterShippingOrders() {
        return returnAfterShippingOrders;
    }

    public List<Order> getReturnAfterCompletedOrders() {
        return returnAfterCompletedOrders;
    }

    public List<Order> getShippingOrders() {
        return shippingOrders;
    }
    public List<Order> getCompletedOrders() {
        return completedOrders;
    }

    public List<Order> getCompletedOrdersByLocalDate(LocalDate localDate) {
        if(localDate == null) return null;

        completedOrders.sort((o1, o2) -> {
            return o2.getOrderCompleteDate().compareTo(o1.getOrderCompleteDate());
        });

        List<Order> list = new ArrayList<Order>();
        boolean inDateRange = false;
        for(Order order : completedOrders) {
            if(order.getOrderCompleteDate().equals(localDate)) {
                list.add(order);
                inDateRange = true;
            } else if(inDateRange == true) {
                return list;
            }
        }
        if(list.isEmpty()) return null;
        return list;
    }

    public List<Order> getReturnAfterCompletedOrdersByLocalDate(LocalDate localDate) {
        if(localDate == null) return null;

        returnAfterCompletedOrders.sort((o1, o2) -> {
            return o2.getOrderCompleteDate().compareTo(o1.getOrderCompleteDate());
        });

        List<Order> list = new ArrayList<Order>();
        boolean inDateRange = false;
        for(Order order : returnAfterCompletedOrders) {
            if(order.getOrderCompleteDate().equals(localDate)) {
                list.add(order);
                inDateRange = true;
            } else if(inDateRange == true) {
                return list;
            }
        }
        if(list.isEmpty()) return null;
        return list;
    }

    private void loadRepository(){

        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));
        
        
        orders = new ArrayList<Order>();
        shippingOrders = new ArrayList<Order>();
        completedOrders = new ArrayList<Order>();
        returnAfterShippingOrders = new ArrayList<Order>();
        returnAfterCompletedOrders = new ArrayList<Order>();

        moveOutList = new ArrayList<MoveOut>();
        returnMoveOuts = new ArrayList<ReturnMoveOut>();
        returnOrders = new ArrayList<ReturnOrder>();

        try {
            if(!file.exists()) {
                createRepositoryFile();
            }

            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            // MeasContentHandler contentHandler = new MeasContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(),
            //     measList);
            // xmlReader.setContentHandler(contentHandler);
            Iterator<InputStream> inputIterator = xssfReader.getSheetsData();
            if(inputIterator instanceof XSSFReader.SheetIterator){

                XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) inputIterator;
                while (sheetIterator.hasNext()) {
                    InputStream inputStream = sheetIterator.next();
                    String sheetName = sheetIterator.getSheetName();
                    XMLReader xmlReader = XMLHelper.newXMLReader();
                    if(sheetName.equals("Orders")){

                        RepositoryOrderStatusContentHandler contentHandler = new RepositoryOrderStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), orders);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals("Movements")) {

                        RepositoryItemMovementStatusContentHandler contentHandler = new RepositoryItemMovementStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOutList);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals("Return Movements")){

                        RepositoryReturnMovementContentHandler contentHandler = new RepositoryReturnMovementContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), returnMoveOuts);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));
                                                
                    }
                    inputStream.close();
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

        MeasImputer measImputer = new MeasImputer();
        ArrayList<Meas> measList = measImputer.getMeasList();
        measList.sort((o1, o2) -> o1.getRelativeId().compareTo(o2.getRelativeId()));
        for(MoveOut moveOut : moveOutList) {
            Meas meas = measImputer.getMeas(moveOut.getSku(), measList);
            if(meas == null) continue;
            moveOut.setId(meas.getId());
        }
        for(ReturnMoveOut moveOut : returnMoveOuts) {
            Meas meas = measImputer.getMeas(moveOut.getSku(), measList);
            if(meas == null) continue;
            moveOut.setId(meas.getId());
        }

        HashMap<String, List<SoftReference<MoveOut>>> orderIdMap = new HashMap<String, List<SoftReference<MoveOut>>>();
        for(MoveOut moveOut : moveOutList){
            if(orderIdMap.containsKey(moveOut.getOrderId())){
                orderIdMap.get(moveOut.getOrderId()).add(new SoftReference<MoveOut>(moveOut));
            } else {
                List<SoftReference<MoveOut>> list = new ArrayList<SoftReference<MoveOut>>();
                list.add(new SoftReference<MoveOut>(moveOut));
                orderIdMap.put(moveOut.getOrderId(), list);
            }
        }
        for(Order order : orders){
            order.setMoveOutList(orderIdMap.get(order.getId()));
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                softMoveOut.get().setOrder(order);
            }
        }

        // orders.sort((o1, o2) -> {
        //     return o1.getId().compareTo(o2.getId());
        // });
        
        // for(MoveOut moveOut : moveOutList){
        //     SoftReference<MoveOut> softMoveOut = new SoftReference<MoveOut>(moveOut);
        //     // System.out.println("searching " + moveOut.getOrderId());
        //     Order lookupOrder = Lookup.lookupOrder(orders, moveOut.getOrderId());
        //     if(lookupOrder != null && lookupOrder.getMoveOutList() != null){
        //         if(lookupOrder.getMoveOutList() == null) lookupOrder.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        //         lookupOrder.getMoveOutList().add(softMoveOut);
        //     } else {
        //         // System.out.println("not found");
        //     }
        // }
        for(Order order : orders){
            if(order.getStatus().equals(OrderService.STATUS_SHIPPING)){
                shippingOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_DELIVERED)){
                shippingOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_COMPLETE) && order.isRequestApproved()){
                returnAfterCompletedOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null){
                returnAfterShippingOrders.add(order);
            } else if(order.getStatus().equals(OrderService.STATUS_COMPLETE)){
                completedOrders.add(order);
            } else if(order.getStatus().contains(OrderService.STATUS_RECEIVED)) {
                completedOrders.add(order);
            }
        }

        HashMap<String, List<SoftReference<ReturnMoveOut>>> returnOrderIdMap = new HashMap<String, List<SoftReference<ReturnMoveOut>>>();
        for(ReturnMoveOut returnMoveOut : returnMoveOuts){
            if(returnOrderIdMap.containsKey(returnMoveOut.getOrderId())){
                returnOrderIdMap.get(returnMoveOut.getOrderId()).add(new SoftReference<ReturnMoveOut>(returnMoveOut));
            } else {
                List<SoftReference<ReturnMoveOut>> list = new ArrayList<SoftReference<ReturnMoveOut>>();
                list.add(new SoftReference<ReturnMoveOut>(returnMoveOut));
                returnOrderIdMap.put(returnMoveOut.getOrderId(), list);
            }
        }
        for(Order order : returnAfterCompletedOrders){
            ReturnOrder returnOrder = new ReturnOrder(order);
            returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            returnOrder.setReturnMoveOutList(returnOrderIdMap.get(returnOrder.getId()));
            returnOrders.add(returnOrder);
        }
        for(Order order : returnAfterShippingOrders){
            ReturnOrder returnOrder = new ReturnOrder(order);
            returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            returnOrder.setReturnMoveOutList(returnOrderIdMap.get(returnOrder.getId()));
            returnOrders.add(returnOrder);
        }

        System.out.println("Repository Shipping Orders size : " + shippingOrders.size());
        System.out.println("Repository Completed Orders size : " + completedOrders.size());
        System.out.println("Repository returnAfterCompleted Orders size : " + returnAfterCompletedOrders.size());
        System.out.println("Repository retrunAfterShipping Orders size : " + returnAfterShippingOrders.size());
    }

    public void createRepositoryFile() throws IOException{
        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));
        XSSFWorkbook workbook;
        if(!file.exists()) file.createNewFile();
        
        workbook = new XSSFWorkbook();
        Sheet orderSheet = workbook.createSheet("Orders");
        Sheet movementSheet = workbook.createSheet("Movements");
        Sheet returnMovementSheet = workbook.createSheet("Return Movements");

        createOrderSheet(orderSheet);
        createMovementSheet(movementSheet);
        createReturnMovementSheet(returnMovementSheet);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();
    }

    private void createReturnMovementSheet(Sheet returnMovementSheet) {
        Row headerRow = returnMovementSheet.createRow(0);

        Cell orderIdCellHeader = headerRow.createCell(0);
        orderIdCellHeader.setCellValue("Order Id");

        Cell SkuCellHeader = headerRow.createCell(1);
        SkuCellHeader.setCellValue("SKU");

        Cell productNameCellHeader = headerRow.createCell(2);
        productNameCellHeader.setCellValue("Product Name");

        Cell variationNameCellHeader = headerRow.createCell(3);
        variationNameCellHeader.setCellValue("Variation Name");

        Cell quantityCellHeader = headerRow.createCell(4);
        quantityCellHeader.setCellValue("Quantity");

        Cell priceCellHeader = headerRow.createCell(5);
        priceCellHeader.setCellValue("Price");

        Cell returnStatusCellHeader = headerRow.createCell(6);
        returnStatusCellHeader.setCellValue("Return Status");

        Cell statusQuantityCellHeader = headerRow.createCell(7);
        statusQuantityCellHeader.setCellValue("Status Quantity");
    }

    private void createMovementSheet(Sheet movementSheet) {
        Row headerRow = movementSheet.createRow(0);

        Cell orderIdCellHeader = headerRow.createCell(0);
        orderIdCellHeader.setCellValue("Order Id");

        Cell SkuCellHeader = headerRow.createCell(1);
        SkuCellHeader.setCellValue("SKU");

        Cell productNameCellHeader = headerRow.createCell(2);
        productNameCellHeader.setCellValue("Product Name");

        Cell variationNameCellHeader = headerRow.createCell(3);
        variationNameCellHeader.setCellValue("Variation Name");

        Cell quantityCellHeader = headerRow.createCell(4);
        quantityCellHeader.setCellValue("Quantity");

        Cell priceCellHeader = headerRow.createCell(5);
        priceCellHeader.setCellValue("Price");
    }

    private void createOrderSheet(Sheet orderSheet) {
        
        Row headerRow = orderSheet.createRow(0);

        Cell orderIdCellHeader = headerRow.createCell(0);
        orderIdCellHeader.setCellValue("Order Id");

        Cell trackingNumberCellHeader = headerRow.createCell(1);
        trackingNumberCellHeader.setCellValue("Tracking Number");

        Cell creationDateCellHeader = headerRow.createCell(2);
        creationDateCellHeader.setCellValue("Creation Date");

        Cell shipOutDateCellHeader = headerRow.createCell(3);
        shipOutDateCellHeader.setCellValue("ShipOut Date");

        Cell completedDateCellHeader = headerRow.createCell(4);
        completedDateCellHeader.setCellValue("Completed Date");

        Cell RequestReturnRefundCellHeader = headerRow.createCell(5);
        RequestReturnRefundCellHeader.setCellValue("Request Return/Refund");

        Cell statusCellHeader = headerRow.createCell(6);
        statusCellHeader.setCellValue("Status");

        Cell orderTotalAmountCellHeader = headerRow.createCell(7);
        orderTotalAmountCellHeader.setCellValue("Order Total Amount");

        Cell managementFeeCellHeader = headerRow.createCell(8);
        managementFeeCellHeader.setCellValue("Management Fee");

        Cell transactionFeeCellHeader = headerRow.createCell(9);
        transactionFeeCellHeader.setCellValue("Transaction Fee");

        Cell serviceFeeCellHeader = headerRow.createCell(10);
        serviceFeeCellHeader.setCellValue("Service Fee");

        Cell CommissionFeeCellHeader = headerRow.createCell(11);
        CommissionFeeCellHeader.setCellValue("Commission Fee");

        Cell shopeeVoucherCellHeader = headerRow.createCell(12);
        shopeeVoucherCellHeader.setCellValue("Shopee Voucher");

        Cell shippingFeeCellHeader = headerRow.createCell(13);
        shippingFeeCellHeader.setCellValue("Shipping Fee");

        Cell shippingRebateCellHeader = headerRow.createCell(14);
        shippingRebateCellHeader.setCellValue("Shipping Rebate");

    }

    public void saveToRepository(List<Order> orders) throws IOException{

        String dateString = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_REMAIN_AFTER_DATE);
        if(dateString != null && !dateString.isEmpty()){
            LocalDate remainAfterDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_PATTERN));
            orders.removeIf(order -> order.getShipOutDate().isBefore(remainAfterDate));
            if(orders.isEmpty()) return;
        }

        // ask user comfirm process will update to repository or skip following part.
        if(!Window.getWindows().isEmpty()) {
            try {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setContentText("Are you sure this process is valid and save which order to repository");
                Optional<ButtonType> result = alert.showAndWait();
                if(!result.isPresent() || result.get() != ButtonType.OK){
                    return;
                }
            } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        }

        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));

        if(!file.exists()) createRepositoryFile();

        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(fileInputStream);
        XSSFSheet orderSheet = workbook.getSheetAt(0);
        Sheet movementSheet = workbook.getSheetAt(1);
        Sheet returnMovementSheet = workbook.getSheetAt(2);

        writeOrderSheetCell(orderSheet, orders);
        writeMovementSheetCell(movementSheet, moveOutList);
        writeReturnMovementSheetCell(returnMovementSheet, returnMoveOuts);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    private void writeReturnMovementSheetCell(Sheet returnMovementSheet, List<ReturnMoveOut> returnMoveOuts) {

        // clean
        int lastRowNum = returnMovementSheet.getLastRowNum();
        for (int i = lastRowNum; i >= 1; i--) {
            Row row = returnMovementSheet.getRow(i);
            if (row != null) {
                returnMovementSheet.removeRow(row);
            }
        }

        int index = 1;
        if(returnMoveOuts == null) return;
        for(ReturnMoveOut returnMoveOut : returnMoveOuts){
            Row row = returnMovementSheet.createRow(index);
            index++;

            Cell orderIdCell = row.getCell(0);
            if(orderIdCell == null) orderIdCell = row.createCell(0);
            orderIdCell.setCellValue(returnMoveOut.getOrderId());

            Cell skuCell = row.getCell(1);
            if(skuCell == null) skuCell = row.createCell(1);
            skuCell.setCellValue(returnMoveOut.getSku());

            Cell productNameCell = row.getCell(2);
            if(productNameCell == null) productNameCell = row.createCell(2);
            productNameCell.setCellValue(returnMoveOut.getProductName());

            Cell variationNameCell = row.getCell(3);
            if(variationNameCell == null) variationNameCell = row.createCell(3);
            variationNameCell.setCellValue(returnMoveOut.getVariationName());

            Cell quantityCell = row.getCell(4);
            if(quantityCell == null) quantityCell = row.createCell(4);
            quantityCell.setCellValue(returnMoveOut.getQuantity());
            
            Cell priceCell = row.getCell(5);
            if(priceCell == null) priceCell = row.createCell(5);
            priceCell.setCellValue(returnMoveOut.getPrice());
            
            Cell returnStatusCell = row.getCell(6);
            if(returnStatusCell == null) returnStatusCell = row.createCell(6);
            returnStatusCell.setCellValue(returnMoveOut.getReturnStatus());

            Cell statusQuantityCell = row.getCell(7);
            if(statusQuantityCell == null) statusQuantityCell = row.createCell(7);
            statusQuantityCell.setCellValue(returnMoveOut.getStatusQuantity());
        }

        while(returnMovementSheet.getRow(index) != null) {
            Row row = returnMovementSheet.getRow(index);
            returnMovementSheet.removeRow(row);
        }
    }

    private void writeMovementSheetCell(Sheet movementSheet, List<MoveOut> moveOuts) {

        // clean
        int lastRowNum = movementSheet.getLastRowNum();
        for (int i = lastRowNum; i >= 1; i--) {
            Row row = movementSheet.getRow(i);
            if (row != null) {
                movementSheet.removeRow(row);
            }
        }

        int index = 1;
        if(moveOuts == null) return;
        for(MoveOut moveOut : moveOuts){
            Row row = movementSheet.createRow(index);
            index++;

            Cell orderIdCell = row.getCell(0);
            if(orderIdCell == null) orderIdCell = row.createCell(0);
            orderIdCell.setCellValue(moveOut.getOrderId());

            Cell skuCell = row.getCell(1);
            if(skuCell == null) skuCell = row.createCell(1);
            skuCell.setCellValue(moveOut.getSku());

            Cell productNameCell = row.getCell(2);
            if(productNameCell == null) productNameCell = row.createCell(2);
            productNameCell.setCellValue(moveOut.getProductName());

            Cell variationNameCell = row.getCell(3);
            if(variationNameCell == null) variationNameCell = row.createCell(3);
            variationNameCell.setCellValue(moveOut.getVariationName());

            Cell quantityCell = row.getCell(4);
            if(quantityCell == null) quantityCell = row.createCell(4);
            quantityCell.setCellValue(moveOut.getQuantity());
            
            Cell priceCell = row.getCell(5);
            if(priceCell == null) priceCell = row.createCell(5);
            priceCell.setCellValue(moveOut.getPrice());
        }

        while(movementSheet.getRow(index) != null) {
            Row row = movementSheet.getRow(index);
            movementSheet.removeRow(row);
        }
    }

    private void writeOrderSheetCell(XSSFSheet orderSheet, List<Order> orders) {

        // clean
        int lastRowNum = orderSheet.getLastRowNum();
        for (int i = lastRowNum; i >= 1; i--) {
            Row row = orderSheet.getRow(i);
            if (row != null) {
                orderSheet.removeRow(row);
            }
        }

        int index = 1;
        if(orders == null) return;
        for(Order order : orders){
            XSSFRow row = orderSheet.createRow(index);
            index++;

            Cell orderIdCell = row.getCell(0);
            if(orderIdCell == null) orderIdCell = row.createCell(0);
            orderIdCell.setCellValue(order.getId());

            Cell trackingNumberCell = row.getCell(1);
            if(trackingNumberCell == null) trackingNumberCell = row.createCell(1);
            trackingNumberCell.setCellValue(order.getTrackingNumber());

            Cell creationDateCell = row.getCell(2);
            if(creationDateCell == null) creationDateCell = row.createCell(2);
            creationDateCell.setCellValue(order.getOrderCreationDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

            Cell shipOutDateCell = row.getCell(3);
            if(shipOutDateCell == null) shipOutDateCell = row.createCell(3);
            shipOutDateCell.setCellValue(order.getShipOutDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

            Cell completedDateCell = row.getCell(4);
            if(completedDateCell == null) completedDateCell = row.createCell(4);
            if(order.getOrderCompleteDate() != null)
                completedDateCell.setCellValue(order.getOrderCompleteDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            
            Cell RequestReturnRefundCell = row.getCell(5);
            if(RequestReturnRefundCell == null) RequestReturnRefundCell = row.createCell(5);
            RequestReturnRefundCell.setCellValue(order.isRequestApproved() ? "Request Approved" : "");

            Cell statusCell = row.getCell(6);
            if(statusCell == null) statusCell = row.createCell(6);
            statusCell.setCellValue(order.getStatus());

            Cell orderTotalAmountCell = row.getCell(7);
            if(orderTotalAmountCell == null) orderTotalAmountCell = row.createCell(7);
            orderTotalAmountCell.setCellValue(order.getOrderTotalAmount());

            Cell managementFeeCell = row.getCell(8);
            if(managementFeeCell == null) managementFeeCell = row.createCell(8);
            managementFeeCell.setCellValue(order.getManagementFee());

            Cell transactionFeeCell = row.getCell(9);
            if(transactionFeeCell == null) transactionFeeCell = row.createCell(9);
            transactionFeeCell.setCellValue(order.getTransactionFee());

            Cell serviceFeeCell = row.getCell(10);
            if(serviceFeeCell == null) serviceFeeCell = row.createCell(10);
            serviceFeeCell.setCellValue(order.getServiceFee());

            Cell commissionFeeCell = row.getCell(11);
            if(commissionFeeCell == null) commissionFeeCell = row.createCell(11);
            commissionFeeCell.setCellValue(order.getCommissionFee());

            Cell shopeeVoucherCell = row.getCell(12);
            if(shopeeVoucherCell == null) shopeeVoucherCell = row.createCell(12);
            shopeeVoucherCell.setCellValue(order.getServiceFee());

            Cell shippingFeeCell = row.getCell(13);
            if(shippingFeeCell == null) shippingFeeCell = row.createCell(13);
            shippingFeeCell.setCellValue(order.getShippingFee());

            Cell shippingRebateCell = row.getCell(14);
            if(shippingRebateCell == null) shippingRebateCell = row.createCell(14);
            shippingRebateCell.setCellValue(order.getServiceFee());
        }

        while(orderSheet.getRow(index) != null) {
            Row row = orderSheet.getRow(index);
            orderSheet.removeRow(row);
        }
    }

    public OrderRepository(boolean loadingRepository){
        if(loadingRepository) {
            loadRepository();
        }
    }

    public void removeCompletedOrders(List<Order> removeOrders) {

        for(Order order : removeOrders){
            Order lookupOrder = Lookup.lookupOrder(completedOrders, order.getId());
            if(lookupOrder != null) {
                completedOrders.remove(lookupOrder);
                orders.remove(lookupOrder);

                List<SoftReference<MoveOut>> softMoveOuts = lookupOrder.getMoveOutList();
                ArrayList<MoveOut> removeMoveOuts = new ArrayList<MoveOut>();
                for(SoftReference<MoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                moveOutList.removeAll(removeMoveOuts);
            }
        }
    }

    public void addReturnAfterShippingOrder(List<Order> newReturnAfterShippingOrders) {
        orders.addAll(newReturnAfterShippingOrders);
        returnAfterShippingOrders.addAll(newReturnAfterShippingOrders);

        ArrayList<ReturnMoveOut> returningMoveOuts = new ArrayList<ReturnMoveOut>();
        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        for(Order order : newReturnAfterShippingOrders){
            ReturnOrder returnOrder = new ReturnOrder(order);
            if(order.isRequestApproved() && order.getStatus().equals((OrderService.STATUS_COMPLETE))) {
                returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            } else if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null) {
                returnOrder.setReturnType(ReturnOrder.FAILED_DELIVERY_TYPE);
            }
            returnOrders.add(returnOrder);
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                MoveOut moveOut = softMoveOut.get();
                ReturnMoveOut returnMoveOut = new ReturnMoveOut(moveOut);
                returningMoveOuts.add(returnMoveOut);
                returnMoveOut.setReturnOrder(returnOrder);
                moveOuts.add(moveOut);
            }
        }
        returnMoveOuts.addAll(returningMoveOuts);
        this.moveOutList.addAll(moveOuts);
    }

    public void addCompletedOrders(List<Order> newCompletedOrders){
        orders.addAll(newCompletedOrders);
        completedOrders.addAll(newCompletedOrders);
        for(Order order : newCompletedOrders){
            List<SoftReference<MoveOut>> moveOuts = order.getMoveOutList();
            for(SoftReference<MoveOut> softMoveOut : moveOuts){
                MoveOut moveOut = softMoveOut.get();
                moveOutList.add(moveOut);
            }
        }
    }

    public void addReturnAfterCompletedOrder(List<Order> newReturnAfterCompletedOrder) {
        orders.addAll(newReturnAfterCompletedOrder);
        returnAfterCompletedOrders.addAll(newReturnAfterCompletedOrder);

        ArrayList<ReturnMoveOut> returningMoveOuts = new ArrayList<ReturnMoveOut>();
        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        for(Order order : newReturnAfterCompletedOrder){
            ReturnOrder returnOrder = new ReturnOrder(order);
            if(order.isRequestApproved() && order.getStatus().equals((OrderService.STATUS_COMPLETE))) {
                returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            } else if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null) {
                returnOrder.setReturnType(ReturnOrder.FAILED_DELIVERY_TYPE);
            }
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                MoveOut moveOut = softMoveOut.get();
                ReturnMoveOut returnMoveOut = new ReturnMoveOut(moveOut);
                returningMoveOuts.add(returnMoveOut);
                returnMoveOut.setReturnOrder(returnOrder);
                moveOuts.add(moveOut);
            }
        }
        returnMoveOuts.addAll(returningMoveOuts);
        this.moveOutList.addAll(moveOuts);
    }

    public void removeShippingOrders(List<Order> removeOrders) {

        for(Order order : removeOrders){
            Order lookupOrder = Lookup.lookupOrder(shippingOrders, order.getId());
            if(lookupOrder != null) {
                shippingOrders.remove(lookupOrder);
                orders.remove(lookupOrder);
                
                List<SoftReference<MoveOut>> softMoveOuts = lookupOrder.getMoveOutList();
                ArrayList<MoveOut> removeMoveOuts = new ArrayList<MoveOut>();
                for(SoftReference<MoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                moveOutList.removeAll(removeMoveOuts);
            }
        }
    }

    public void addShippingOrders(List<Order> newShippingOrders){
        orders.addAll(newShippingOrders);
        shippingOrders.addAll(newShippingOrders);
        for(Order order : newShippingOrders){
            List<SoftReference<MoveOut>> moveOuts = order.getMoveOutList();
            for(SoftReference<MoveOut> softMoveOut : moveOuts){
                MoveOut moveOut = softMoveOut.get();
                moveOutList.add(moveOut);
            }
        }
    }

    public void submitTransaction() {
        try {
            saveToRepository(orders);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addOrders(List<Order> newOrders) {
        orders.addAll(newOrders);
    }

    public void addReturnMoveOutsAsSortReference(List<SoftReference<ReturnMoveOut>> returnMoveOutList) {
        ArrayList<ReturnMoveOut> toAdd = new ArrayList<ReturnMoveOut>();
        for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnMoveOutList) {
            ReturnMoveOut returnMoveOut = softReturnMoveOut.get();
            toAdd.add(returnMoveOut);
        }
        returnMoveOuts.addAll(toAdd);
    }

    public void removeReturnMoveOutsAsSortReference(List<SoftReference<ReturnMoveOut>> returnMoveOutList) {
        List<ReturnMoveOut> toRemove = new ArrayList<ReturnMoveOut>();
        for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnMoveOutList) {
            ReturnMoveOut returnMoveOut = softReturnMoveOut.get();
            toRemove.add(returnMoveOut);
        }
        returnMoveOuts.removeAll(toRemove);
    }
}