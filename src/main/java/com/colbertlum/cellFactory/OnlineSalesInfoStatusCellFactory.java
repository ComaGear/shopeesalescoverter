package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.util.List;

import com.colbertlum.entity.OnlineSalesInfoReason;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class OnlineSalesInfoStatusCellFactory implements Callback<ListView<OnlineSalesInfoReason>, ListCell<OnlineSalesInfoReason>>{



    private List<OnlineSalesInfoReason> selectOnlineSalesList;

    @Override
    public ListCell<OnlineSalesInfoReason> call(ListView<OnlineSalesInfoReason> param) {
        return new ListCell<>(){
            private DecimalFormat decimalFormat = new DecimalFormat("##.00");

            @Override
            public void updateItem(OnlineSalesInfoReason infoStatus, boolean empty){
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
                    stockTextField.setPromptText("stock");
                    stockTextField.setText(Integer.toString(infoStatus.getOnlineSalesInfo().getQuantity()));
                    stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue.matches("\\d*")){
                            stockTextField.setText(newValue.replaceAll("[^\\d]", ""));
                        }

                        if(newValue != null && !newValue.isEmpty() && Integer.parseInt(newValue) >= 0) {
                            infoStatus.getOnlineSalesInfo().setQuantity(Integer.parseInt(newValue));
                        }
                    });
                    stockTextField.setPrefWidth(50);
                    
                    TextField priceTextField = new TextField(decimalFormat.format(infoStatus.getOnlineSalesInfo().getPrice()));
                    priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue.matches("\\d*"))
                            newValue = newValue.replaceAll("[^\\d.]", "");
                        priceTextField.setText(newValue);

                        if(!newValue.isEmpty() && Double.parseDouble(newValue) != infoStatus.getOnlineSalesInfo().getPrice()){
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
                    
                    HBox hBox = new HBox(checkBox, productNameText, variationName, skuText, stockTextField, priceTextField, statusText);
                    hBox.setPadding(new Insets(2, 5, 2, 5));
                    setGraphic(hBox);
                }
            }
        };
    }

    public OnlineSalesInfoStatusCellFactory(List<OnlineSalesInfoReason> selectOnlineSalesList){
        this.selectOnlineSalesList = selectOnlineSalesList;
    }
    
}
