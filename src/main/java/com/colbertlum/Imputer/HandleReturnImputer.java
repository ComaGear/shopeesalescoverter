package com.colbertlum.Imputer;

import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.colbertlum.OrderRepository;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;
import com.colbertlum.entity.UOM;

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
    
    private void outputCreditNoteToXlsx(ArrayList<ReturnOrder> updatedReturnOrders){
        // TODO export a bundle of Credit Note seperate Return and Damaged item.
        // get output file location form ShopeeSalesConvertApplication
        String location = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.CREDIT_NOTE_PATH);
        LocalDate now = LocalDate.now();
        String damagedItemFileName = String.format("DamagedItem_..", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String returnedItemFileName = String.format("ReturnItem..", now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        // reprocess ReturnOrders to ReturnMove list, easily parse to xlsx.
        ArrayList<ReturnMoveOut> damagedItemMoveOuts = new ArrayList<ReturnMoveOut>();
        ArrayList<ReturnMoveOut> returnedItemMoveOuts = new ArrayList<ReturnMoveOut>();
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

        // parse to xlsx.
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet damagedSheet = workbook.createSheet("Damaged Biztory");
        XSSFSheet returnedSheet = workbook.createSheet("Returned Biztory");
        XSSFSheet summarySheet = workbook.createSheet("Summary");

        MeasImputer measImputer = new MeasImputer();

        if(damagedSheet != null && !damagedItemMoveOuts.isEmpty()){

            XSSFRow orderDetailHeaderRow = damagedSheet.createRow(0);
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

            damagedItemMoveOuts.sort((o1, o2) -> {
                return o1.getSku().compareTo(o2.getSku());
            });
            for(ReturnMoveOut moveOut : damagedItemMoveOuts){
                Meas meas = measImputer.getMeas(moveOut.getSku(), measImputer.getMeasList());
                moveOut.setOrderId(meas.getId());
                moveOut.setQuantity(meas.getMeasurement() * moveOut.getQuantity());
                moveOut.setPrice(moveOut.getPrice() / meas.getMeasurement());

                // TODO convert returnMoveOut's item sku to UOM base item.
                // check returnMoveOut had get ProductId while transform from MoveOut before.
                // if got it will igrone using OnlineMeas, directly using UOM as process output and handle return scene.
                /**
                UOM uom = null;
                if(moveOut.getOrderId() != null) {
                    uom = UOM.binarySearch(meas.getId(), ShopeeSalesConvertApplication.getIrsUoms());
                } else {
                    uom = new UOM();
                    uom.setProductId("");
                    uom.setCostPrice(moveOut.getProductSubTotal() / moveOut.getQuantity());
                }
                */
            }
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
