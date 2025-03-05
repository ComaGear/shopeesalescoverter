package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.util.List;

import com.colbertlum.entity.BigSellerStockCounting;
import com.colbertlum.entity.ListingStock;
import com.colbertlum.entity.ListingStockReason;
import com.colbertlum.entity.OnlineSalesInfo;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ListingStockReasonCellFactory implements Callback<ListView<ListingStockReason>, ListCell<ListingStockReason>>{



    private List<ListingStockReason> selectOnlineSalesList;

    @Override
    public ListCell<ListingStockReason> call(ListView<ListingStockReason> param) {
        return new ListCell<>(){
            private DecimalFormat decimalFormat = new DecimalFormat("##.00");
            private ListingStockReason listStockInfoReason;

            @Override
            public void updateItem(ListingStockReason listStockInfoReason, boolean empty){

                ListingStock listingStock = listStockInfoReason.getOnlineSalesInfo();
                
                super.updateItem(listStockInfoReason, empty);

                if(listStockInfoReason == null || empty){
                    setText("");
                    setGraphic(null);
                } else {


                    CheckBox checkBox = new CheckBox();
                    if(selectOnlineSalesList.contains(listStockInfoReason)) checkBox.setSelected(true);
                    checkBox.setOnAction(a -> {
                        if(checkBox.isSelected()) {
                            selectOnlineSalesList.add(listStockInfoReason);
                        } else {
                            selectOnlineSalesList.remove(listStockInfoReason);
                        }
                    });
                    checkBox.setPrefWidth(20);

                    
                    if(listingStock instanceof OnlineSalesInfo) {
                        OnlineSalesInfo infoStatus = (OnlineSalesInfo) listingStock;

                        Text productNameText = new Text(infoStatus.getProductName());
                        productNameText.setWrappingWidth(500);

                        Text variationName = new Text(infoStatus.getVariationName());
                        variationName.setWrappingWidth(200);

                        TextField stockTextField = new TextField();
                        stockTextField.setPromptText("stock");
                        stockTextField.setText(Integer.toString(infoStatus.getQuantity()));
                        stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                            if(!newValue.matches("\\d*")){
                                stockTextField.setText(newValue.replaceAll("[^\\d]", ""));
                            }

                            if(newValue != null && !newValue.isEmpty() && Integer.parseInt(newValue) >= 0) {
                                infoStatus.setQuantity(Integer.parseInt(newValue));
                            }
                        });
                        stockTextField.setPrefWidth(50);
                        
                        TextField priceTextField = new TextField(decimalFormat.format(infoStatus.getPrice()));
                        priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                            if(!newValue.matches("\\d*"))
                                newValue = newValue.replaceAll("[^\\d.]", "");
                            priceTextField.setText(newValue);

                            if(!newValue.isEmpty() && Double.parseDouble(newValue) != infoStatus.getPrice()){
                                infoStatus.setPrice(Double.parseDouble(newValue));
                            }
                        });
                        priceTextField.setPrefWidth(50);
                        
                        String sku = infoStatus.getSku();
                        Text skuText = new Text(sku);
                        skuText.setWrappingWidth(80);

                        Text statusText = new Text(listStockInfoReason.getStatus());
                        statusText.setWrappingWidth(150);
                        
                        HBox hBox = new HBox(checkBox, productNameText, variationName, skuText, stockTextField, priceTextField, statusText);
                        hBox.setPadding(new Insets(2, 5, 2, 5));
                        setGraphic(hBox);
                    }
                    if(listingStock instanceof BigSellerStockCounting) {
                        BigSellerStockCounting infoStatus = (BigSellerStockCounting) listingStock;
                        Text productNameText = new Text(infoStatus.getName());
                        productNameText.setWrappingWidth(700);

                        TextField stockTextField = new TextField();
                        stockTextField.setPromptText("stock");
                        stockTextField.setText(Integer.toString(infoStatus.getStock()));
                        stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                            if(!newValue.matches("\\d*")){
                                stockTextField.setText(newValue.replaceAll("[^\\d]", ""));
                            }

                            if(newValue != null && !newValue.isEmpty() && Integer.parseInt(newValue) >= 0) {
                                infoStatus.setStock(Integer.parseInt(newValue));
                            }
                        });
                        stockTextField.setPrefWidth(100);

                        String sku = infoStatus.getSku();
                        Text skuText = new Text(sku);
                        skuText.setWrappingWidth(80);

                        Text statusText = new Text(listStockInfoReason.getStatus());
                        statusText.setWrappingWidth(150);
                        
                        HBox hBox = new HBox(checkBox, productNameText, skuText, stockTextField, statusText);
                        hBox.setPadding(new Insets(2, 5, 2, 5));
                        setGraphic(hBox);
                    }

                    
                }
            }
        };
    }

    public ListingStockReasonCellFactory(List<ListingStockReason> selectOnlineSalesList){
        this.selectOnlineSalesList = selectOnlineSalesList;
    }
    
}
