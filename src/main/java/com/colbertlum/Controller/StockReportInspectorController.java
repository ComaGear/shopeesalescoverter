package com.colbertlum.Controller;

import java.util.Comparator;
import java.util.List;

import com.colbertlum.cellFactory.ProductStockCell;
import com.colbertlum.entity.ProductStock;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class StockReportInspectorController {

    private static final String SEARCH_BY_ID = "ID";
    private static final String SEARCH_BY_NAME = "NAME";
    private static final String SEARCH_BY_TOTAL_AVAILABLE_STOCK = "COST";

    private static final String SORTING_BY_AVAILABLE_STOCK = "AVAILABLE STOCK";
    private static final String SORTING_BY_ALLOCATED_STOCK = "ALLOCATED STOCK";
    private static final String SORTING_BY_STOCK = "STOCK";
    private static final String SORTING_BY_NAME = "NAME";
    private static final String SORTING_BY_ID = "ID";
    private static final String SORTING_BY_TOTAL_AVAILABLE_STOCK_COST = "COST";
    public static final String fxmlFile = "views/StockReportInspectorPage.fxml";

    @FXML
    MenuButton sortingMenuButton;
    @FXML
    TextField searchBar;
    @FXML
    MenuButton searchByMenuButton;
    @FXML
    ListView<ProductStock> productStockListView;

    private FilteredList<ProductStock> filteredProductStockList;
    private ObservableList<ProductStock> observableProductStockList;
    private SortedList<ProductStock> sortedProductStocks;

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
            case SORTING_BY_TOTAL_AVAILABLE_STOCK_COST:
                sortingMenuButton.setText("Sorting By Cost");
                this.sortingMode = SORTING_BY_TOTAL_AVAILABLE_STOCK_COST;
                break;
        }
        sortingFilteredList();

        event.consume();
    }

    @FXML
    public void handleSearchByMenuItem(ActionEvent event){
        MenuItem clickedItem = (MenuItem) event.getSource();
        this.searchMode = clickedItem.getText();
        searchByMenuButton.setText("Search By " + this.searchMode);
    }

    private void sortingFilteredList(){
        switch (this.sortingMode) {
            case SORTING_BY_ID:
                sortedProductStocks.setComparator(Comparator.comparing(ProductStock::getId));
                break;
            case SORTING_BY_NAME:
                sortedProductStocks.setComparator(Comparator.comparing(ProductStock::getProductName));
                break;
            case SORTING_BY_STOCK:
                sortedProductStocks.setComparator(Comparator.comparing(ProductStock::getStock));
                break;
            case SORTING_BY_ALLOCATED_STOCK:
                sortedProductStocks.setComparator(Comparator.comparing(ProductStock::getAllocatedStock));
                break;
            case SORTING_BY_AVAILABLE_STOCK:
                sortedProductStocks.setComparator(Comparator.comparing(ProductStock::getAvailableStock));
                break;
            case SORTING_BY_TOTAL_AVAILABLE_STOCK_COST:
                sortedProductStocks.setComparator(Comparator.comparing(ProductStock::getTotalAvailableStockCost));
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
                case SEARCH_BY_TOTAL_AVAILABLE_STOCK:
                    matchStr = Double.toString(productStock.getTotalAvailableStockCost());
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
        this.sortedProductStocks = new SortedList<>(filteredProductStockList);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> handleSearchBar(oldValue, newValue));
        productStockListView.setCellFactory(ListView -> new ProductStockCell());
        productStockListView.setItems(sortedProductStocks);
        sortingFilteredList();
    }

    // public StockReportInspectorController() throws IOException{
    //     FXMLLoader fxmlLoader = new FXMLLoader();
    //     fxmlLoader.setLocation(ClassLoader.getSystemResource("/resources/views/" + fxmlFile));
    //     fxmlLoader.load();

    // }
}
