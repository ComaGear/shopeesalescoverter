package com.colbertlum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.UOM;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class MeasImputer {

    public static void imputeNameField(ArrayList<Meas> measList) {

        List<UOM> irsUoms = ShopeeSalesConvertApplication.getIrsUoms();

        measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
                return o1.getId().toLowerCase().compareTo(o2.getId().toLowerCase());
            }
            
        });

        irsUoms.removeIf(uom -> (uom.getRate() != 1));

        irsUoms.sort(new Comparator<UOM>() {

            @Override
            public int compare(UOM o1, UOM o2) {
                return o1.getProductId().toLowerCase().compareTo(o2.getProductId().toLowerCase());
            }
            
        });

        for(Meas meas : measList){
            UOM uom = binarySearch(meas, irsUoms);
            if(uom == null) continue;
            meas.setName(uom.getDescription());
        }
    }

    private static UOM binarySearch(Meas meas, List<UOM> uoms){
        
        int lo = 0;
        int hi = uoms.size()-1;

        while(lo <= hi) {
            int mid = lo + (hi-lo) / 2;
            if(uoms.get(mid).getProductId().toLowerCase().compareTo(meas.getId().toLowerCase()) > 0) hi = mid-1; 
            else if(uoms.get(mid).getProductId().toLowerCase().compareTo(meas.getId().toLowerCase()) < 0) lo = mid+1;
            else{
                return uoms.get(mid);
            }
        }
        return null;
    }

    public static VBox generatePanel(){
        Label productNameLabel = new Label("Product Name");
        TextField productNameField = new TextField();
        productNameField.setPromptText("search item by product description");
        productNameField.setPrefWidth(200);
        Label measurementLabel = new Label("Measurement");
        TextField measurementField = new TextField();
        measurementField.setPromptText("measure size default was 1.00");
        Label parentSkuLabel = new Label("parent Sku");
        TextField parentSkuField = new TextField();
        parentSkuField.setTooltip(new Tooltip("connect meas with parent sku id as child sku"));
        parentSkuField.setPromptText("grouping with same sku");
        Label label = new Label("Update Rule");
        TextField updateRuleField = new TextField();
        updateRuleField.setPromptText("default was 3t");

        
    }
    
}
