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
                    if(selectOnlineSalesList.contains(infoStatus)) checkBox.setSelected(true);
                    checkBox.setOnAction(a -> {
                        if(checkBox.isSelected()) {
                            selectOnlineSalesList.add(infoStatus);
                        } else {
                            selectOnlineSalesList.remove(infoStatus);
                        }
                    });
                    checkBox.setPrefWidth(20);

                    Text productNameText = new Text(infoStatus.getOnlineSalesInfo().getProductName());
                    productNameText.setWrappingWidth(500);

                    Text variationName = new Text(infoStatus.getOnlineSalesInfo().getVariationName());
                    variationName.setWrappingWidth(200);

                    TextField stockTextField = new TextField();
                    infoStatus.getOnlineSalesInfo().setQuantity(0);
                    stockTextField.setPromptText("stock");
                    stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue.matches("\\d*")){
                            stockTextField.setText(newValue.replaceAll("[^\\d]", ""));
                        }

                        if(newValue != null && !newValue.isEmpty() && Integer.parseInt(newValue) > 0) {
                            infoStatus.getOnlineSalesInfo().setQuantity(Integer.parseInt(newValue));
                        }
                    });
                    stockTextField.setPrefWidth(50);
                    stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {

                    });
                    
                    TextField priceTextField = new TextField(decimalFormat.format(infoStatus.getOnlineSalesInfo().getPrice()));
                    priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue.matches("\\d*"))
                            newValue = newValue.replaceAll("[^\\d.]", "");
                        priceTextField.setText(decimalFormat.format(Double.parseDouble(newValue)));

                        if(Double.parseDouble(newValue) != infoStatus.getOnlineSalesInfo().getPrice()){
                            infoStatus.getOnlineSalesInfo().setPrice(Double.parseDouble(newValue));
                        }
                    });
                    priceTextField.setPrefWidth(50);
                    
                    String sku = infoStatus.getOnlineSalesInfo().getSku();
                    if(sku == null || sku.isEmpty()) sku = infoStatus.getOnlineSalesInfo().getParentSku();
                    Text skuText = new Text(sku);
                    skuText.setWrappingWidth(80);

                    Text statusText = new Text(infoStatus.getStatus());
                    statusText.setWrappingWidth(150);
                    
                    setGraphic(new HBox(checkBox, productNameText, variationName, skuText, stockTextField, priceTextField, statusText));
                }
            }
        };
    }

    public OnlineSalesInfoStatusCellFactory(List<OnlineSalesInfoStatus> selectOnlineSalesList){
        this.selectOnlineSalesList = selectOnlineSalesList;
    }
    
}
