package com.colbertlum.Controller;

import java.util.List;

import com.colbertlum.Imputer.HandleReturnImputer;
import com.colbertlum.cellFactory.ReturnMoveOutCellFactory;
import com.colbertlum.entity.ReturnMoveOut;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HandleReturnController {
    
    private Stage stage;
    private HandleReturnImputer imputer;

    private Scene generatePanel(){

        HBox headerPanel = new HBox();

        ListView<ReturnMoveOut> listView = new ListView<ReturnMoveOut>();
        listView.setCellFactory(new ReturnMoveOutCellFactory());

        Scene scene = new Scene(new VBox());
        scene.getStylesheets().add(getClass().getResource("copiable-text.css").toExternalForm());
        return scene;
    }

    private ListView<ReturnMoveOut> refillListView(ListView<ReturnMoveOut> listView, List<ReturnMoveOut> returnMoveOuts){
        listView.getItems().clear();

        listView.getItems().addAll(returnMoveOuts);
        return listView;
    }

    public void initDialog(Stage stage){
        this.stage = stage;

        stage.setTitle("Handle returning order");
        stage.setWidth(1400);
        stage.setHeight(600);
        stage.centerOnScreen();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {

                imputer.saveTransaction();
            }
            
        });
        
    }

    public Stage getStage() {
        return stage;
    }

    public HandleReturnController(){
        this.imputer = new HandleReturnImputer();
    }
}
