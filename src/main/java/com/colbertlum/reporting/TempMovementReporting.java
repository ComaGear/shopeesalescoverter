package com.colbertlum.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.SummaryOrder;
import com.colbertlum.entity.UOM;

public class TempMovementReporting {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    
    public static void reporting(File file, List<MoveOut> moveOuts){
    
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet biztorySheet = workbook.createSheet("biztory");
        writeBiztoryMoveOutSheet(biztorySheet, moveOuts);

        XSSFSheet orderDetailSheet = workbook.createSheet("order profit");
        writeOrderSummarySheet(orderDetailSheet, moveOuts);

        XSSFSheet movementDetailSheet = workbook.createSheet("movement detail");
        writeProductProfitSummarySheet(movementDetailSheet, moveOuts);

        try{
            if(!file.exists()) file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);

            fileOutputStream.close();
            workbook.close();
        }catch(IOException exception){
            exception.printStackTrace();
            System.out.println(exception.toString());
        }
    }

    private static void writeBiztoryMoveOutSheet(XSSFSheet biztorySheet, List<MoveOut> moveOuts){

        // clean
        int lastRowNum = biztorySheet.getLastRowNum();
        for (int i = lastRowNum; i >= 0; i--) {
            Row row = biztorySheet.getRow(i);
            if (row != null) {
                biztorySheet.removeRow(row);
            }
        }

        int rowCount = 0;
        XSSFRow headerRow = biztorySheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("Code");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Qty");
        headerRow.createCell(3).setCellValue("unit");
        headerRow.createCell(4).setCellValue("Unit Price");

        for(MoveOut moveOut : moveOuts){

            if(moveOut.getQuantity() == 0) continue; 

            String productName = (moveOut.getProductName() == null ? "" : moveOut.getProductName())
                + " - " + 
                (moveOut.getVariationName() == null ? "" : moveOut.getVariationName());

            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            productName = productName.replaceAll(characterFilter,"");

            XSSFRow row = biztorySheet.createRow(rowCount++);
            row.createCell(0).setCellValue(moveOut.getProductId());
            row.createCell(1).setCellValue(productName);
            row.createCell(2).setCellValue(moveOut.getQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(moveOut.getProductSubTotal() / moveOut.getQuantity());
        }
    }

        private static void writeProductProfitSummarySheet(XSSFSheet movementDetailSheet, List<MoveOut> moveOuts) {


        // clean
        int lastRowNum = movementDetailSheet.getLastRowNum();
        for (int i = lastRowNum; i >= 0; i--) {
            Row row = movementDetailSheet.getRow(i);
            if (row != null) {
                movementDetailSheet.removeRow(row);
            }
        }

        List<UOM> uoms = ShopeeSalesConvertApplication.getIrsUoms();

        XSSFRow orderDetailHeaderRow = movementDetailSheet.createRow(0);
        orderDetailHeaderRow.createCell(0).setCellValue("Order ID");
        orderDetailHeaderRow.createCell(1).setCellValue("Order Shipout Date");
        orderDetailHeaderRow.createCell(2).setCellValue("Product ID");
        orderDetailHeaderRow.createCell(3).setCellValue("Product Name");
        orderDetailHeaderRow.createCell(4).setCellValue("Qauntity");
        orderDetailHeaderRow.createCell(5).setCellValue("Cost");
        orderDetailHeaderRow.createCell(6).setCellValue("SubTotal");
        orderDetailHeaderRow.createCell(7).setCellValue("SubCost");
        orderDetailHeaderRow.createCell(8).setCellValue("Profit");
        orderDetailHeaderRow.createCell(9).setCellValue("Profit Rate");
        orderDetailHeaderRow.createCell(10).setCellValue("transaction Fee");
        orderDetailHeaderRow.createCell(11).setCellValue("Service Fee");
        orderDetailHeaderRow.createCell(12).setCellValue("Commission Fee");
        orderDetailHeaderRow.createCell(13).setCellValue("Management Fee");
        orderDetailHeaderRow.createCell(14).setCellValue("Grand Total");
        orderDetailHeaderRow.createCell(15).setCellValue("Order Shipping Fee");


        moveOuts.sort(new Comparator<MoveOut>() {

            @Override
            public int compare(MoveOut o1, MoveOut o2) {
                return o1.getOrder().getId().compareTo(o2.getOrder().getId());
            }
            
        });

        uoms.removeIf(uom -> (uom.getRate() != 1));
        int index = 1;
        for(MoveOut moveOut : moveOuts){

            UOM uom = null;
            if(moveOut.getProductId() != null) {
                uom = UOM.binarySearch(moveOut.getProductId(), uoms);
            } else {
                uom = new UOM();
                uom.setProductId("");
                uom.setCostPrice(moveOut.getProductSubTotal() / moveOut.getQuantity());
            }

            XSSFRow row = movementDetailSheet.createRow(index);
            row.createCell(0).setCellValue(moveOut.getOrder().getId());
            row.createCell(1).setCellValue(DateTimeFormatter.ofPattern(DATE_PATTERN).format(moveOut.getOrder().getShipOutDate()));
            row.createCell(2).setCellValue(moveOut.getProductId());
            row.createCell(3).setCellValue(moveOut.getProductName() + "-" + moveOut.getVariationName());
            row.createCell(4).setCellValue(moveOut.getQuantity());
            row.createCell(5).setCellValue(uom.getCostPrice());
            row.createCell(6).setCellValue(moveOut.getProductSubTotal());
            row.createCell(7).setCellValue(uom.getCostPrice() * moveOut.getQuantity());
            row.createCell(8).setCellValue(moveOut.getProductSubTotal() - (uom.getCostPrice() * moveOut.getQuantity()));
            row.createCell(9).setCellValue(1 - ((uom.getCostPrice() * moveOut.getQuantity()) / moveOut.getProductSubTotal()));
            row.createCell(10).setCellValue(moveOut.getOrder().getTransactionFee());
            row.createCell(11).setCellValue(moveOut.getOrder().getCommissionFee());
            row.createCell(12).setCellValue(moveOut.getOrder().getServiceFee());
            row.createCell(13).setCellValue(moveOut.getOrder().getManagementFee());
            row.createCell(14).setCellValue(moveOut.getOrder().getOrderTotalAmount());
            row.createCell(15).setCellValue(moveOut.getOrder().getShippingFee());

            index++;
        }
    }

    private static void writeOrderSummarySheet(XSSFSheet orderDetailSheet, List<MoveOut> moveOuts) {

        // clean
        int lastRowNum = orderDetailSheet.getLastRowNum();
        for (int i = lastRowNum; i >= 0; i--) {
            Row row = orderDetailSheet.getRow(i);
            if (row != null) {
                orderDetailSheet.removeRow(row);
            }
        }

        List<UOM> uoms = ShopeeSalesConvertApplication.getIrsUoms();

        XSSFRow orderDetailHeaderRow = orderDetailSheet.createRow(0);
        orderDetailHeaderRow.createCell(0).setCellValue("Order ID");
        orderDetailHeaderRow.createCell(1).setCellValue("Order Shipout Date");
        orderDetailHeaderRow.createCell(2).setCellValue("Order Status");
        orderDetailHeaderRow.createCell(3).setCellValue("Total Amount");
        orderDetailHeaderRow.createCell(4).setCellValue("Profit");
        orderDetailHeaderRow.createCell(5).setCellValue("Profit Rate");

        moveOuts.sort(new Comparator<MoveOut>() {

            @Override
            public int compare(MoveOut o1, MoveOut o2) {
                return o1.getOrder().getId().compareTo(o2.getOrder().getId());
            }
            
        });


        uoms.removeIf(uom -> (uom.getRate() != 1));
        ArrayList<SummaryOrder> summaryOrders = new ArrayList<SummaryOrder>();
        uoms.sort(new Comparator<UOM>() {

            @Override
            public int compare(UOM o1, UOM o2) {
                return o1.getProductId().toLowerCase().compareTo(o2.getProductId().toLowerCase());
            }
            
        });

        for(MoveOut moveOut : moveOuts){
            SummaryOrder lastOrder = null;
            if(!summaryOrders.isEmpty()) {
                lastOrder = summaryOrders.get(summaryOrders.size()-1);
            } else {
                lastOrder = new SummaryOrder();
            }
            
            UOM uom = null;
            if(moveOut.getProductId() != null) {
                uom = UOM.binarySearch(moveOut.getProductId(), uoms);
            } else {
                uom = new UOM();
                uom.setProductId("");
                uom.setCostPrice(moveOut.getProductSubTotal() / moveOut.getQuantity());
            }
            double moveOutProfit = moveOut.getProductSubTotal() - (uom.getCostPrice() * moveOut.getQuantity());
            if(lastOrder.getId() != null && lastOrder.getId().equals(moveOut.getOrder().getId())) {
               lastOrder.setProfit(lastOrder.getProfit() + moveOutProfit);
               lastOrder.setTotalAmount(lastOrder.getTotalAmount() + moveOut.getProductSubTotal());
            } else {
                lastOrder = new SummaryOrder();
                lastOrder.setId(moveOut.getOrder().getId());
                lastOrder.setProfit(moveOutProfit);
                lastOrder.setShipOutDate(moveOut.getOrder().getShipOutDate());
                lastOrder.setTotalAmount(moveOut.getProductSubTotal());
                lastOrder.setStatus(moveOut.getOrder().getStatus());
                summaryOrders.add(lastOrder);
            }
            
        }

        int index = 1;
        for(SummaryOrder order : summaryOrders){
            XSSFRow row = orderDetailSheet.createRow(index);
            row.createCell(0).setCellValue(order.getId());
            row.createCell(1).setCellValue(DateTimeFormatter.ofPattern(DATE_PATTERN).format(order.getShipOutDate()));
            row.createCell(2).setCellValue(order.getStatus());
            row.createCell(3).setCellValue(order.getTotalAmount());
            row.createCell(4).setCellValue(order.getProfit());
            row.createCell(5).setCellValue(order.getProfit() / order.getTotalAmount());
            index++;
        }
    }
}
