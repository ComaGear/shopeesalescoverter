package com.colbertlum.Imputer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.constants.OrderInternalStatus;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.OrderFactory;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

public class HandleReturnImputer {

    List<ReturnOrder> returnOrderList;
    List<ReturnMoveOut> returnMoveOutList;
    List<Meas> measList = ShopeeSalesConvertApplication.getMeasList();
    private OrderRepository orderRepository;
    private ArrayList<ReturnOrder> updatedReturnOrders;

    private Comparator<ReturnOrder> trackingNumberComparator =  new Comparator<ReturnOrder>() {

        @Override
        public int compare(ReturnOrder o1, ReturnOrder o2) {
            
            return OrderFactory.getTrackingNumber(o1).compareTo(OrderFactory.getTrackingNumber(o2));
        }
        
    };

    private Comparator<ReturnOrder> orderIdComparator =  new Comparator<ReturnOrder>() {

        @Override
        public int compare(ReturnOrder o1, ReturnOrder o2) {
            return o1.getId().compareTo(o2.getId());
        }
        
    };

    public List<ReturnOrder> getReturnOrderList() {
        return returnOrderList;
    }
    
    public ReturnOrder getOrder(String orderId) {
        if(orderId == null || orderId.isEmpty() || orderId.equals("")) return null;
        
        ReturnOrder order = null;

        returnOrderList.sort(orderIdComparator);
        order = Lookup.lookupReturnOrder(returnOrderList, orderId);
        if(order != null) 
            return order;

        returnOrderList.sort(trackingNumberComparator);
        order = Lookup.lookupReturnOrderByTrackingNumber(returnOrderList, orderId);
        if(order != null) return order;
        System.out.println("re");
        return null;
    }
    
    public ReturnMoveOut getReturnMoveOuInOrder(ReturnOrder order, String text) {
        for(SoftReference<ReturnMoveOut> softMoveOut :  order.getReturnMoveOutList()){

            String sku = softMoveOut.get().getSku();

            if(sku.equals(text)) return softMoveOut.get();

            measList.sort((o1, o2) -> o1.getRelativeId().compareTo(o2.getRelativeId()));
            Meas meas = Lookup.lookupMeasBySku(measList, sku);
            if(meas != null && meas.getId().equals(text)) return softMoveOut.get();
        }
        return null;
        }
    
    public void saveTransaction() {
        if(this.updatedReturnOrders == null) return;
        
        orderRepository.submitTransaction();
        
        try{
            outputCreditNoteToXlsx(this.updatedReturnOrders);
        } catch (IOException e) {
            if(!Window.getWindows().isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Couldn't output Credit Note due to.. ");
                alert.setContentText(e.getMessage());
                alert.getButtonTypes().add(ButtonType.CANCEL);
                alert.showAndWait();
            }
            System.out.println(e.getMessage());
        }
    }

    private void figureToPutLocalDate(ReturnMoveOut returnMoveOut, Map<LocalDate, List<ReturnMoveOut>> map) {
        LocalDate orderCompleteDate = returnMoveOut.getReturnOrder().getOrderCompleteDate();
        if(orderCompleteDate != null) {
            if(map.containsKey(orderCompleteDate)) {
                map.get(orderCompleteDate).add(returnMoveOut);
            } else {
                map.put(orderCompleteDate, new ArrayList<>());
                map.get(orderCompleteDate).add(returnMoveOut);
            }
        } else {
            LocalDate now = LocalDate.now();
            if(map.containsKey(now)) {
                map.get(now).add(returnMoveOut);
            } else {
                map.put(now, new ArrayList<>());
                map.get(now).add(returnMoveOut);
            }
        }
    }

    private void outputCreditNoteToXlsx(ArrayList<ReturnOrder> updatedReturnOrders) throws IOException{

        // reprocess ReturnOrders to ReturnMove list, easily parse to xlsx.
        // List<ReturnMoveOut> damagedItemMoveOuts = new ArrayList<ReturnMoveOut>();
        // List<ReturnMoveOut> returnedItemMoveOuts = new ArrayList<ReturnMoveOut>();
        Map<LocalDate, List<ReturnMoveOut>> damagedItemMoveOutLocalDateMap = new HashMap<LocalDate, List<ReturnMoveOut>>();
        Map<LocalDate, List<ReturnMoveOut>> returnedItemMoveOutsLocalDateMap = new HashMap<LocalDate, List<ReturnMoveOut>>();
        for(ReturnOrder returnOrder : updatedReturnOrders){
            for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnOrder.getReturnMoveOutList()){

                ReturnMoveOut returnMoveOut = softReturnMoveOut.get();
                returnMoveOut.setReturnOrder(returnOrder);
                switch (returnMoveOut.getReturnStatus()) {
                    case ReturnMoveOut.DAMAGED:
                        // damagedItemMoveOuts.add(returnMoveOut.clone());

                        figureToPutLocalDate(returnMoveOut.clone(), damagedItemMoveOutLocalDateMap);
                        break;
                    case ReturnMoveOut.LOST:
                        returnMoveOut.setReturnStatus(ReturnMoveOut.DAMAGED);
                        // damagedItemMoveOuts.add(returnMoveOut.clone());
                        figureToPutLocalDate(returnMoveOut.clone(), damagedItemMoveOutLocalDateMap);
                        break;
                    case ReturnMoveOut.NONE :
                        break;
                    case ReturnMoveOut.PARTICULAR_RECEIVED:
                        ReturnMoveOut returnedClone = returnMoveOut.clone();
                        returnedClone.setReturnStatus(ReturnMoveOut.RECEIVED);

                        // only issue credit note when it was return after completed (has generated invoice)
                        if(returnOrder.getInternalStatus().equals(OrderInternalStatus.AFTER_RETURN)) {
                            // returnedItemMoveOuts.add(returnedClone);
                            figureToPutLocalDate(returnedClone, returnedItemMoveOutsLocalDateMap);
                        }

                        ReturnMoveOut damagedClone = returnMoveOut.clone();
                        damagedClone.setReturnStatus(ReturnMoveOut.DAMAGED);
                        damagedClone.setStatusQuantity(damagedClone.getQuantity() - damagedClone.getStatusQuantity());
                        // damagedItemMoveOuts.add(damagedClone);
                        figureToPutLocalDate(damagedClone, damagedItemMoveOutLocalDateMap);
                        break;
                    case ReturnMoveOut.RECEIVED:
                        // only issue credit note when it was return after completed (has generated invoice)
                        if(returnOrder.getInternalStatus().equals(OrderInternalStatus.AFTER_RETURN)) {
                            // returnedItemMoveOuts.add(returnMoveOut);
                            figureToPutLocalDate(returnMoveOut, returnedItemMoveOutsLocalDateMap);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        MeasImputer measImputer = new MeasImputer();
        // get output file location form ShopeeSalesConvertApplication
        String location = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.CREDIT_NOTE_PATH);
        ArrayList<LocalDate> dateList = new ArrayList<LocalDate>();
        dateList.addAll(damagedItemMoveOutLocalDateMap.keySet());
        dateList.addAll(returnedItemMoveOutsLocalDateMap.keySet());
        for(LocalDate localDate : dateList) {
            String creditNoteName = String.format("CreditNote_%d.%d.%d", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());        
            String filePath = location + File.separator + creditNoteName + ".xlsx";
            File file = new File(filePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            List<ReturnMoveOut> damagedItemMoveOuts = null;
            List<ReturnMoveOut> returnedItemMoveOuts = null;
            if(returnedItemMoveOutsLocalDateMap.containsKey(localDate)){
                returnedItemMoveOuts = returnedItemMoveOutsLocalDateMap.get(localDate);
                for(ReturnMoveOut moveOut : returnedItemMoveOuts) {
                    moveOut.setProductId(measImputer.getMeas(moveOut.getSku(), measList).getId());
                }
            }
            if(damagedItemMoveOutLocalDateMap.containsKey(localDate)){
                damagedItemMoveOuts = damagedItemMoveOutLocalDateMap.get(localDate);
                for(ReturnMoveOut moveOut : damagedItemMoveOuts) {
                    moveOut.setProductId(measImputer.getMeas(moveOut.getSku(), measList).getId());
                }
            }

            // parse to xlsx.
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet damagedSheet = workbook.createSheet("Damaged Biztory");
            XSSFSheet returnedSheet = workbook.createSheet("Returned Biztory");
            XSSFSheet summarySheet = workbook.createSheet("Summary");
    
            if(damagedSheet != null && damagedItemMoveOuts != null){
                writeDamagedSheet(damagedSheet, damagedItemMoveOuts);
            }
            if(returnedSheet != null && returnedItemMoveOuts != null){
                writeReturnedSheet(returnedSheet, returnedItemMoveOuts);
            }

            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
        }
    }

    private void writeReturnedSheet(XSSFSheet biztorySheet, List<ReturnMoveOut> returnedMoveOuts){

        // clean
        int lastRowNum = biztorySheet.getLastRowNum();
        for (int i = lastRowNum; i >= 1; i--) {
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

        returnedMoveOuts.sort((o1, o2) -> {
            return o1.getSku().compareTo(o2.getSku());
        });

        for(ReturnMoveOut returnMoveOut : returnedMoveOuts) {
            if(returnMoveOut.getStatusQuantity() == 0) continue; 

            String productName = returnMoveOut.getName();

            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            productName = productName.replaceAll(characterFilter,"");

            XSSFRow row = biztorySheet.createRow(rowCount++);
            row.createCell(0).setCellValue(returnMoveOut.getProductId());
            row.createCell(1).setCellValue(productName);
            row.createCell(2).setCellValue(returnMoveOut.getStatusQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(returnMoveOut.getPrice());
        }   
    }
    
    private void writeDamagedSheet(XSSFSheet biztorySheet, List<ReturnMoveOut> damagedItemMoveOuts){

        // clean
        int lastRowNum = biztorySheet.getLastRowNum();
        for (int i = lastRowNum; i >= 1; i--) {
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

        damagedItemMoveOuts.sort((o1, o2) -> {
            return o1.getSku().compareTo(o2.getSku());
        });

        for(ReturnMoveOut returnMoveOut : damagedItemMoveOuts) {
            if(returnMoveOut.getStatusQuantity() == 0) continue; 

            String productName = returnMoveOut.getName();

            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            productName = productName.replaceAll(characterFilter,"");

            XSSFRow row = biztorySheet.createRow(rowCount++);
            row.createCell(0).setCellValue(returnMoveOut.getProductId());
            row.createCell(1).setCellValue(productName);
            row.createCell(2).setCellValue(returnMoveOut.getStatusQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(returnMoveOut.getPrice());
        }

        
        double totalLost = 0.0d;
        for(ReturnMoveOut returnMoveOut : damagedItemMoveOuts) {
            totalLost += returnMoveOut.getPrice() * returnMoveOut.getStatusQuantity();
        }
        rowCount += 5;
        XSSFRow summaryRow = biztorySheet.createRow(rowCount);
        summaryRow.createCell(0).setCellValue("Total Loss :");
        summaryRow.createCell(1).setCellValue(totalLost);
    }
    
    public void setUpdated(ReturnOrder returnOrder) {
        if(this.updatedReturnOrders == null) {
            this.updatedReturnOrders = new ArrayList<ReturnOrder>();
        }

        this.updatedReturnOrders.add(returnOrder);
    }

    public HandleReturnImputer(){
        orderRepository = new OrderRepository(true);
        this.returnOrderList = orderRepository.getReturnOrders();
        this.returnMoveOutList = orderRepository.getReturnMoveOuts();
    }

    public void updateOrder(ReturnOrder returnOrder, ReturnOrder cloneReturnOrder) {
        
        orderRepository.removeReturnMoveOutsAsSortReference(returnOrder.getReturnMoveOutList());
        orderRepository.addReturnMoveOutsAsSortReference(cloneReturnOrder.getReturnMoveOutList());
        returnOrder.update(cloneReturnOrder);
    }
}
