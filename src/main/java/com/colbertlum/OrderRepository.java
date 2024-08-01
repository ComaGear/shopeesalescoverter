package com.colbertlum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.returnMoveOut;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class OrderRepository {

    List<Order> orders;
    List<Order> shippingOrders;
    List<Order> completedOrders;
    List<Order> returnAfterShippingOrders;
    List<Order> returnAfterCompletedOrders;

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
        
    }

    public void saveToRepository(List<Order> order){

        // ask user comfirm process will update to repository or skip following part.
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure this process is valid and save which order to repository");
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() && result.get() != ButtonType.OK){
            return;
        }
    }

    public OrderRepository(){
        loadRepository();
    }

    public void addInReturnMoveOut(ArrayList<returnMoveOut> returningMoveOuts) {
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
}
