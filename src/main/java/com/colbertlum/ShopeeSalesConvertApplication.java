package com.colbertlum;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShopeeSalesConvertApplication extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Shopee Sales Converter");
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);


        VBox vBox = new VBox(null);


        Scene scene = new Scene(vBox, 600, 300);
        primaryStage.setScene(scene);


        
    }
}