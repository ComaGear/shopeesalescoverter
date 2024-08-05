package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
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

                        RepositoryReturnMovementContentHandler contentHandler = new RepositoryReturnMovementContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), returnMoveOuts);
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

    public void saveToRepository(List<Order> orders) throws IOException{

        // ask user comfirm process will update to repository or skip following part.
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure this process is valid and save which order to repository");
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() && result.get() != ButtonType.OK){
            return;
        }

        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet orderSheet = workbook.getSheetAt(0);
        Sheet movementSheet = workbook.getSheetAt(1);
        Sheet returnMovementSheet = workbook.getSheetAt(2);

        writeOrderSheet(orderSheet, orders);
        writeMovementSheet(movementSheet, moveOutList);
        writeReturnMovementSheet(returnMovementSheet, returnMoveOuts);

        fileInputStream.close();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();
    }

    private void writeReturnMovementSheet(Sheet returnMovementSheet, List<ReturnMoveOut> returnMoveOuts) {
        Row headerRow = returnMovementSheet.getRow(0);
        if(headerRow == null) returnMovementSheet.createRow(0);

        Cell orderIdCellHeader = headerRow.getCell(0);
        if(orderIdCellHeader == null) orderIdCellHeader = headerRow.createCell(0);
        orderIdCellHeader.setCellValue("Order Id");

        Cell SkuCellHeader = headerRow.getCell(1);
        if(SkuCellHeader == null) SkuCellHeader = headerRow.createCell(1);
        SkuCellHeader.setCellValue("SKU");

        Cell productNameCellHeader = headerRow.getCell(2);
        if(productNameCellHeader == null) productNameCellHeader = headerRow.createCell(2);
        productNameCellHeader.setCellValue("Product Name");

        Cell variationNameCellHeader = headerRow.getCell(3);
        if(variationNameCellHeader == null) variationNameCellHeader = headerRow.createCell(3);
        variationNameCellHeader.setCellValue("Variation Name");

        Cell quantityCellHeader = headerRow.getCell(4);
        if(quantityCellHeader == null) quantityCellHeader = headerRow.createCell(4);
        quantityCellHeader.setCellValue("Quantity");

        Cell priceCellHeader = headerRow.getCell(5);
        if(priceCellHeader == null) priceCellHeader = headerRow.createCell(5);
        priceCellHeader.setCellValue("Price");

        Cell returnStatusCellHeader = headerRow.getCell(6);
        if(returnStatusCellHeader == null) returnStatusCellHeader = headerRow.createCell(6);
        returnStatusCellHeader.setCellValue("Return Status");

        Cell statusQuantityCellHeader = headerRow.getCell(7);
        if(statusQuantityCellHeader == null) statusQuantityCellHeader = headerRow.createCell(7);
        statusQuantityCellHeader.setCellValue("Status Quantity");

        int index = 1;
        for(ReturnMoveOut returnMoveOut : returnMoveOuts){
            Row row = returnMovementSheet.getRow(index);
            if(row == null) row = returnMovementSheet.createRow(index);
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
    }

    private void writeMovementSheet(Sheet movementSheet, List<MoveOut> moveOuts) {
        Row headerRow = movementSheet.getRow(0);
        if(headerRow == null) movementSheet.createRow(0);

        Cell orderIdCellHeader = headerRow.getCell(0);
        if(orderIdCellHeader == null) orderIdCellHeader = headerRow.createCell(0);
        orderIdCellHeader.setCellValue("Order Id");

        Cell SkuCellHeader = headerRow.getCell(1);
        if(SkuCellHeader == null) SkuCellHeader = headerRow.createCell(1);
        SkuCellHeader.setCellValue("SKU");

        Cell productNameCellHeader = headerRow.getCell(2);
        if(productNameCellHeader == null) productNameCellHeader = headerRow.createCell(2);
        productNameCellHeader.setCellValue("Product Name");

        Cell variationNameCellHeader = headerRow.getCell(3);
        if(variationNameCellHeader == null) variationNameCellHeader = headerRow.createCell(3);
        variationNameCellHeader.setCellValue("Variation Name");

        Cell quantityCellHeader = headerRow.getCell(4);
        if(quantityCellHeader == null) quantityCellHeader = headerRow.createCell(4);
        quantityCellHeader.setCellValue("Quantity");

        Cell priceCellHeader = headerRow.getCell(5);
        if(priceCellHeader == null) priceCellHeader = headerRow.createCell(5);
        priceCellHeader.setCellValue("Price");

        int index = 1;
        for(MoveOut moveOut : moveOuts){
            Row row = movementSheet.getRow(index);
            if(row == null) row = movementSheet.createRow(index);
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
    }

    private void writeOrderSheet(Sheet orderSheet, List<Order> orders) {
        Row headerRow = orderSheet.getRow(0);
        if(headerRow == null) orderSheet.createRow(0);

        Cell orderIdCellHeader = headerRow.getCell(0);
        if(orderIdCellHeader == null) orderIdCellHeader = headerRow.createCell(0);
        orderIdCellHeader.setCellValue("Order Id");

        Cell trackingNumberCellHeader = headerRow.getCell(1);
        if(trackingNumberCellHeader == null) trackingNumberCellHeader = headerRow.createCell(1);
        trackingNumberCellHeader.setCellValue("Tracking Number");

        Cell creationDateCellHeader = headerRow.getCell(2);
        if(creationDateCellHeader == null) creationDateCellHeader = headerRow.createCell(2);
        creationDateCellHeader.setCellValue("Creation Date");

        Cell shipOutDateCellHeader = headerRow.getCell(3);
        if(shipOutDateCellHeader == null) shipOutDateCellHeader = headerRow.createCell(3);
        shipOutDateCellHeader.setCellValue("ShipOut Date");

        Cell completedDateCellHeader = headerRow.getCell(4);
        if(completedDateCellHeader == null) completedDateCellHeader = headerRow.createCell(4);
        completedDateCellHeader.setCellValue("Completed Date");

        Cell RequestReturnRefundCellHeader = headerRow.getCell(5);
        if(RequestReturnRefundCellHeader == null) RequestReturnRefundCellHeader = headerRow.createCell(5);
        RequestReturnRefundCellHeader.setCellValue("Request Return/Refund");

        Cell statusCellHeader = headerRow.getCell(6);
        if(statusCellHeader == null) statusCellHeader = headerRow.createCell(6);
        statusCellHeader.setCellValue("Status");

        Cell orderTotalAmountCellHeader = headerRow.getCell(7);
        if(orderTotalAmountCellHeader == null) orderTotalAmountCellHeader = headerRow.createCell(7);
        orderTotalAmountCellHeader.setCellValue("Order Total Amount");

        Cell managementFeeCellHeader = headerRow.getCell(8);
        if(managementFeeCellHeader == null) managementFeeCellHeader = headerRow.createCell(8);
        managementFeeCellHeader.setCellValue("Management Fee");

        Cell transactionFeeCellHeader = headerRow.getCell(9);
        if(transactionFeeCellHeader == null) transactionFeeCellHeader = headerRow.createCell(9);
        transactionFeeCellHeader.setCellValue("Transaction Fee");

        Cell serviceFeeCellHeader = headerRow.getCell(10);
        if(serviceFeeCellHeader == null) serviceFeeCellHeader = headerRow.createCell(10);
        serviceFeeCellHeader.setCellValue("Service Fee");

        Cell CommissionFeeCellHeader = headerRow.getCell(11);
        if(CommissionFeeCellHeader == null) CommissionFeeCellHeader = headerRow.createCell(11);
        CommissionFeeCellHeader.setCellValue("Commission Fee");

        Cell shopeeVoucherCellHeader = headerRow.getCell(12);
        if(shopeeVoucherCellHeader == null) shopeeVoucherCellHeader = headerRow.createCell(12);
        shopeeVoucherCellHeader.setCellValue("Shopee Voucher");

        Cell shippingFeeCellHeader = headerRow.getCell(13);
        if(shippingFeeCellHeader == null) shippingFeeCellHeader = headerRow.createCell(13);
        shippingFeeCellHeader.setCellValue("Shipping Fee");

        Cell shippingRebateCellHeader = headerRow.getCell(14);
        if(shippingRebateCellHeader == null) shippingRebateCellHeader = headerRow.createCell(14);
        shippingRebateCellHeader.setCellValue("Shipping Rebate");

        int index = 1;
        for(Order order : orders){
            Row row = orderSheet.getRow(index);
            if(row == null) orderSheet.createRow(index);
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

    public void addOrders(Object figureOutNewReportOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addOrders'");
    }
}
