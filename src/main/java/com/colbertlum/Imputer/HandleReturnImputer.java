package com.colbertlum.Imputer;

import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.colbertlum.OrderRepository;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

public class HandleReturnImputer {

    List<ReturnOrder> returnOrderList;
    List<ReturnMoveOut> returnMoveOutList;
    List<Meas> measList = ShopeeSalesConvertApplication.getMeasList();
    private OrderRepository orderRepository;
    private ArrayList<ReturnOrder> updatedReturnOrders;

    private Comparator<ReturnOrder> trackingNumberComparator =  new Comparator<ReturnOrder>() {

        @Override
        public int compare(ReturnOrder o1, ReturnOrder o2) {
            return o1.getTrackingNumber().compareTo(o2.getTrackingNumber());
        }
        
    };

    private Comparator<ReturnOrder> orderIdComparator =  new Comparator<ReturnOrder>() {

        @Override
        public int compare(ReturnOrder o1, ReturnOrder o2) {
            return o1.getTrackingNumber().compareTo(o2.getTrackingNumber());
        }
        
    };

    public List<ReturnOrder> getReturnOrderList() {
        return returnOrderList;
    }
    
    public ReturnOrder getOrder(String text) {
        if(text == null || text.isEmpty() || text.equals("")) return null;
        
        ReturnOrder order = null;

        returnOrderList.sort(orderIdComparator);
        order = Lookup.lookupReturnOrder(returnOrderList, text);
        if(order != null) return order;

        returnOrderList.sort(trackingNumberComparator);
        order = Lookup.lookupReturnOrderByTrackingNumber(returnOrderList, text);
        if(order != null) return order;
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
        orderRepository.submitTransaction();
    

        outputCreditNoteToXlsx(this.updatedReturnOrders);
    }

    private void convertQuantityByStatus(List<ReturnMoveOut> returnMoveOuts) {
        for(ReturnMoveOut returnMoveOut : returnMoveOuts) {
            (())
        }
    }
    
    private void outputCreditNoteToXlsx(ArrayList<ReturnOrder> updatedReturnOrders){
        // TODO export a bundle of Credit Note seperate Return and Damaged item.
        // get output file location form ShopeeSalesConvertApplication
        String location = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.CREDIT_NOTE_PATH);
        LocalDate now = LocalDate.now();
        String damagedItemFileName = String.format("DamagedItem_%d.%d.%d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String returnedItemFileName = String.format("ReturnItem_%d.%d.%d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        // reprocess ReturnOrders to ReturnMove list, easily parse to xlsx.
        List<ReturnMoveOut> damagedItemMoveOuts = new ArrayList<ReturnMoveOut>();
        List<ReturnMoveOut> returnedItemMoveOuts = new ArrayList<ReturnMoveOut>();
        for(ReturnOrder returnOrder : updatedReturnOrders){
            for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnOrder.getReturnMoveOutList()){

                ReturnMoveOut returnMoveOut = softReturnMoveOut.get();
                switch (returnMoveOut.getReturnStatus()) {
                    case ReturnMoveOut.DAMAGED:
                        damagedItemMoveOuts.add(returnMoveOut.clone());
                        break;
                    case ReturnMoveOut.LOST:
                        returnMoveOut.setReturnStatus(ReturnMoveOut.DAMAGED);
                        damagedItemMoveOuts.add(returnMoveOut.clone());
                        break;
                    case ReturnMoveOut.NONE :
                        break;
                    case ReturnMoveOut.PARTICULAR_RECEIVED:
                        ReturnMoveOut returnedClone = returnMoveOut.clone();
                        returnedClone.setReturnStatus(ReturnMoveOut.RECEIVED);
                        returnedItemMoveOuts.add(returnedClone);

                        ReturnMoveOut damagedClone = returnMoveOut.clone();
                        damagedClone.setReturnStatus(ReturnMoveOut.DAMAGED);
                        damagedClone.setStatusQuantity(damagedClone.getQuantity() - damagedClone.getStatusQuantity());
                        damagedItemMoveOuts.add(damagedClone);
                        break;
                    case ReturnMoveOut.RECEIVED:
                        returnedItemMoveOuts.add(returnMoveOut);
                        break;
                    default:
                        break;
                }
            }
        }

        MeasImputer measImputer = new MeasImputer();
        for(ReturnMoveOut moveOut : returnedItemMoveOuts) {
            moveOut.setId(measImputer.getMeas(moveOut.getSku(), measList).getId());
        }
        for(ReturnMoveOut moveOut : damagedItemMoveOuts) {
            moveOut.setId(measImputer.getMeas(moveOut.getSku(), measList).getId());
        }

        convertQuantityByStatus(returnedItemMoveOuts);
        convertQuantityByStatus(damagedItemMoveOuts);

        // parse to xlsx.
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet damagedSheet = workbook.createSheet("Damaged Biztory");
        XSSFSheet returnedSheet = workbook.createSheet("Returned Biztory");
        XSSFSheet summarySheet = workbook.createSheet("Summary");

        if(damagedSheet != null && !damagedItemMoveOuts.isEmpty()){
            writeDamagedSheet(summarySheet, damagedItemMoveOuts);
        }
    }

    private void writeReturnedSheet(Sheet sheet, List<ReturnMoveOut> returnMoveOuts){

    }
    
    private void writeDamagedSheet(XSSFSheet biztorySheet, List<ReturnMoveOut> damagedItemMoveOuts){

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

            String productName = returnMoveOut.getProductName() + " - " + returnMoveOut.getVariationName();

            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            productName = productName.replaceAll(characterFilter,"");

            XSSFRow row = biztorySheet.createRow(rowCount++);
            row.createCell(0).setCellValue(returnMoveOut.getId());
            row.createCell(1).setCellValue(productName);
            row.createCell(2).setCellValue(returnMoveOut.getStatusQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(moveOut.getProductSubTotal() / moveOut.getQuantity());
        }
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
}
