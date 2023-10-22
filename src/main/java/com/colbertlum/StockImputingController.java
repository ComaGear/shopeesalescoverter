package com.colbertlum;

import java.util.List;

import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.OnlineSalesInfoStatus;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public class StockImputingController {

    private Stage imputerStage;
    private List<OnlineSalesInfoStatus> onlineSalesInfoStatusList;

    public StockImputingController(Stage imputerStage, List<OnlineSalesInfoStatus> onlineSalesInfoStatusList) {
        this.imputerStage = imputerStage;
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
    }

    public void initStage() {

    }

    public Stage getStage() {
        return imputerStage;
    }

    public List<OnlineSalesInfo> getFixedOnlineInfo() {
    }


}
