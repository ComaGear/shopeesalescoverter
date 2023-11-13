package com.colbertlum.cellFactory;

import java.util.List;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfoStatus;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MeasCellFactory implements Callback<ListView<Meas>, ListCell<Meas>> {

    private List<Meas> selectedMeasList;

    public Meas getSelectedMeas() {
        return selectedMeasList.get(0);
    }

    @Override
    public ListCell<Meas> call(ListView<Meas> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Meas meas, boolean empty){
                super.updateItem(meas, empty);  
                if(meas == null || empty){
                    setText("");
                    setGraphic(null);
                    return;
                }

                CheckBox checkBox = new CheckBox();
                if(!selectedMeasList.isEmpty() && selectedMeasList.get(0).equals(meas)) checkBox.setSelected(true);
                checkBox.setOnAction(a -> {
                    if(!selectedMeasList.isEmpty() && selectedMeasList.get(0).equals(meas)) {
                        checkBox.setSelected(false);
                        selectedMeasList.clear();
                    } else {
                        selectedMeasList.add(0, meas);
                    }
                });
                checkBox.setPrefWidth(20);
                

                Text relativeIdText = new Text(meas.getRelativeId());
                relativeIdText.setWrappingWidth(90);
                Text nameText = new Text(meas.getName());
                nameText.setWrappingWidth(650);
                Text measuremenText = new Text(Double.toString(meas.getMeasurement()));
                measuremenText.setWrappingWidth(100);
                Text idText = new Text(meas.getId());
                idText.setWrappingWidth(100);
                Text updateRuleText = new Text(meas.getUpdateRule());
                updateRuleText.setWrappingWidth(100);

                setGraphic(new HBox(checkBox, relativeIdText, nameText, measuremenText, idText, updateRuleText));
            }
        };
    }
    
    public MeasCellFactory(List<Meas> selectedMeasList){
        this.selectedMeasList = selectedMeasList;
    }
}
