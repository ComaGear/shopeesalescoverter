package com.colbertlum.Meas;

import com.colbertlum.entity.UOM;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class UOMCellFactory implements Callback<ListView<UOM>, ListCell<UOM>> {

    @Override
    public ListCell<UOM> call(ListView<UOM> param) {
        return new ListCell<UOM>(){
            @Override
            public void updateItem(UOM uom, boolean empty){
                if(empty || uom == null) {
                    setText(null);
                    setGraphic(null);
                } else if(uom != null){
                    Text descriptionText = new Text(uom.getDescription());
                    Text idText = new Text(uom.getProductId());
                    idText.setVisible(false);
                    setGraphic(new HBox(descriptionText, idText));
                }
            }
        };
    }
    
}
