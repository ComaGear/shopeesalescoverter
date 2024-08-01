package com.colbertlum.reporting;

import java.io.File;
import java.util.List;

import com.colbertlum.entity.MoveOut;

public class CompletedMovementReporting {
    
    public static void reporting(File folder, List<MoveOut> moveOuts){
    

        new File(folder.getAbsolutePath() + "/Completed Movement 2024.3.1.xlsx");
    }
}
