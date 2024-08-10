package com.colbertlum;

import java.io.File;
import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.reporting.CompletedMovementReporting;
import com.colbertlum.reporting.TempMovementReporting;

public class OrderService {

    public static final String STATUS_CANCEL = "Cancelled";
    public static final String STATUS_COMPLETE = "Completed";
    public static final String STATUS_TO_SHIP = "To ship";
    public static final String STATUS_UNPAID = "Unpaid";
    public static final String STATUS_SHIPPING = "Shipping";


    private List<Order> beingCompleteOrderList;
    private List<Order> beingShippingOrderList;
    private List<Order> beingReturningAfterShippingOrderList;
    private List<Order> beingReturningAfterCompleteOrderList;
    private List<Order> beingPendingOrderList;
    private OrderRepository orderRepository;

    private List<Order> allOrders;

    public void process(List<MoveOut> moveOuts){
        orderRepository = new OrderRepository();

        List<Order> orders = new ArrayList<Order>();
        for(MoveOut moveOut : moveOuts){
            if(!orders.contains(moveOut.getOrder())) orders.add(moveOut.getOrder());
        }

        for(Order order : orders){
            determineStatus(order);
        }

        // reporting being Shipping Move Out.
        ArrayList<MoveOut> ShippingMoveOuts = new ArrayList<MoveOut>();
        // ArrayList<Order> figureOutOrderInRepositoryOnlyOnShipping = figureOutOrderInRepositoryOnlyOnShipping(orderRepository);
        List<Order> shippingOrders = orderRepository.getShippingOrders();
        List<Order> newShippingOrders = figureOutNewShippingOrder(orderRepository);
        for(Order order : newShippingOrders){
            for(SoftReference<MoveOut> moveOut : order.getMoveOutList()){
                ShippingMoveOuts.add(moveOut.get());
            }
        }
        for(Order order : shippingOrders){
            for(SoftReference<MoveOut> moveOut : order.getMoveOutList()){
                ShippingMoveOuts.add(moveOut.get());
            }
        }
        File tempMovementFile = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.TEMP_MOVEMENT_PATH));
        TempMovementReporting.reporting(tempMovementFile, new ArrayList<MoveOut>(ShippingMoveOuts));
        // save on shipping order to repository
        orderRepository.addShippingOrders(newShippingOrders);

        // record completed order
        ArrayList<Order> newCompletedOrder = figureOutNewCompletedOrder(orderRepository);
        // save on completed order to repository
        orderRepository.addCompletedOrders(newCompletedOrder);

        // figureOut toReport orders
        ArrayList<Order> toReportOrders = new ArrayList<Order>();
        toReportOrders.addAll(newCompletedOrder);
        toReportOrders.addAll(lookupOrderNotYetOnShipping(figureOutNewInReturnOrder(orderRepository), orderRepository));
        toReportOrders.addAll(lookupOrderNotYetOnShipping(figureOutNewInReturnAfterCompletedOrder(orderRepository), orderRepository));

        //figure out new return order after ship out.
        ArrayList<Order> newReturnAfterShippingOrders = figureOutNewInReturnOrder(orderRepository);
        orderRepository.removeShippingOrders(newReturnAfterShippingOrders);
        orderRepository.addReturnAfterShippingOrder(newReturnAfterShippingOrders);

        //figure out new return order after buyer received order and request return or refund.
        ArrayList<Order> newReturnAfterCompletedOrder = figureOutNewInReturnAfterCompletedOrder(orderRepository);
        // save to repository
        orderRepository.removeCompletedOrders(newReturnAfterCompletedOrder);
        orderRepository.removeShippingOrders(newReturnAfterCompletedOrder);
        orderRepository.addReturnAfterCompletedOrder(newReturnAfterCompletedOrder);
        

        // reporting completed order by date.
        toReportOrders.sort(new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getOrderCompleteDate().compareTo(o2.getOrderCompleteDate());
            }
            
        });
        HashMap<String, List<MoveOut>> dateDifferentMoveOuts = new HashMap<String, List<MoveOut>>();
        for(Order order : toReportOrders){
            LocalDate orderCompleteDate = order.getOrderCompleteDate();
            String fileName = String.format("SalesCompleted$x.$x.$x", orderCompleteDate.getYear(), orderCompleteDate.getMonthValue(), orderCompleteDate.getDayOfMonth());
            if(!dateDifferentMoveOuts.containsKey(fileName)){
                dateDifferentMoveOuts.put(fileName, new ArrayList<MoveOut>());
            }
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                MoveOut moveOut = softMoveOut.get();
                dateDifferentMoveOuts.get(fileName).add(moveOut);
            }
        }
        for(String fileName : dateDifferentMoveOuts.keySet()){
            String filePath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.COMPLETE_ORDER_PATH) + fileName;
            CompletedMovementReporting.reporting(new File(filePath), dateDifferentMoveOuts.get(fileName));
        }

        orderRepository.submitTransaction();
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


    private List<Order> figureOutNewShippingOrder(OrderRepository orderRepository) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'figureOutNewShippingOrder'");
    }


    private ArrayList<Order> figureOutNewInReturnAfterCompletedOrder(OrderRepository orderRepository) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'figureOutNewInReturnAfterCompletedOrder'");
    }


    private ArrayList<Order> figureOutNewInReturnOrder(OrderRepository orderRepository) {
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


    private ArrayList<Order> figureOutOrderInRepositoryOnlyOnShipping(OrderRepository LorderRepository) {
        ArrayList<Order> tempList = new ArrayList<Order>(LorderRepository.getShippingOrders());

        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };
        tempList.sort(comparator);
        allOrders.sort(comparator);
        
        int tempIndex = 0;
        ArrayList<Order> toRemove = new ArrayList<Order>();
        for(int i = 0; i < tempList.size(); i++){
            Order lookupOrder = Lookup.lookupOrder(allOrders, tempList.get(i).getId());
            if(lookupOrder == null){
                toRemove.add(tempList.get(tempIndex));
            }
        }
        tempList.removeAll(toRemove);
        
        return tempList;
    }


    private void determineStatus(Order order){
        if(order.getStatus().equals(STATUS_COMPLETE) && order.isRequestApproved()){
            beingCompleteOrderList.add(order);
            beingReturningAfterCompleteOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(STATUS_COMPLETE)){
            beingCompleteOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(STATUS_SHIPPING)){
            beingShippingOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(STATUS_CANCEL) && order.getShipOutDate() != null){
            // beingCompleteOrderList.add(order);
            beingReturningAfterShippingOrderList.add(order);
            return;
        }
        if(order.getStatus().equals(STATUS_TO_SHIP)){
            beingPendingOrderList.add(order);
        }
    }

    private HashMap<String, Double> beingShipping(List<MoveOut> moveOuts){

        HashMap<String, Double> pendingStockReducingMap = new HashMap<String, Double>();

        for(MoveOut moveOut : moveOuts) {
            if(!pendingStockReducingMap.containsKey(moveOut.getOrderId())) {
                pendingStockReducingMap.put(moveOut.getOrderId(), moveOut.getQuantity());
            } else {
                Double lastReduce = pendingStockReducingMap.get(moveOut.getOrderId());
                pendingStockReducingMap.put(moveOut.getOrderId(), lastReduce);
            }
        }

        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.TEMP_MOVEMENT_PATH));
        TempMovementReporting.reporting(file, moveOuts);

        return pendingStockReducingMap;
    }

    private List<MoveOut> beingCompleted(List<MoveOut> moveOuts){


        List<Order> completedOrderList = orderRepository.getCompletedOrders();
        
        Order foundOrder = null;
        for(MoveOut moveOut : moveOuts){
            if(foundOrder != null && !moveOut.getOrder().getId().equals(foundOrder.getId())){
                foundOrder = Lookup.lookupOrder(completedOrderList, moveOut.getOrder().getId());
            }
            
            if(foundOrder != null){
                moveOuts.remove(moveOut);
            }
        }

        String path = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.COMPLETE_ORDER_PATH);
        CompletedMovementReporting.reporting(new File(path), moveOuts);


    }
    
    private void beingReturningAfterShipping(List<MoveOut> moveOuts){

    }

    private void beingReturningAfterCompleted(List<Order> orders){

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

    public OrderService() {
        this.beingCompleteOrderList = new ArrayList<Order>();
        this.beingShippingOrderList = new ArrayList<Order>();
        this.beingReturningAfterShippingOrderList = new ArrayList<Order>();
        this.beingReturningAfterCompleteOrderList = new ArrayList<Order>();
        this.beingPendingOrderList = new ArrayList<Order>();
    }


}
