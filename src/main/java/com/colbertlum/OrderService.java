package com.colbertlum;

import java.io.File;
import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ProductStock;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.reporting.CompletedMovementReporting;
import com.colbertlum.reporting.TempMovementReporting;

public class OrderService {

    public static final String STATUS_CANCEL = "Cancelled";
    public static final String STATUS_COMPLETE = "Completed";
    public static final String STATUS_TO_SHIP = "To ship";
    public static final String STATUS_UNPAID = "Unpaid";
    public static final String STATUS_SHIPPING = "Shipping";
    public static final String STATUS_DELIVERED = "Delivery";
    public static final String STATUS_RECEIVED = "Order Received";


    private List<Order> beingCompleteOrderList;
    private List<Order> beingShippingOrderList;
    private List<Order> beingReturningAfterShippingOrderList;
    private List<Order> beingReturningAfterCompleteOrderList;
    private List<Order> beingReceivedOrderList;
    private List<Order> beingDeliveredOrderList;
    private List<Order> beingPendingOrderList;
    private OrderRepository orderRepository;

    public static void inspectDataValidation(DataValidationInterface dataValidation){
        dataValidation.appendHandlingColumnExpectData("Order Status", STATUS_CANCEL);
        // appendHandlingColumnToMap("Order Status", "Cancelled");
        // appendHandlingColumnToMap("Order Status", "Completed");
        // appendHandlingColumnToMap("Order Status", "Unpaid");
        // appendHandlingColumnToMap("Order Status", "Shipping");

        // appendHandlingColumnContainsTextInDataToMap("Order Status", "Order Received, But");
        // TODO write all status to inspecting. upsert ShopeeOderReportContentHandler's override method 'appendHanding()' using this method
    }

    private void cleanPending(List<MoveOut> moveOuts) {
        moveOuts.removeIf(moveOut -> 
            STATUS_UNPAID.equals(moveOut.getOrder().getStatus()) 
                || STATUS_TO_SHIP.equals(moveOut.getOrder().getStatus())
        );
    }

    public void process(List<MoveOut> moveOuts){

        cleanPending(moveOuts);

        List<Order> orders = new ArrayList<Order>();
        for(MoveOut moveOut : moveOuts){
            if(!orders.contains(moveOut.getOrder())) orders.add(moveOut.getOrder());
        }

        for(Order order : orders){
            determineStatus(order);
        }

        // record completed order
        ArrayList<Order> newCompletedOrders = figureOutNewCompletedOrder(orderRepository);
        // save on completed order to repository
        orderRepository.addCompletedOrders(newCompletedOrders);
        orderRepository.removeShippingOrders(newCompletedOrders);

        // record received order
        List<Order> newReceivedOrders = figureOutNewReceivedOrder(orderRepository);
        orderRepository.addCompletedOrders(newReceivedOrders);
        orderRepository.removeShippingOrders(newReceivedOrders);

        // figureOut toReport orders from newCompleted and newInReturnAfterCompleted order with not record in repository.
        ArrayList<Order> toReportOrders = new ArrayList<Order>();
        if(!newCompletedOrders.isEmpty()) {
            toReportOrders.addAll(newCompletedOrders);
        }
        if(!newReceivedOrders.isEmpty()) {
            toReportOrders.addAll(newReceivedOrders);
        }
        if(!lookupOrderNotYetOnCompletedInRepository(figureOutNewInReturnAfterCompletedOrder(orderRepository), orderRepository).isEmpty()) {
            toReportOrders.addAll(lookupOrderNotYetOnCompletedInRepository(figureOutNewInReturnAfterCompletedOrder(orderRepository), orderRepository));
        }
        // toReportOrders.addAll(lookupOrderNotYetOnShipping(figureOutNewInReturnOrder(orderRepository), orderRepository));
        // toReportOrders.addAll(lookupOrderNotYetOnShipping(figureOutNewInReturnAfterCompletedOrder(orderRepository), orderRepository));

        //figure out new return order after ship out.
        ArrayList<Order> newReturnAfterShippingOrders = figureOutNewInReturnOrder(orderRepository);
        orderRepository.removeShippingOrders(newReturnAfterShippingOrders);
        orderRepository.addReturnAfterShippingOrder(newReturnAfterShippingOrders);

        //figure out new return order after buyer received order and request return or refund.
        List<Order> newReturnAfterCompletedOrder = figureOutNewInReturnAfterCompletedOrder(orderRepository);
        // save to repository
        orderRepository.removeCompletedOrders(newReturnAfterCompletedOrder);
        orderRepository.removeShippingOrders(newReturnAfterCompletedOrder);
        orderRepository.addReturnAfterCompletedOrder(newReturnAfterCompletedOrder);

        // record delivered order
        List<Order> newDeliveredOrders = figureOutNewDeliveredOrders(orderRepository);
        orderRepository.addShippingOrders(newDeliveredOrders);

        // reporting being Shipping and Delivered MoveOut.
        ArrayList<MoveOut> shippingMoveOuts = new ArrayList<MoveOut>();
        // ArrayList<Order> figureOutOrderInRepositoryOnlyOnShipping = figureOutOrderInRepositoryOnlyOnShipping(orderRepository);
        List<Order> shippingOrders = orderRepository.getShippingOrders();
        List<Order> newShippingOrders = figureOutNewShippingOrder(orderRepository);
        orderRepository.addShippingOrders(newShippingOrders);

        for(Order order : newShippingOrders){
            for(SoftReference<MoveOut> moveOut : order.getMoveOutList()){
                shippingMoveOuts.add(moveOut.get());
            }
        }
        for(Order order : shippingOrders){
            for(SoftReference<MoveOut> moveOut : order.getMoveOutList()){
                shippingMoveOuts.add(moveOut.get());
            }
        }
        for(Order order : newDeliveredOrders) {
            for(SoftReference<MoveOut> moveOut : order.getMoveOutList()) {
                shippingMoveOuts.add(moveOut.get());
            }
        }
        File tempMovementFile = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.TEMP_MOVEMENT_FILE_PATH));
        TempMovementReporting.reporting(tempMovementFile, new ArrayList<MoveOut>(shippingMoveOuts));
        // save on shipping and delivered order to repository

        

        // reporting completed order by date.
        if(!toReportOrders.isEmpty()){
            toReportOrders.sort(new Comparator<Order>() {

                @Override
                public int compare(Order o1, Order o2) {
                    return o1.getOrderCompleteDate().compareTo(o2.getOrderCompleteDate());
                }
                
            });
            HashMap<String, List<MoveOut>> dateDifferentMoveOuts = new HashMap<String, List<MoveOut>>();
            for(Order order : toReportOrders){
                LocalDate orderCompleteDate = order.getOrderCompleteDate();
                String fileName = String.format("SalesCompleted%d.%d.%d", orderCompleteDate.getYear(), orderCompleteDate.getMonthValue(), orderCompleteDate.getDayOfMonth());
                if(!dateDifferentMoveOuts.containsKey(fileName)){
                    dateDifferentMoveOuts.put(fileName, new ArrayList<MoveOut>());
                }
                for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                    MoveOut moveOut = softMoveOut.get();
                    dateDifferentMoveOuts.get(fileName).add(moveOut);
                }
            }
            for(String fileName : dateDifferentMoveOuts.keySet()){
                String filePath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.COMPLETE_ORDER_PATH) + File.separator + fileName + ".xlsx";
                CompletedMovementReporting.reporting(new File(filePath), dateDifferentMoveOuts.get(fileName));
            }
        }

        orderRepository.submitTransaction();
    }

    private List<Order> lookupOrderNotYetOnCompletedInRepository(List<Order> orders, OrderRepository orderRepository){
        ArrayList<Order> previousNotyetOnCompleted = new ArrayList<Order>();

        List<Order> completedOrders = new ArrayList<Order>(orderRepository.getShippingOrders());
        completedOrders.sort((o1, o2) ->{
            return o1.getId().compareTo(o2.getId());
        });
        for(Order order : orders){
            Order lookupOrder = Lookup.lookupOrder(completedOrders, order.getId());
            if(lookupOrder == null){
                previousNotyetOnCompleted.add(lookupOrder);
            } else if (STATUS_SHIPPING.equals(lookupOrder.getStatus())){
                previousNotyetOnCompleted.add(lookupOrder);
            }
        }

        return previousNotyetOnCompleted;
    }

    private List<Order> lookupOrderNotYetOnShipping(List<Order> orders, OrderRepository orderRepository){

        ArrayList<Order> previousNotYetOnShippingOrder = new ArrayList<Order>();

        List<Order> shippingOrders = orderRepository.getShippingOrders();
        shippingOrders.sort((o1, o2) ->{
            return o1.getId().compareTo(o2.getId());
        });
        for(Order order : orders){
            Order lookupOrder = Lookup.lookupOrder(shippingOrders, order.getId());
            if(lookupOrder == null){
                previousNotYetOnShippingOrder.add(lookupOrder);
            } else if (STATUS_SHIPPING.equals(lookupOrder.getStatus())){
                previousNotYetOnShippingOrder.add(lookupOrder);
            }
        }

        return previousNotYetOnShippingOrder;
    }

    private List<Order> figureOutNewReceivedOrder(OrderRepository orderRepository) {

        if(beingReceivedOrderList == null) return new ArrayList<>();

        List<Order> completedOrdersInRepository = new ArrayList<Order>(orderRepository.getCompletedOrders());

        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };
        completedOrdersInRepository.sort(comparator);
        beingReceivedOrderList.sort(comparator);
        
        ArrayList<Order> newReceivedOrders = new ArrayList<Order>();
        for(int i = 0; i < beingReceivedOrderList.size(); i++){
            Order lookupOrder = Lookup.lookupOrder(completedOrdersInRepository, beingReceivedOrderList.get(i).getId());
            if(lookupOrder == null){
                newReceivedOrders.add(beingReceivedOrderList.get(i));
            }
        }

        return newReceivedOrders;
    }

    private List<Order> figureOutNewDeliveredOrders(OrderRepository orderRepository) {

        if(beingDeliveredOrderList == null) return new ArrayList<>();

        List<Order> newDeliveredOrders = new ArrayList<Order>();

        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };
        List<Order> repositoryShippingOrders = new ArrayList<Order>(orderRepository.getShippingOrders());
        repositoryShippingOrders.sort(comparator);
        beingDeliveredOrderList.sort(comparator);
        for(Order order : beingDeliveredOrderList){
            Order lookupOrder = Lookup.lookupOrder(repositoryShippingOrders, order.getId());
            if(lookupOrder == null) {
                newDeliveredOrders.add(order);
            }
        }

        return newDeliveredOrders;
    }

    private List<Order> figureOutNewShippingOrder(OrderRepository orderRepository) {

        if(beingShippingOrderList == null) return new ArrayList<>();

        ArrayList<Order> newShippingOrders = new ArrayList<Order>();

        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };
        List<Order> repositoryShippingOrders = new ArrayList<Order>(orderRepository.getShippingOrders());
        repositoryShippingOrders.sort(comparator);
        beingShippingOrderList.sort(comparator);
        for(Order order : beingShippingOrderList){
            Order lookupOrder = Lookup.lookupOrder(repositoryShippingOrders, order.getId());
            if(lookupOrder == null) {
                newShippingOrders.add(order);
            }
        }

        return newShippingOrders;
    }


    private List<Order> figureOutNewInReturnAfterCompletedOrder(OrderRepository orderRepository) {

        if(beingReturningAfterCompleteOrderList == null) return new ArrayList<>();

        ArrayList<Order> newInReturnAfterCompletedOrder = new ArrayList<Order>();
       
        Comparator<Order> comparator = new Comparator<Order>(){

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }

        };
        ArrayList<Order> repositoryReturnAfterCompletedOrders = new ArrayList<Order>(orderRepository.getReturnAfterCompletedOrders());
        repositoryReturnAfterCompletedOrders.sort(comparator);
        beingReturningAfterCompleteOrderList.sort(comparator);
        for(Order order : beingReturningAfterCompleteOrderList){
            Order lookupOrder = Lookup.lookupOrder(repositoryReturnAfterCompletedOrders, order.getId());
            if(lookupOrder != null){
                newInReturnAfterCompletedOrder.add(order);
            }
        }

        return newInReturnAfterCompletedOrder;
    }


    private ArrayList<Order> figureOutNewInReturnOrder(OrderRepository orderRepository) {

        if(beingReturningAfterShippingOrderList == null) return new ArrayList<>();

        ArrayList<Order> returnOrders = new ArrayList<Order>(orderRepository.getReturnAfterShippingOrders());

        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };
        returnOrders.sort(comparator);
        beingReturningAfterShippingOrderList.sort(comparator);

        ArrayList<Order> newReturnOrders = new ArrayList<Order>();
        for(Order order : beingReturningAfterShippingOrderList){
            Order lookupOrder = Lookup.lookupOrder(returnOrders, order.getId());
            if(lookupOrder == null){
                newReturnOrders.add(order);
            }
        }
        return newReturnOrders;
    }


    private ArrayList<Order> figureOutNewCompletedOrder(OrderRepository orderRepository) {

        if(beingCompleteOrderList == null) return new ArrayList<>();

        ArrayList<Order> completedOrdersInRepository = new ArrayList<Order>(orderRepository.getCompletedOrders());

        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };
        completedOrdersInRepository.sort(comparator);
        beingCompleteOrderList.sort(comparator);
        
        ArrayList<Order> newCompletedOrders = new ArrayList<Order>();
        // int repositoryIndex = 0;
        for(int i = 0; i < beingCompleteOrderList.size(); i++){
            Order lookupOrder = Lookup.lookupOrder(completedOrdersInRepository, beingCompleteOrderList.get(i).getId());
            if(lookupOrder == null){
                newCompletedOrders.add(beingCompleteOrderList.get(i));
            }
        }

        return newCompletedOrders;
    }

    private void determineStatus(Order order){
        if(order.getStatus().equals(OrderService.STATUS_COMPLETE) && order.isRequestApproved()){
            beingReturningAfterCompleteOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_DELIVERED) && order.isRequestApproved()){
            beingReturningAfterCompleteOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_RECEIVED) && order.isRequestApproved()){
            beingReturningAfterCompleteOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_DELIVERED)) {
            beingDeliveredOrderList.add(order);
            return;
        }
        if(order.getStatus().contains(OrderService.STATUS_RECEIVED)) {
            beingReceivedOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_COMPLETE)){
            beingCompleteOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_SHIPPING)){
            beingShippingOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null){
            // beingCompleteOrderList.add(order);
            beingReturningAfterShippingOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(OrderService.STATUS_TO_SHIP)){
            beingPendingOrderList.add(order);
            return;
        }
    }

    public void reduceStockMap(List<ProductStock> stockList, Map<String, Double> toReduceMap) {
        for(String key : toReduceMap.keySet()) {
            ProductStock productStock = ProductStock.binarySearch(key, stockList);
            Double d = productStock.getAvailableStock() - toReduceMap.get(key);
            productStock.setStock(d);
        }
    }

    public Map<String, Double> calculatePendingOrderStockRequirement(List<MoveOut> moveOuts){

        HashMap<String, Double> pendingStockReducingMap = new HashMap<String, Double>();

        for(MoveOut moveOut : moveOuts) {
            if(!pendingStockReducingMap.containsKey(moveOut.getId())) {
                pendingStockReducingMap.put(moveOut.getId(), moveOut.getQuantity());
            } else {
                Double lastReduce = pendingStockReducingMap.get(moveOut.getId());
                pendingStockReducingMap.put(moveOut.getId(), lastReduce);
            }
        }
        
        return pendingStockReducingMap;
    }

    public Map<String, Double> getReservedDamagedStockQuantity() {
        List<ReturnMoveOut> returnMoveOuts = orderRepository.getReturnMoveOuts();
        Map<String, Double> map = new HashMap<String, Double>();
        
        for(ReturnMoveOut returnMoveOut : returnMoveOuts){
            map.put(returnMoveOut.getId(), returnMoveOut.getStatusQuantity());
        }
        return map;
    }

    public List<Order> getBeingDeliveredOrderList(){
        return beingDeliveredOrderList;
    }

    public List<Order> getBeingReceivedOrderList(){
        return beingReceivedOrderList;
    }

    public List<Order> getBeingCompleteOrderList() {
        return beingCompleteOrderList;
    }
    public List<Order> getBeingShippingOrderList() {
        return beingShippingOrderList;
    }

    public List<Order> getBeingReturningAfterShippingOrderList() {
        return beingReturningAfterShippingOrderList;
    }

    public List<Order> getBeingReturningAfterCompleteOrderList() {
        return beingReturningAfterCompleteOrderList;
    }

    public List<Order> getBeingPendingOrderList() {
        return beingPendingOrderList;
    }

    public OrderService(OrderRepository orderRepository) {
        this.beingCompleteOrderList = new ArrayList<Order>();
        this.beingShippingOrderList = new ArrayList<Order>();
        this.beingReturningAfterShippingOrderList = new ArrayList<Order>();
        this.beingReturningAfterCompleteOrderList = new ArrayList<Order>();
        this.beingPendingOrderList = new ArrayList<Order>();

        this.orderRepository = orderRepository;
    }


}
