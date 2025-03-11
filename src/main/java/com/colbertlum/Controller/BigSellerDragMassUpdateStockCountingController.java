package com.colbertlum.Controller;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BigSellerDragMassUpdateStockCountingController {

    Map<String, String> results;


    public void openBigSellerMassUpdateStockCountingDragStage(Stage stage, Stage prStage) {

        stage.setWidth(600);
        stage.setHeight(400);
        stage.setX((prStage.getX() + (prStage.getWidth() / 2)) - (stage.getWidth()));
        stage.setY((prStage.getY() + (prStage.getHeight() / 2)) - (stage.getHeight()));

        

        Scene scene = new Scene(new VBox(new HBox(exportFilePane, importFilePane), confirmButton));
        stage.setScene(scene);

        stage.showAndWait();
    }

    public Map<String, String> getResult(){
        return results;
    }

    public BigSellerDragMassUpdateStockCountingController() {
        this.results = HashMap<String, String>();
    }
}
