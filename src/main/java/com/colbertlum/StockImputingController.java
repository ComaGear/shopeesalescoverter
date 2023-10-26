package com.colbertlum;

import java.util.List;

import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.OnlineSalesInfoStatus;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class StockImputingController {

    private Stage imputerStage;
    private List<OnlineSalesInfoStatus> onlineSalesInfoStatusList;
    private List<OnlineSalesInfoStatus> selectOnlineSalesList;

    public StockImputingController(Stage imputerStage, List<OnlineSalesInfoStatus> onlineSalesInfoStatusList) {
        this.imputerStage = imputerStage;
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
    }

    public void initStage() {
        ListView<OnlineSalesInfoStatus> onlineSalesInfoStatusListView = new ListView<OnlineSalesInfoStatus>();
        onlineSalesInfoStatusListView.setCellFactory(new OnlineSalesInfoStatusCellFactory(this.selectOnlineSalesList));

        MeasImputer measImputer = new MeasImputer();
    
        measImputer.generatePanel();
    }

    public Stage getStage() {
        return imputerStage;
    }

    public List<OnlineSalesInfo> getFixedOnlineInfo() {
        return null;
    }


}
