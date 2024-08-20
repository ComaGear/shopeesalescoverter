package com.colbertlum.reporting;

import java.io.File;
import java.util.List;

import com.colbertlum.entity.MoveOut;

public class TempMovementReporting {
    
    public static void reporting(File folder, List<MoveOut> moveOuts){

        //TODO 
        new File(folder.getAbsolutePath() + "/ShippingMovement.xlsx");
    }
}
