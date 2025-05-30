package com.colbertlum.Controller;

import java.util.List;

import com.colbertlum.cellFactory.ProductStockCell;
import com.colbertlum.entity.ProductStock;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class StockReportInspectorController {

    private static final String SEARCH_BY_ID = "ID";
    private static final String SEARCH_BY_NAME = "NAME";
    private static final String SORTING_BY_AVAILABLE_STOCK = "AVAILABLE STOCK";
    private static final String SORTING_BY_ALLOCATED_STOCK = "ALLOCATED STOCK";
    private static final String SORTING_BY_STOCK = "STOCK";
    private static final String SORTING_BY_NAME = "NAME";
    private static final String SORTING_BY_ID = "ID";

    public static final String fxmlFile = "views/StockReportInspectorPage.fxml";

    @FXML
    MenuButton sortingMenuButton;
    @FXML
    TextField searchBar;
    @FXML
    ListView<ProductStock> productStockListView;

    private FilteredList<ProductStock> filteredProductStockList;
    private ObservableList<ProductStock> observableProductStockList;

    private List<ProductStock> productStockList;
    private String sortingMode;
    private String searchMode;

    public void setProductStockList(List<ProductStock> productStockList) {
        this.productStockList = productStockList;
        this.observableProductStockList.clear();
        this.observableProductStockList.setAll(productStockList);
    }

    public List<ProductStock> getProductStockList(){
        return productStockList;
    }
    
    @FXML
    public void handleSortingByMenuItem(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();

        clickedItem.getText();

        switch (clickedItem.getText()) {
            case SORTING_BY_ID:
                sortingMenuButton.setText("Sorting By SKU");
                this.sortingMode = SORTING_BY_ID;
                break;
            case SORTING_BY_NAME:
                sortingMenuButton.setText("Sorting By NAME");
                this.sortingMode = SORTING_BY_NAME;
                break;
            case SORTING_BY_STOCK:
                sortingMenuButton.setText("Sorting By STOCK");
                this.sortingMode = SORTING_BY_ID;
                break;
            case SORTING_BY_ALLOCATED_STOCK:
                sortingMenuButton.setText("Sorting By ALLOCATED STOCK");
                this.sortingMode = SORTING_BY_ALLOCATED_STOCK;
                break;
            case SORTING_BY_AVAILABLE_STOCK:
                sortingMenuButton.setText("Sorting By AVAILABLE STOCK");
                this.sortingMode = SORTING_BY_AVAILABLE_STOCK;
                break;
        }
        sortingFilteredList();
    }

    private void sortingFilteredList(){
        switch (this.sortingMode) {
            case SORTING_BY_ID:
                filteredProductStockList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
                break;
            case SORTING_BY_NAME:
                filteredProductStockList.sort((o1, o2) -> o1.getProductName().compareTo(o2.getProductName()));
                break;
            case SORTING_BY_STOCK:
                filteredProductStockList.sort((o1, o2) -> (o1.getStock() >= (o2.getStock())) ? 1 : -1);
                break;
            case SORTING_BY_ALLOCATED_STOCK:
                filteredProductStockList.sort((o1, o2) -> (o1.getAllocatedStock() >= (o2.getAllocatedStock())) ? 1 : -1);
                break;
            case SORTING_BY_AVAILABLE_STOCK:
                filteredProductStockList.sort((o1, o2) -> (o1.getAvailableStock() >= (o2.getAvailableStock())) ? 1 : -1);
                break;
        }
    }

    private void handleSearchBar(String oldValue, String newValue){
        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
        filteredProductStockList.setPredicate(productStock -> {
            if (newVal == null || newVal.isEmpty()) return true;
            String[] splitStr = newVal.split("\\s+");

            String matchStr = null;
            switch(this.searchMode) {
                case SEARCH_BY_NAME:
                    matchStr = productStock.getProductName().toLowerCase();
                    break;
                case SEARCH_BY_ID:
                    matchStr = productStock.getId().toLowerCase();
                    break;
            }

            int i = 0;
            int match = 0;
            while(i < splitStr.length && !(matchStr.indexOf(splitStr[i].toLowerCase()) < 0)){

                int subIndex = matchStr.indexOf(splitStr[i].toLowerCase());
                matchStr = matchStr.substring(subIndex+splitStr[i].length(), matchStr.length());
                match++;
                i++;
            }
            if(i >= splitStr.length && match >= splitStr.length){
                return true;
            }
            
            return false;
        });
    });

    }

    @FXML
    public void initialize(){
        this.sortingMode = SORTING_BY_ID;
        this.searchMode = SEARCH_BY_NAME;

        this.observableProductStockList = FXCollections.observableArrayList();
        this.filteredProductStockList = new FilteredList<>(this.observableProductStockList);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> handleSearchBar(oldValue, newValue));
        productStockListView.setCellFactory(ListView -> new ProductStockCell());
        productStockListView.setItems(filteredProductStockList);
        sortingFilteredList();
    }

    // public StockReportInspectorController() throws IOException{
    //     FXMLLoader fxmlLoader = new FXMLLoader();
    //     fxmlLoader.setLocation(ClassLoader.getSystemResource("/resources/views/" + fxmlFile));
    //     fxmlLoader.load();

    // }
}
