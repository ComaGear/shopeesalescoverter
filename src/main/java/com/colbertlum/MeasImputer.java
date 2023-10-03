package com.colbertlum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.UOM;

public class MeasImputer {

    public static void imputeNameField(ArrayList<Meas> measList) {

        List<UOM> irsUoms = ShopeeSalesConvertApplication.getIrsUoms();

        measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        });

        irsUoms.removeIf(uom -> (uom.getRate() != 1));

        irsUoms.sort(new Comparator<UOM>() {

            @Override
            public int compare(UOM o1, UOM o2) {
                return o1.getProductId().compareTo(o2.getProductId());
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
            if(uoms.get(mid).getProductId().compareTo(meas.getId()) > 0) hi = mid-1; 
            else if(uoms.get(mid).getProductId().compareTo(meas.getId()) < 0) lo = mid+1;
            else{
                return uoms.get(mid);
            }
        }
        return null;
    }

    
    
}
