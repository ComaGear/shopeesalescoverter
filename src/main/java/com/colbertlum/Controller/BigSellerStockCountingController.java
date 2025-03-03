package com.colbertlum.Controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class BigSellerStockCountingController {
    
    private Stage stage;
    private 

    public Stage getStage() {
        return stage;
    }

    public BigSellerStockCountingController(Stage preStage) {
        stage = new Stage();
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setX(preStage.getX() + (preStage.getWidth() / 2) + (stage.getWidth() / 2));
        stage.setY(preStage.getY() + (preStage.getHeight() / 2) + (stage.getHeight() / 2));

        stage.setTitle("mass updating big seller stock count from biztory stock report");
        stage.setScene(initScene());
    }

    private Scene initScene() {
        
    }
}
