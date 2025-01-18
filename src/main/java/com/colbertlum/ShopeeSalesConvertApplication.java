package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.Controller.HandleReturnController;
import com.colbertlum.Controller.MeasImputingController;
import com.colbertlum.Controller.SalesImputingController;
import com.colbertlum.Controller.StockImputingController;
import com.colbertlum.Exception.OnlineSalesInfoException;
import com.colbertlum.Imputer.MeasImputer;
import com.colbertlum.Imputer.StockImputer;
import com.colbertlum.contentHandler.BigSellerReportContentHandler;
import com.colbertlum.contentHandler.MeasContentHandler;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.contentHandler.StockReportContentReader;
import com.colbertlum.contentHandler.uomContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.SummaryOrder;
import com.colbertlum.entity.UOM;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ShopeeSalesConvertApplication extends Application {


    public static final String BIG_SELLER = "Big Seller";
    public static final String SHOPEE_ORDER = "Shopee Order";
    public static final String DATA_SOURCE_TYPE = "data-source-type";
    private static final String OUTPUT_PATH = "output-path";
    public static final String MEAS = "meas";
    private static final String UOM_STRING = "uom";
    public static final String REPORT = "report";
    public static final String ONLINE_SALES_PATH = "onlineSales-path";
    public static final String STOCK_REPORT_PATH = "stock-report-path";
    public static final String TEMP_MOVEMENT_FILE_PATH = "temp_movement_path";
    public static final String COMPLETE_ORDER_PATH = "completed_movement_path";
    public static final String CREDIT_NOTE_PATH = "credit-note-path";
    public static final String ORDER_REPOSITORY_PATH = "order_repository_path";

    private List<UOM> uoms;

    private String reportPath = "";
    private Stack<Scene> sceneStack;
    private Stage priStage;
    private MeasImputer measImputer;
    private Stage dialogStage;
    private StockImputingController stockImputingController;
    private LocalDate startDate;
    private LocalDate endDate;
    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.generateResourcesFile();
        this.priStage = primaryStage;

        primaryStage.setTitle("Shopee Sales Converter");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(500);

        Font font = new Font(14);

        MenuBar menuBar = setupMenuBar();

        Text reportPathHeaderText = new Text("Sales Report Position : ");
        reportPathHeaderText.setFont(font);
        String reportPathString = getProperty(REPORT);
        Text reportPathText = new Text(reportPathString);
        reportPathText.setFont(font);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));

        Button selectSalesReportButton = new Button("select report");
        selectSalesReportButton.setOnAction(e -> {
            File report = fileChooser.showOpenDialog(priStage);
            if(report == null) return;
            reportPathText.setText(report.getPath());
            saveProperty(REPORT, report.getPath());
            reportPath = report.getPath();
        });
        selectSalesReportButton.setPrefWidth(100);
        
        Region reportBarSpacer = new Region();
        HBox.setHgrow(reportBarSpacer, Priority.ALWAYS);
        HBox reportBarBox = new HBox(reportPathHeaderText, reportPathText, reportBarSpacer, selectSalesReportButton);
        reportBarBox.setPadding(new Insets(5));
        
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setPrefWidth(120);
        endDatePicker.setPrefWidth(120);
        startDatePicker.setOnAction(e ->{
            startDate = startDatePicker.getValue();
        });
        endDatePicker.setOnAction(e ->{
            endDate = endDatePicker.getValue();
        });

        // Text startText = new Text("Start Date : ");
        // Text endText = new Text("End Date : ");
        // startText.setFont(font);
        // endText.setFont(font);
        // HBox datePickerHBox = new HBox(startText, startDatePicker, endText, endDatePicker);
        // datePickerHBox.setPadding(new Insets(5));

        TextField outputFileNameTextField = new TextField();
        LocalDate now = LocalDate.now();
        String date = now.getYear() + "." + now.getMonthValue() + "." + now.getDayOfMonth();
        Text outputPathText = new Text(getProperty(OUTPUT_PATH));
        outputPathText.setFont(font);
        outputFileNameTextField.setPrefWidth(200);
        outputFileNameTextField.setText("onlineSalesReport_" + date);

        Button processButton = new Button("PROCESS");
        processButton.setPrefWidth(100);
        
        processButton.setOnAction(e ->{
            processSales();
            // String outputFilePath = getProperty(OUTPUT_PATH) + "\\"+ outputFileNameTextField.getText() + ".xlsx";
            // if(this.uoms == null) uoms = getIrsUoms();
            // saveOutputToFile(processedMoveOuts, uoms, outputFilePath);
        });
        
        Region processBarSpacer = new Region();
        HBox.setHgrow(processBarSpacer, Priority.ALWAYS);
        HBox processBarBox = new HBox(outputPathText, outputFileNameTextField, processBarSpacer, processButton);
        processBarBox.setPadding(new Insets(5));

        Separator separator = new Separator();
        
        Text onlineMassUpdateHeaderText = new Text("OnlineMassUpdate File Position : ");
        onlineMassUpdateHeaderText.setFont(font);
        Text onlineMassUpdateFilePathText = new Text(getProperty(ONLINE_SALES_PATH));
        onlineMassUpdateFilePathText.setFont(font);
        Button selectOnlineSalesInfoButton = new Button("select mass update item sales stock generate by shopee");
        // selectOnlineSalesInfoButton.setPrefWidth(400);
        selectOnlineSalesInfoButton.setOnAction(e ->{
            File onlineMassUpdateSalesFile = fileChooser.showOpenDialog(primaryStage);
            onlineMassUpdateFilePathText.setText(onlineMassUpdateSalesFile.getPath());
            saveProperty(ONLINE_SALES_PATH, onlineMassUpdateSalesFile.getPath());
        });
        Button imputeStockButton = new Button("Impute Stock");
        imputeStockButton.setPrefWidth(400);
        imputeStockButton.setOnAction(handleImputeAction());
        
        Region onlineMassUpdateBarSpacer = new Region();
        HBox.setHgrow(onlineMassUpdateBarSpacer, Priority.ALWAYS);
        VBox massUpdateBox = new VBox(new HBox(onlineMassUpdateHeaderText, onlineMassUpdateFilePathText), 
            new HBox(selectOnlineSalesInfoButton, onlineMassUpdateBarSpacer, imputeStockButton));
        massUpdateBox.setPadding(new Insets(5));

        VBox vBox;
        if(getProperty(DATA_SOURCE_TYPE).equals(SHOPEE_ORDER)) {
            // vBox = new VBox(menuBar, reportBarBox, datePickerHBox, processBarBox, separator, massUpdateBox);
            vBox = new VBox(menuBar, reportBarBox, processBarBox, separator, massUpdateBox);
        } else {
            vBox = new VBox(menuBar, reportBarBox, processBarBox, separator, massUpdateBox);
        }
        Scene scene = new Scene(vBox, 1000, 500);
        
        // primaryStage.setScene(scene);

        pushScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                if(dialogStage != null) dialogStage.close();
            }
            
        });
    }

    private EventHandler<ActionEvent> handleImputeAction() {
        return e -> {
            StockImputer stockImputer = null;
            try {
                stockImputer = new StockImputer(StockReportContentReader.getStockReport(), getMeasList());
            } catch (IOException e1) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("you must select valid Stock Report File \n that generate from biztory");
                alert.showAndWait();
                return;
            }

            List<OnlineSalesInfo> onlineSalesInfoList;
            try {
                onlineSalesInfoList = stockImputer.getOnlineSalesInfoList(new File(getProperty(ONLINE_SALES_PATH)));
            } catch (IOException e1) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText(e1.getMessage());
                alert.showAndWait();
                return;
            }

            try {
                stockImputer.figureStock(onlineSalesInfoList);
            } catch (OnlineSalesInfoException e1) {
                Stage imputerStage = new Stage();
                imputerStage.setX(priStage.getX() + 5);
                imputerStage.setY(priStage.getY() + 5);
                stockImputingController = new StockImputingController(imputerStage, e1.getOnlineSalesInfoStatusList());
                stockImputingController.initStage();
                stockImputingController.getStage().showAndWait();

                // imputerStage.setOnCloseRequest(new EventHandler<WindowEvent>(){

                //     @Override
                //     public void handle(WindowEvent event) {
                //         List<OnlineSalesInfo> fixedOnlineInfo = stockImputingController.getFixedOnlineInfo();
                //         StockImputer stockImputer2 = null;
                //         try {
                //             stockImputer2 = new StockImputer(StockReportContentReader.getStockReport(), getMeasList());
                //         } catch (IOException e) {
                //             Alert alert = new Alert(AlertType.ERROR);
                //             alert.setContentText(e.getMessage());
                //             alert.showAndWait();
                //         }
                //         for(OnlineSalesInfo info : fixedOnlineInfo){
                //             stockImputer2.updateOnlineSalesInfo(info, onlineSalesInfoList);
                //         }
                //         new Alert(AlertType.INFORMATION, Integer.toString(fixedOnlineInfo.get(0).getQuantity()), ButtonType.OK).show();
                //     }
                // });
            }
            try {
                StockImputer.saveOutputToFile(onlineSalesInfoList, new File(getProperty(ONLINE_SALES_PATH)));
                new Alert(AlertType.INFORMATION, "Online Sales Info Updated", ButtonType.OK).show();
            } catch (IOException e2) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText(e2.getMessage());
                alert.showAndWait();
            }
            
        };
    }

    private void processSales() {
        List<MoveOut> moveOuts = getMoveOuts();

        if(this.measImputer == null) this.measImputer = new MeasImputer(); 
        SalesConverter salesConverter = new SalesConverter(moveOuts, this.measImputer.getMeasList());
        salesConverter.process();
        
        if(salesConverter.hasEmptySkuMoveOut() || salesConverter.hasNotExistSkuMoveOut()){
            SalesImputingController salesImputingController = 
                new SalesImputingController(salesConverter.getEmptySkuMoveOuts(), salesConverter.getNotExistSkuMoveOuts()
                    , salesConverter.getAdvanceFillMoveOuts());
            dialogStage = new Stage();
            dialogStage.setX(priStage.getX() + 10);
            dialogStage.setY(priStage.getY() + 10);
            salesImputingController.initDialog(dialogStage);
            salesImputingController.getStage().showAndWait();

            moveOuts = getMoveOuts();
            salesConverter = new SalesConverter(moveOuts, new MeasImputer().getMeasList());
            salesConverter.process();
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("output file to : " + getProperty(OUTPUT_PATH));
        alert.show();
        // return moveOuts;

    OrderService orderService = new OrderService(new OrderRepository());
        orderService.process(moveOuts);
    }

    public static ArrayList<Meas> getMeasList() {

        ArrayList<Meas> measList = new ArrayList<Meas>();

        try {
            String pathStr = getProperty(MEAS);
            File file = new File(pathStr);
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            MeasContentHandler contentHandler = new MeasContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(),
                measList);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (IOException | OpenXML4JException e) {
            Stage warningStage = initWarningStage("you must select meas file");
            warningStage.show();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return measList;
    }

    public static List<UOM> getIrsUoms() {

        ArrayList<UOM> uoms = new ArrayList<UOM>();

        try {
            String pathStr = getProperty(UOM_STRING);
            File file = new File(pathStr);
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            uomContentHandler contentHandler = new uomContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(),
                uoms);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (IOException | OpenXML4JException e) {
            Stage warningStage = initWarningStage("you must select uom file");
            warningStage.show();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return uoms;
    }

    public List<MoveOut> getMoveOuts() {

        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        
        try {
            String pathStr = getProperty(REPORT);
            if(pathStr == null) pathStr = reportPath;
            File file = new File(pathStr);
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            XMLReader xmlReader = XMLHelper.newXMLReader();
            if(getProperty(DATA_SOURCE_TYPE).equals(SHOPEE_ORDER)) {
                ShopeeOrderReportContentHandler contentHandler = new ShopeeOrderReportContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOuts);
                xmlReader.setContentHandler(contentHandler);

            } else {
                BigSellerReportContentHandler contentHandler = new BigSellerReportContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOuts);
                xmlReader.setContentHandler(contentHandler);
            }
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (IOException | OpenXML4JException e) {
            Stage warningStage = initWarningStage("you must select report file");
            warningStage.show();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // filtering by start and end date.
        if(getProperty(DATA_SOURCE_TYPE).equals(SHOPEE_ORDER)) {

            if(startDate == null || endDate == null){
                new Alert(AlertType.ERROR, "please choose valid date.", ButtonType.OK).showAndWait();
                throw new IllegalArgumentException("not valid start or end date");
            } 

            if(startDate.isAfter(endDate)){
                new Alert(AlertType.ERROR, "start date could not greater than end date", ButtonType.OK).showAndWait();
                throw new IllegalArgumentException("not valid start or end date");
            } 

            ArrayList<MoveOut> newMoveOuts = new ArrayList<MoveOut>();

            for(MoveOut moveOut : moveOuts){

                String status = moveOut.getOrder().getStatus();
                if(Order.STATUS_UNPAID.equals(status) || Order.STATUS_TO_SHIP.equals(status)){
                    continue;
                }
                LocalDate shipOutDate = moveOut.getOrder().getShipOutDate();
                if(shipOutDate == null){
                    continue;
                }

                if(shipOutDate.isBefore(startDate) || shipOutDate.isAfter(endDate)){
                    continue;
                }
                newMoveOuts.add(moveOut);
            }
            return newMoveOuts;
        }

        return moveOuts;
    }

    private MenuBar setupMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().add(helpMenu);
        
        MenuItem settingMenuItem = new MenuItem("Settings");
        helpMenu.getItems().add(settingMenuItem);
        settingMenuItem.setOnAction(e ->{
            this.pushScene(initSettingScene());
        });

        MenuItem measMenuItem = new MenuItem("modify Measurement");
        helpMenu.getItems().add(measMenuItem);
        measMenuItem.setOnAction(e ->{
            MeasImputingController measImputingController = new MeasImputingController();
            Stage stage = new Stage();
            stage.setX(priStage.getX() + 5);
            stage.setY(priStage.getY() + 5);

            measImputingController.initDialog(stage);
            stage.showAndWait();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    measImputingController.close();
                }
                
            });
        });

        MenuItem handleReturnMenuItem = new MenuItem("handle returning");
        helpMenu.getItems().add(handleReturnMenuItem);
        handleReturnMenuItem.setOnAction((e) -> {
            HandleReturnController handleReturnController = new HandleReturnController();
            Stage stage = new Stage();
            handleReturnController.initDialog(stage);
            // Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            // stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            // stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
            stage.showAndWait();
            
        });
        

        return menuBar;
    }

    private void pushScene(Scene scene) {
        if(sceneStack == null) sceneStack = new Stack<Scene>();

        sceneStack.push(scene);
        priStage.setScene(sceneStack.peek());
    }

    private void popScene(){
        sceneStack.pop();
        priStage.setScene(sceneStack.peek());
    }

    public static Stage initWarningStage(String warningMessage){
        Stage dialogStage = new Stage();

        Scene scene = new Scene(new HBox(new Text(warningMessage)));
        dialogStage.setScene(scene);
        dialogStage.setTitle("Warning");
        dialogStage.setWidth(400);
        dialogStage.setHeight(200);
        dialogStage.setAlwaysOnTop(true);

        return dialogStage;
    }

    private Scene initSettingScene() {

        Button backButton = new Button("back");
        backButton.setOnAction(e -> {
            popScene();
        });

        int buttonWidth = 300;

        Font font = new Font("monospace", 16);
        Text uomPathText = new Text(getProperty(UOM_STRING));
        uomPathText.setFont(font);
        Text measPathText = new Text(getProperty(MEAS));
        measPathText.setFont(font);
        Text stockReportPathText = new Text(getProperty(STOCK_REPORT_PATH));
        stockReportPathText.setFont(font);
        Text outputPathText = new Text(getProperty(OUTPUT_PATH));
        outputPathText.setFont(font);
        Text dataSourceText = new Text("process sales from " + getProperty(DATA_SOURCE_TYPE));
        dataSourceText.setFont(font);
        Text tempMovementPathText = new Text("save Temp Movement Report at : '" + getProperty(TEMP_MOVEMENT_FILE_PATH) + "'");
        tempMovementPathText.setFont(font);
        Text completeOrderMovementPathText = new Text("save completed order report at : '" + getProperty(COMPLETE_ORDER_PATH) + "'");
        completeOrderMovementPathText.setFont(font);
        Text creditNotePathText = new Text("credit note report at : '" + getProperty(CREDIT_NOTE_PATH) + "'");
        creditNotePathText.setFont(font);
        Text orderRepositoryPathText = new Text("Order Record Repository at : '" + getProperty(ORDER_REPOSITORY_PATH) + "'");
        orderRepositoryPathText.setFont(font);
        
        FileChooser xlsxFileChooser = new FileChooser();
        xlsxFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));
        FileChooser csvFileChooser = new FileChooser();
        csvFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv File", "*.csv"));
        DirectoryChooser folderChooser = new DirectoryChooser();

        Button selectUomButton = new Button("select UOM File (Irs System Export data)");
        selectUomButton.setPrefWidth(buttonWidth);
        selectUomButton.setOnAction(e -> {
            File report = xlsxFileChooser.showOpenDialog(priStage);
            uomPathText.setText(report.getPath());
            saveProperty(UOM_STRING, report.getPath());
        });

        Button selectMeasButton = new Button("select Online Meas File");
        selectMeasButton.setPrefWidth(buttonWidth);
        selectMeasButton.setOnAction(e -> {
            File uomFile = xlsxFileChooser.showOpenDialog(priStage);
            measPathText.setText(uomFile.getPath());
            saveProperty(MEAS, uomFile.getPath());
        });

        Button selectStockReportButton = new Button("select stock report from Biztory");
        selectStockReportButton.setPrefWidth(buttonWidth);
        selectStockReportButton.setOnAction(e ->{
            File stockReportFile = csvFileChooser.showOpenDialog(priStage);
            if(!stockReportFile.exists()) return;
            stockReportPathText.setText(stockReportFile.getPath());
            saveProperty(STOCK_REPORT_PATH, stockReportFile.getPath());
        });

        Button selectOutputPathButton = new Button("select output generate path");
        selectOutputPathButton.setPrefWidth(buttonWidth);
        selectOutputPathButton.setOnAction(e ->{
            File outputFolder = folderChooser.showDialog(priStage);
            outputPathText.setText(outputFolder.getPath());
            saveProperty(OUTPUT_PATH, outputFolder.getPath());
        });

        MenuButton reportSourceMenuButton = new MenuButton("Edit Data Source");
        reportSourceMenuButton.setPrefWidth(buttonWidth);
        MenuItem bigSellerReportSourceItem = new MenuItem(BIG_SELLER);
        MenuItem shopeeReportSourceItem = new MenuItem(SHOPEE_ORDER);
        bigSellerReportSourceItem.setOnAction(e ->{
            saveProperty(DATA_SOURCE_TYPE, BIG_SELLER);
            dataSourceText.setText("process sales from Big Seller");
        });
        shopeeReportSourceItem.setOnAction(e ->{
            saveProperty(DATA_SOURCE_TYPE, SHOPEE_ORDER);
            dataSourceText.setText("process sales from Shopee Order");
        });
        reportSourceMenuButton.getItems().add(bigSellerReportSourceItem);
        reportSourceMenuButton.getItems().add(shopeeReportSourceItem);
        

        Button selectTempMovementReportPathButton = new Button("select temporary movement report file");
        selectTempMovementReportPathButton.setPrefWidth(buttonWidth);
        selectTempMovementReportPathButton.setOnAction(e -> {
            
            File file = xlsxFileChooser.showOpenDialog(priStage);
            if(file == null) return;
            saveProperty(TEMP_MOVEMENT_FILE_PATH, file.getPath());
            tempMovementPathText.setText("save Temp Movement Report at : '" + file.getPath() + "'");
        });

        Button selectCompletedOrderReportFolderPathButton = new Button("select completed order movement report location");
        selectCompletedOrderReportFolderPathButton.setPrefWidth(buttonWidth);
        selectCompletedOrderReportFolderPathButton.setOnAction(e -> {
            
            File folder = folderChooser.showDialog(priStage);
            if(folder == null) return;
            saveProperty(COMPLETE_ORDER_PATH, folder.getPath());
            completeOrderMovementPathText.setText("save Completed Order Movement Report at : '" + folder.getPath() + "'");
        });

        Button selectCreditNoteReportFolderPathButton = new Button("select credit note report location");
        selectCreditNoteReportFolderPathButton.setPrefWidth(buttonWidth);
        selectCreditNoteReportFolderPathButton.setOnAction(e -> {
            
            File folder = folderChooser.showDialog(priStage);
            if(folder == null) return;
            saveProperty(CREDIT_NOTE_PATH, folder.getPath());
            completeOrderMovementPathText.setText("save Credit Note Report at : '" + folder.getPath() + "'");
        });

        VBox vBox = new VBox(backButton, 
            measPathText, selectMeasButton,
            uomPathText, selectUomButton, 
            stockReportPathText, selectStockReportButton,
            outputPathText, selectOutputPathButton,
            dataSourceText, reportSourceMenuButton,
            tempMovementPathText, selectTempMovementReportPathButton,
            completeOrderMovementPathText, selectCompletedOrderReportFolderPathButton,
            creditNotePathText, selectCreditNoteReportFolderPathButton);

        return new Scene(vBox, 600, 400);
    }

    private void saveOutputToFile(List<MoveOut> moveOuts, List<UOM> uoms, String outputFilePath){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet biztorySheet = workbook.createSheet("biztory");
        

        int rowCount = 0;
        XSSFRow headerRow = biztorySheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("Code");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Qty");
        headerRow.createCell(3).setCellValue("unit");
        headerRow.createCell(4).setCellValue("Unit Price");

        for(MoveOut moveOut : moveOuts){

            if(moveOut.getQuantity() == 0) continue; 

            String productName = moveOut.getProductName() + " - " + moveOut.getVariationName();

            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            productName = productName.replaceAll(characterFilter,"");

            XSSFRow row = biztorySheet.createRow(rowCount++);
            row.createCell(0).setCellValue(moveOut.getId());
            row.createCell(1).setCellValue(productName);
            row.createCell(2).setCellValue(moveOut.getQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(moveOut.getProductSubTotal() / moveOut.getQuantity());
        }


        XSSFSheet orderDetailSheet = workbook.createSheet("order profit");
        writeOrderSummarySheet(orderDetailSheet, moveOuts);

        XSSFSheet movementDetailSheet = workbook.createSheet("movement detail");
        writeProductProfitSummarySheet(movementDetailSheet, moveOuts);

        try{
            
            FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
            // FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\comag\\Desktop\\" + outputFileNameStr + ".xlsx");
            workbook.write(fileOutputStream);

            fileOutputStream.close();
            workbook.close();
        }catch(IOException exception){
            exception.printStackTrace();
            System.out.println(exception.toString());
        }
    }

    private void writeProductProfitSummarySheet(XSSFSheet movementDetailSheet, List<MoveOut> moveOuts) {

        if(this.uoms == null) this.uoms = getIrsUoms();

        XSSFRow orderDetailHeaderRow = movementDetailSheet.createRow(0);
        orderDetailHeaderRow.createCell(0).setCellValue("Order ID");
        orderDetailHeaderRow.createCell(1).setCellValue("Order Shipout Date");
        orderDetailHeaderRow.createCell(2).setCellValue("Product ID");
        orderDetailHeaderRow.createCell(3).setCellValue("Product Name");
        orderDetailHeaderRow.createCell(4).setCellValue("Qauntity");
        orderDetailHeaderRow.createCell(5).setCellValue("Cost");
        orderDetailHeaderRow.createCell(6).setCellValue("SubTotal");
        orderDetailHeaderRow.createCell(7).setCellValue("SubCost");
        orderDetailHeaderRow.createCell(8).setCellValue("Profit");
        orderDetailHeaderRow.createCell(9).setCellValue("Profit Rate");
        orderDetailHeaderRow.createCell(10).setCellValue("transaction Fee");
        orderDetailHeaderRow.createCell(11).setCellValue("Service Fee");
        orderDetailHeaderRow.createCell(12).setCellValue("Commission Fee");
        orderDetailHeaderRow.createCell(13).setCellValue("Management Fee");
        orderDetailHeaderRow.createCell(14).setCellValue("Grand Total");
        orderDetailHeaderRow.createCell(15).setCellValue("Order Shipping Fee");


        moveOuts.sort(new Comparator<MoveOut>() {

            @Override
            public int compare(MoveOut o1, MoveOut o2) {
                return o1.getOrder().getId().compareTo(o2.getOrder().getId());
            }
            
        });

        uoms.removeIf(uom -> (uom.getRate() != 1));
        int index = 1;
        for(MoveOut moveOut : moveOuts){

            UOM uom = null;
            if(moveOut.getId() != null) {
                // TODO Why this moveOut using orderID? it should be Meas's id.
                uom = UOM.binarySearch(moveOut.getId(), uoms); 
            } else {
                uom = new UOM();
                uom.setProductId("");
                uom.setCostPrice(moveOut.getProductSubTotal() / moveOut.getQuantity());
            }

            XSSFRow row = movementDetailSheet.createRow(index);
            row.createCell(0).setCellValue(moveOut.getOrder().getId());
            row.createCell(1).setCellValue(moveOut.getOrder().getShipOutDate());
            row.createCell(2).setCellValue(moveOut.getId());
            row.createCell(3).setCellValue(moveOut.getProductName() + "-" + moveOut.getVariationName());
            row.createCell(4).setCellValue(moveOut.getQuantity());
            row.createCell(5).setCellValue(uom.getCostPrice());
            row.createCell(6).setCellValue(moveOut.getProductSubTotal());
            row.createCell(7).setCellValue(uom.getCostPrice() * moveOut.getQuantity());
            row.createCell(8).setCellValue(moveOut.getProductSubTotal() - (uom.getCostPrice() * moveOut.getQuantity()));
            row.createCell(9).setCellValue(1 - ((uom.getCostPrice() * moveOut.getQuantity()) / moveOut.getProductSubTotal()));
            row.createCell(10).setCellValue(moveOut.getOrder().getTransactionFee());
            row.createCell(11).setCellValue(moveOut.getOrder().getCommissionFee());
            row.createCell(12).setCellValue(moveOut.getOrder().getServiceFee());
            row.createCell(13).setCellValue(moveOut.getOrder().getManagementFee());
            row.createCell(14).setCellValue(moveOut.getOrder().getOrderTotalAmount());
            row.createCell(15).setCellValue(moveOut.getOrder().getShippingFee());

            index++;
        }
    }

    private void writeOrderSummarySheet(XSSFSheet orderDetailSheet, List<MoveOut> moveOuts) {

        if(this.uoms == null) this.uoms = getIrsUoms();

        XSSFRow orderDetailHeaderRow = orderDetailSheet.createRow(0);
        orderDetailHeaderRow.createCell(0).setCellValue("Order ID");
        orderDetailHeaderRow.createCell(1).setCellValue("Order Shipout Date");
        orderDetailHeaderRow.createCell(2).setCellValue("Total Amount");
        orderDetailHeaderRow.createCell(3).setCellValue("Profit");
        orderDetailHeaderRow.createCell(4).setCellValue("Profit Rate");

        moveOuts.sort(new Comparator<MoveOut>() {

            @Override
            public int compare(MoveOut o1, MoveOut o2) {
                return o1.getOrder().getId().compareTo(o2.getOrder().getId());
            }
            
        });


        uoms.removeIf(uom -> (uom.getRate() != 1));
        ArrayList<SummaryOrder> summaryOrders = new ArrayList<SummaryOrder>();
        uoms.sort(new Comparator<UOM>() {

            @Override
            public int compare(UOM o1, UOM o2) {
                return o1.getProductId().toLowerCase().compareTo(o2.getProductId().toLowerCase());
            }
            
        });

        for(MoveOut moveOut : moveOuts){
            SummaryOrder lastOrder = null;
            if(!summaryOrders.isEmpty()) {
                lastOrder = summaryOrders.get(summaryOrders.size()-1);
            } else {
                lastOrder = new SummaryOrder();
            }
            
            UOM uom = null;
            if(moveOut.getId() != null) {
                uom = UOM.binarySearch(moveOut.getId(), uoms);
            } else {
                uom = new UOM();
                uom.setProductId("");
                uom.setCostPrice(moveOut.getProductSubTotal() / moveOut.getQuantity());
            }
            double moveOutProfit = moveOut.getProductSubTotal() - (uom.getCostPrice() * moveOut.getQuantity());
            if(lastOrder.getId() != null && lastOrder.getId().equals(moveOut.getOrder().getId())) {
               lastOrder.setProfit(lastOrder.getProfit() + moveOutProfit);
               lastOrder.setTotalAmount(lastOrder.getTotalAmount() + moveOut.getProductSubTotal());
            } else {
                lastOrder = new SummaryOrder();
                lastOrder.setId(moveOut.getOrder().getId());
                lastOrder.setProfit(moveOutProfit);
                lastOrder.setShipOutDate(moveOut.getOrder().getShipOutDate());
                lastOrder.setTotalAmount(moveOut.getProductSubTotal());
                summaryOrders.add(lastOrder);
            }
            
        }

        int index = 1;
        for(SummaryOrder order : summaryOrders){
            XSSFRow row = orderDetailSheet.createRow(index);
            row.createCell(0).setCellValue(order.getId());
            row.createCell(1).setCellValue(order.getShipOutDate());
            row.createCell(2).setCellValue(order.getTotalAmount());
            row.createCell(3).setCellValue(order.getProfit());
            row.createCell(4).setCellValue(order.getProfit() / order.getTotalAmount());
            index++;
        }
    }

    private static Properties getProperties() throws IOException{
        Properties properties;
        
        // InputStream inputStream = ClassLoader.getSystemResourceAsStream("/IrsSalesConverter.properties");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("./ShopeeSalesConvertApplication.properties");
        } catch (FileNotFoundException e){
            inputStream = ClassLoader.getSystemResourceAsStream("ShopeeSalesConvertApplication.properties");
        }
        
        properties = new Properties();

        properties.load(inputStream);
        if(inputStream != null )inputStream.close();

        return properties;
    }

    public static String getProperty(String key){
        try{
            Properties properties = getProperties();
            return properties.getProperty(key);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static void saveProperty(String key, String value) {
        try{
            Properties properties = getProperties();
            properties.setProperty(key, value);
            saveProperties(properties);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void saveProperties(Properties properties) throws IOException{
        FileOutputStream fileOutputStream = new FileOutputStream("./ShopeeSalesConvertApplication.properties");

        properties.store(fileOutputStream, null);

        fileOutputStream.close();
    }

    public void generateResourcesFile(){
        generateResourcesOfUpdateRule();
        generateResourcesOfProperties();

    }

    public void generateResourcesOfProperties(){
            
        File shopeeSalesConverterPropertiesFile = new File("./ShopeeSalesConvertApplication.properties");

        if(shopeeSalesConverterPropertiesFile.exists()) {
            return;
        }

        try {
            shopeeSalesConverterPropertiesFile.createNewFile();

            saveProperty("meas", "C:\\Users");
            saveProperty("uom", "C:\\Users");
            saveProperty("report", "C:\\Users");
            saveProperty("output-path", "C:\\Users");
            saveProperty("onlineSales-path", "C:\\Users");
            saveProperty("stock-report-path", "C:\\Users");
            saveProperty("data-source-type", "Shopee Order");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void generateResourcesOfUpdateRule(){
        File updateRuleFile = new File("./updateRule.csv");

        if(updateRuleFile.exists()) {
            return;
        }

        try {
            updateRuleFile.createNewFile();

            FileWriter fileWriter = new FileWriter(updateRuleFile);
            fileWriter.append("1t, 1.0\n");
            fileWriter.append("2t, 0.7\n");
            fileWriter.append("3t, 0.5\n");
            fileWriter.append("4t, 0.4\n");
            fileWriter.append("disc, 0.0\n");
            fileWriter.append("default, 0.4\n");
            fileWriter.append("5t, 0.3\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}