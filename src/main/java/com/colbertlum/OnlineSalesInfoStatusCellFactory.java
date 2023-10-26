package com.colbertlum;

import java.text.DecimalFormat;
import java.util.List;

import com.colbertlum.entity.OnlineSalesInfoStatus;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class OnlineSalesInfoStatusCellFactory implements Callback<ListView<OnlineSalesInfoStatus>, ListCell<OnlineSalesInfoStatus>>{



    private List<OnlineSalesInfoStatus> selectOnlineSalesList;

    @Override
    public ListCell<OnlineSalesInfoStatus> call(ListView<OnlineSalesInfoStatus> param) {
        return new ListCell<>(){
            private DecimalFormat decimalFormat = new DecimalFormat("##.00");

            @Override
            public void updateItem(OnlineSalesInfoStatus infoStatus, boolean empty){
                super.updateItem(infoStatus, empty);
                if(infoStatus == null || empty){
                    setText("");
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setOnAction(a -> {
                        if(checkBox.isSelected()) {
                            selectOnlineSalesList.add(infoStatus);
                        } else {
                            selectOnlineSalesList.remove(infoStatus);
                        }
                    });

                    Text productNameText = new Text(infoStatus.getOnlineSalesInfo().getProductName());
                    Text variationName = new Text(infoStatus.getOnlineSalesInfo().getVariationName());

                    TextField stockTextField = new TextField();
                    stockTextField.setPromptText("stock");
                    stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue.matches("\\d*")){
                            stockTextField.setText(newValue.replaceAll("[^\\d]", ""));
                        }
                    });
                    
                    TextField priceTextField = new TextField(decimalFormat.format(infoStatus.getOnlineSalesInfo().getPrice()));
                    priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue.matches("\\d*"))
                            newValue = newValue.replaceAll("[^\\d.]", "");
                        priceTextField.setText(decimalFormat.format(Double.parseDouble(newValue)));
                    });
                    
                    Text skuText = new Text(infoStatus.getOnlineSalesInfo().getSku());
                    
                    setGraphic(new HBox(checkBox, productNameText, variationName, skuText, stockTextField, priceTextField));
                }
            }
        };
    }

    public OnlineSalesInfoStatusCellFactory(List<OnlineSalesInfoStatus> selectOnlineSalesList){
        this.selectOnlineSalesList = selectOnlineSalesList;
    }
    
}
