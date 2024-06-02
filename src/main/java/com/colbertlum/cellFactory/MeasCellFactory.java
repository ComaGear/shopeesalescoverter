package com.colbertlum.cellFactory;

import java.util.List;

import com.colbertlum.MeasImputer;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfoStatus;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MeasCellFactory implements Callback<ListView<Meas>, ListCell<Meas>> {

    private List<Meas> selectedMeasList;

    Clipboard clipboard = Clipboard.getSystemClipboard();

    private MeasImputer imputer;

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
                nameText.setWrappingWidth(530);
                Text measuremenText = new Text(Double.toString(meas.getMeasurement()));
                measuremenText.setWrappingWidth(50);
                Text idText = new Text(meas.getId());
                idText.setWrappingWidth(100);

                MenuButton menu = getMenu(meas);
                menu.setPrefWidth(70);
                // Text updateRuleText = new Text(meas.getUpdateRule());
                // updateRuleText.setWrappingWidth(50);

                Button copySkuButton = new Button("copy");
                copySkuButton.setOnAction(e ->{
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(meas.getRelativeId());
                    clipboard.setContent(clipboardContent);
                });

                setGraphic(new HBox(checkBox, relativeIdText, nameText, measuremenText, idText, menu, copySkuButton));

            }

            private MenuButton getMenu(Meas meas) {
                MenuButton menu = new MenuButton(meas.getUpdateRule());
                MenuItem t1MenuItem = new MenuItem("1t");
                MenuItem t2MenuItem = new MenuItem("2t");
                MenuItem t3MenuItem = new MenuItem("3t");
                MenuItem t4MenuItem = new MenuItem("4t");
                MenuItem t5MenuItem = new MenuItem("5t");
                MenuItem discMenuItem = new MenuItem("disc");
                t1MenuItem.setOnAction(e -> {
                    menu.setText("1t");
                    getItem().setUpdateRule("1t");
                    imputer.setMeasChange(true);
                });
                t2MenuItem.setOnAction(e -> {
                    menu.setText("2t");
                    getItem().setUpdateRule("2t");
                    imputer.setMeasChange(true);
                });
                t3MenuItem.setOnAction(e -> {
                    menu.setText("3t");
                    getItem().setUpdateRule("3t");
                    imputer.setMeasChange(true);
                });
                t4MenuItem.setOnAction(e -> {
                    menu.setText("4t");
                    getItem().setUpdateRule("4t");
                    imputer.setMeasChange(true);
                });
                t5MenuItem.setOnAction(e -> {
                    menu.setText("5t");
                    getItem().setUpdateRule("5t");
                    imputer.setMeasChange(true);
                });
                discMenuItem.setOnAction(e -> {
                    menu.setText("disc");
                    getItem().setUpdateRule("disc");
                    imputer.setMeasChange(true);
                });

                menu.getItems().add(t1MenuItem);
                menu.getItems().add(t2MenuItem);
                menu.getItems().add(t3MenuItem);
                menu.getItems().add(t4MenuItem);
                menu.getItems().add(t5MenuItem);
                menu.getItems().add(discMenuItem);

                return menu;
            }
        };
    }
    
    public MeasCellFactory(List<Meas> selectedMeasList, MeasImputer imputer){
        this.selectedMeasList = selectedMeasList;
        this.imputer = imputer;
    }
}
