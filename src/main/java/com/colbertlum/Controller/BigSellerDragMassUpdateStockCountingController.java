package com.colbertlum.Controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.colbertlum.ShopeeSalesConvertApplication;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BigSellerDragMassUpdateStockCountingController {
    public static final String EXPORT_FILE = "exportFile";
    public static final String IMPORT_FILE = "importFile";

    private Map<String, String> results;


    public void openBigSellerMassUpdateStockCountingDragStage(Stage stage, Stage prStage) {

        stage.setWidth(600);
        stage.setHeight(400);
        stage.setX((prStage.getX() + (prStage.getWidth() / 2)) - (stage.getWidth()));
        stage.setY((prStage.getY() + (prStage.getHeight() / 2)) - (stage.getHeight()));

        Font font = new Font(8);

        
        Text exportFileHeader = new Text("Please drag exported file from Big Seller's stock counting.");
        exportFileHeader.setFont(font);
        String exportFilePath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.BIG_SELLER_STOCK_COUNTING_EXPORT_FILE_PATH);
        Text exportFilePathText = new Text(exportFilePath);
        exportFilePathText.setFont(font);
        VBox exportFileVBox = new VBox(exportFileHeader, exportFilePathText);
        exportFileVBox.setAlignment(Pos.CENTER);

        Pane exportFilePane = new Pane(exportFileVBox);
        exportFilePane.setPrefWidth(300);
        exportFilePane.setPrefHeight(300);
        exportFilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 20;");
        exportFilePane.setOnDragOver((event) -> {
            if(event.getDragboard().hasFiles()) {
                exportFilePane.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-background-color: lightgray;");
                event.acceptTransferModes(TransferMode.MOVE);   
            }
            event.consume();
        });
        exportFilePane.setOnDragDropped((event) -> {
            exportFilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 20;");
            File file = event.getDragboard().getFiles().get(0);
            exportFilePathText.setText(file.getAbsolutePath());
            results.put(EXPORT_FILE, file.getAbsolutePath());
        });
        exportFilePane.setOnDragExited((event) -> {
            exportFilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 20;");
            event.consume();
        });


        Text importFileHeader = new Text("Please drag to import file from Big Seller's stock counting.");
        importFileHeader.setFont(font);
        String importFilePath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.BIG_SELLER_STOCK_COUNTING_IMPORT_FILE_PATH);
        Text importFilePathText = new Text(importFilePath);
        importFilePathText.setFont(font);
        VBox importFileVBox = new VBox(importFileHeader, importFilePathText);
        importFileVBox.setAlignment(Pos.CENTER);

        Pane importFilePane = new Pane(exportFileVBox);
        importFilePane.setPrefWidth(300);
        importFilePane.setPrefHeight(300);
        importFilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 20;");
        importFilePane.setOnDragOver((event) -> {
            if(event.getDragboard().hasFiles()) {
                importFilePane.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-background-color: lightgray;");
                event.acceptTransferModes(TransferMode.MOVE);   
            }
            event.consume();
        });
        importFilePane.setOnDragDropped((event) -> {
            importFilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 20;");
            File file = event.getDragboard().getFiles().get(0);
            importFilePathText.setText(file.getAbsolutePath());
            results.put(IMPORT_FILE, file.getAbsolutePath());
        });
        importFilePane.setOnDragExited((event) -> {
            importFilePane.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 20;");
            event.consume();
        });


        Button confirmButton = new Button("confirm");
        confirmButton.setOnAction((e) -> stage.close());

        Scene scene = new Scene(new VBox(new HBox(exportFilePane, importFilePane), confirmButton));
        stage.setScene(scene);

        stage.showAndWait();
    }

    public Map<String, String> getResult(){
        return results;
    }

    public BigSellerDragMassUpdateStockCountingController() {
        this.results = new HashMap<String, String>();
    }
}
