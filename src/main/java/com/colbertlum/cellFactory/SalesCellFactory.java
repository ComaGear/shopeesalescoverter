package com.colbertlum.cellFactory;

import java.util.List;

import com.colbertlum.entity.MoveOutReason;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class SalesCellFactory implements Callback<ListView<MoveOutReason>, ListCell<MoveOutReason>>{

    private List<MoveOutReason> selectedMoveOutStatusList;

    @Override
    public ListCell<MoveOutReason> call(ListView<MoveOutReason> param) {
        return new ListCell<>() {
            
            @Override
            public void updateItem(MoveOutReason moveOutStatus, boolean empty){
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
                Text nameText = new Text(moveOutStatus.getMoveOut().getName());
                nameText.setWrappingWidth(650);
                Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
                foundRow.setWrappingWidth(100);
                Text UOMName = new Text(moveOutStatus.getMoveOut().getProductId());
                UOMName.setWrappingWidth(300);
                Text status = new Text(moveOutStatus.getStatus());
                status.setWrappingWidth(100);

                setGraphic(new HBox(checkBox, skuText, nameText, foundRow, status));
            }
        };
    }

    public SalesCellFactory(List<MoveOutReason> selectedMoveOutStatusList){
        this.selectedMoveOutStatusList = selectedMoveOutStatusList;
    }
    
}
