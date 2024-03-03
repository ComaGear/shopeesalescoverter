package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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

import com.colbertlum.Controller.MeasImputingController;
import com.colbertlum.Controller.SalesImputingController;
import com.colbertlum.Controller.StockImputingController;
import com.colbertlum.Exception.OnlineSalesInfoException;
import com.colbertlum.contentHandler.BigSellerReportContentHandler;
import com.colbertlum.contentHandler.MeasContentHandler;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.contentHandler.StockReportContentReader;
import com.colbertlum.contentHandler.uomContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.UOM;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ShopeeSalesConvertApplication extends Application {


    public static final String BIG_SELLER = "Big Seller";
    public static final String SHOPEE_ORDER = "Shopee Order";
    public static final String DATA_SOURCE_TYPE = "data-source-type";
    private static final String OUTPUT_PATH = "output-path";
    public static final String MEAS = "meas";
    private static final String UOM = "uom";
    public static final String REPORT = "report";
    public static final String ONLINE_SALES_PATH = "onlineSales-path";
    public static final String STOCK_REPORT_PATH = "stock-report-path";
    private String reportPath = "";
    private Stack<Scene> sceneStack;
    private Stage priStage;
    private MeasImputer measImputer;
    private Stage dialogStage;
    private StockImputingController stockImputingController;
    private LocalDate startDate;
    private LocalDate endDate;
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.priStage = primaryStage;

        primaryStage.setTitle("Shopee Sales Converter");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(500);

        Font font = new Font(14);

        MenuBar menuBar = setupMenuBar();

        String reportPathString = getProperty(REPORT);
        Text reportPathText = new Text(reportPathString);
        reportPathText.setFont(font);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));

        Button selectBigSellerReportButton = new Button("select report");
        selectBigSellerReportButton.setOnAction(e -> {
            File report = fileChooser.showOpenDialog(priStage);
            if(report == null) return;
            reportPathText.setText(report.getPath());
            saveProperty(REPORT, report.getPath());
            reportPath = report.getPath();
        });
        selectBigSellerReportButton.setPrefWidth(100);
        HBox reportBarBox = new HBox(selectBigSellerReportButton, reportPathText);
        
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

        Text startText = new Text("Start Date : ");
        Text endText = new Text("End Date : ");
        startText.setFont(font);
        endText.setFont(font);
        HBox datePickerHBox = new HBox(startText, startDatePicker, endText, endDatePicker);

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
            List<MoveOut> processedMoveOuts = processSales();
            String outputFilePath = getProperty(OUTPUT_PATH) + "\\"+ outputFileNameTextField.getText() + ".xlsx";
            saveOutputToFile(processedMoveOuts, outputFilePath);
        });

        HBox processBarBox = new HBox(processButton, outputPathText, outputFileNameTextField);

        Separator separator = new Separator();
        
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
        imputeStockButton.setPrefWidth(100);
        imputeStockButton.setOnAction(handleImputeAction());
        
        VBox massUpdateBox = new VBox(new HBox(selectOnlineSalesInfoButton, imputeStockButton), onlineMassUpdateFilePathText);

        VBox vBox;
        if(getProperty(DATA_SOURCE_TYPE).equals(SHOPEE_ORDER)) {
            vBox = new VBox(menuBar, reportBarBox, datePickerHBox, processBarBox, separator, massUpdateBox);
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

    private List<MoveOut> processSales() {
        List<MoveOut> moveOuts = getMoveOuts();

        if(this.measImputer == null) this.measImputer = new MeasImputer(); 
        SalesConverter salesConverter = new SalesConverter(moveOuts, this.measImputer.getMeasList());
        salesConverter.process();
        
        if(salesConverter.hasEmptySkuMoveOut() || salesConverter.hasNotExistSkuMoveOut()){
            SalesImputingController salesImputingController = new SalesImputingController(salesConverter.getEmptySkuMoveOuts(), salesConverter.getNotExistSkuMoveOuts());
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
        return moveOuts;
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
            String pathStr = getProperty(UOM);
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
        Text uomPathText = new Text(getProperty(UOM));
        uomPathText.setFont(font);
        Text measPathText = new Text(getProperty(MEAS));
        measPathText.setFont(font);
        Text stockReportPathText = new Text(getProperty(STOCK_REPORT_PATH));
        stockReportPathText.setFont(font);
        Text outputPathText = new Text(getProperty(OUTPUT_PATH));
        outputPathText.setFont(font);
        Text dataSourceText = new Text("process sales from " + getProperty(DATA_SOURCE_TYPE));
        dataSourceText.setFont(font);

        FileChooser xlsxFileChooser = new FileChooser();
        xlsxFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));

        Button selectUomButton = new Button("select UOM File (Irs System Export data)");
        selectUomButton.setPrefWidth(buttonWidth);
        selectUomButton.setOnAction(e -> {
            File report = xlsxFileChooser.showOpenDialog(priStage);
            uomPathText.setText(report.getPath());
            saveProperty(UOM, report.getPath());
        });

        Button selectMeasButton = new Button("select Online Meas File");
        selectMeasButton.setPrefWidth(buttonWidth);
        selectMeasButton.setOnAction(e -> {
            File uomFile = xlsxFileChooser.showOpenDialog(priStage);
            measPathText.setText(uomFile.getPath());
            saveProperty(MEAS, uomFile.getPath());
        });

        FileChooser csvFileChooser = new FileChooser();
        csvFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv File", "*.csv"));
        Button selectStockReportButton = new Button("select stock report from Biztory");
        selectStockReportButton.setPrefWidth(buttonWidth);
        selectStockReportButton.setOnAction(e ->{
            File stockReportFile = csvFileChooser.showOpenDialog(priStage);
            if(!stockReportFile.exists()) return;
            stockReportPathText.setText(stockReportFile.getPath());
            saveProperty(STOCK_REPORT_PATH, stockReportFile.getPath());
        });

        DirectoryChooser folderChooser = new DirectoryChooser();
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
        

        VBox vBox = new VBox(backButton, measPathText, selectMeasButton, uomPathText, selectUomButton, 
            stockReportPathText, selectStockReportButton, outputPathText, selectOutputPathButton,
            dataSourceText, reportSourceMenuButton);


        return new Scene(vBox, 600, 400);
    }

    private void saveOutputToFile(List<MoveOut> moveOuts, String outputFilePath){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        

        int rowCount = 0;
        XSSFRow headerRow = sheet.createRow(rowCount++);
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

            XSSFRow row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(moveOut.getId());
            row.createCell(1).setCellValue(productName);
            row.createCell(2).setCellValue(moveOut.getQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(moveOut.getProductSubTotal() / moveOut.getQuantity());
        }

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
}