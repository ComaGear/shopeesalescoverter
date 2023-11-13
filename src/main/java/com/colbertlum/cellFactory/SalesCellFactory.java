package com.colbertlum.cellFactory;

import java.util.List;

import com.colbertlum.entity.MoveOutStatus;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class SalesCellFactory implements Callback<ListView<MoveOutStatus>, ListCell<MoveOutStatus>>{

    private List<MoveOutStatus> selectedMoveOutStatusList;

    @Override
    public ListCell<MoveOutStatus> call(ListView<MoveOutStatus> param) {
        return new ListCell<>() {
            
            @Override
            public void updateItem(MoveOutStatus moveOutStatus, boolean empty){
                super.updateItem(moveOutStatus, empty);

                if(moveOutStatus == null || empty) {
                    setText("");
                    setGraphic(null);
                    return;
                }

                CheckBox checkBox = new CheckBox();
                if(selectedMoveOutStatusList.contains(moveOutStatus)) checkBox.setSelected(true);
                checkBox.setOnAction(a -> {
                    if(checkBox.isSelected()) {
                        selectedMoveOutStatusList.add(moveOutStatus);
                    } else {
                        selectedMoveOutStatusList.remove(moveOutStatus);
                    }
                });
                checkBox.setPrefWidth(20);

                Text skuText = new Text(moveOutStatus.getMoveOut().getSku());
                skuText.setWrappingWidth(90);
                Text nameText = new Text(moveOutStatus.getMoveOut().getProductName());
                nameText.setWrappingWidth(450);
                Text variationText = new Text(moveOutStatus.getMoveOut().getVariationName());
                variationText.setWrappingWidth(200);
                Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
                foundRow.setWrappingWidth(100);
                Text status = new Text(moveOutStatus.getStatus());
                status.setWrappingWidth(100);

                setGraphic(new HBox(checkBox, skuText, nameText, variationText, foundRow, status));
            }
        };
    }

    public SalesCellFactory(List<MoveOutStatus> selectedMoveOutStatusList){
        this.selectedMoveOutStatusList = selectedMoveOutStatusList;
    }
    
}
