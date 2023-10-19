package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
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

import com.colbertlum.contentHandler.BigSellerReportContentHandler;
import com.colbertlum.contentHandler.MeasContentHandler;
import com.colbertlum.contentHandler.uomContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.UOM;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ShopeeSalesConvertApplication extends Application {


    private static final String OUTPUT_PATH = "output-path";
    public static final String MEAS = "meas";
    private static final String UOM = "uom";
    public static final String REPORT = "report";
    private String reportPath = "";
    private Stack<Scene> sceneStack;
    private Stage priStage;
    private MeasImputer measImputer;
    private Stage dialogStage;
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.priStage = primaryStage;

        primaryStage.setTitle("Shopee Sales Converter");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(500);

        MenuBar menuBar = setupMenuBar();

        String reportPathString = getProperty(REPORT);
        Text reportPathText = new Text(reportPathString);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));

        Button selectReportButton = new Button("select report");
        selectReportButton.setOnAction(e -> {
            File report = fileChooser.showOpenDialog(priStage);
            if(report == null) return;
            reportPathText.setText(report.getPath());
            saveProperty(REPORT, report.getPath());
            reportPath = report.getPath();
        });
        selectReportButton.setPrefWidth(80);
        HBox reportBarBox = new HBox(selectReportButton, reportPathText);

        TextField outputFileNameTextField = new TextField();
        LocalDate now = LocalDate.now();
        String date = now.getYear() + "." + now.getMonthValue() + "." + now.getDayOfMonth();
        outputFileNameTextField.setText("onlineSalesReport_" + date);

        Button processButton = new Button("PROCESS");
        processButton.setPrefWidth(80);
        
        processButton.setOnAction(e ->{
            List<MoveOut> processedMoveOuts = processSales();
            String outputFilePath = getProperty(OUTPUT_PATH) + "\\"+ outputFileNameTextField.getText() + ".xlsx";
            saveOutputToFile(processedMoveOuts, outputFilePath);
        });

        HBox processBarBox = new HBox(processButton, outputFileNameTextField);

        Separator separator = new Separator();
        
        VBox vBox = new VBox(menuBar, reportBarBox, processBarBox, separator);
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

    private List<MoveOut> processSales() {
        List<MoveOut> moveOuts = getMoveOuts();

        if(this.measImputer == null) this.measImputer = new MeasImputer(); 
        SalesConverter salesConverter = new SalesConverter(moveOuts, this.measImputer.getMeasList());
        salesConverter.process();
        
        if(salesConverter.hasEmptySkuMoveOut() || salesConverter.hasNotExistSkuMoveOut()){
            SalesImputer salesImputer = new SalesImputer(salesConverter.getEmptySkuMoveOuts(), salesConverter.getNotExistSkuMoveOuts());
            // SalesImputer salesImputer = new SalesImputer(null, null);kking
            salesImputer.setMeasList(this.measImputer.getMeasList());
            dialogStage = new Stage();
            dialogStage.setX(priStage.getX() + 10);
            dialogStage.setY(priStage.getY() + 10);
            salesImputer.initDialog(dialogStage);
            salesImputer.getStage().showAndWait();

            moveOuts = getMoveOuts();
            salesConverter = new SalesConverter(moveOuts, this.measImputer.getMeasList());
            salesConverter.process();
        }

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
            BigSellerReportContentHandler contentHandler = new BigSellerReportContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOuts);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
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

        return moveOuts;
    }

    private MenuBar setupMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().add(helpMenu);
        
        MenuItem menuItem = new MenuItem("Settings");
        helpMenu.getItems().add(menuItem);
        menuItem.setOnAction(e ->{
            this.pushScene(initSettingScene());
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

        Font font = new Font("monospace", 16);
        Text uomPathText = new Text(getProperty(UOM));
        uomPathText.setFont(font);
        Text measPathText = new Text(getProperty(MEAS));
        measPathText.setFont(font);


        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));

        Button selectUomButton = new Button("select Report");
        selectUomButton.setOnAction(e -> {
            File report = fileChooser.showOpenDialog(priStage);
            uomPathText.setText(report.getPath());
            saveProperty(UOM, report.getPath());
        });

        Button selectMeasButton = new Button("select uom file");
        selectMeasButton.setOnAction(e -> {
            File uomFile = fileChooser.showOpenDialog(priStage);
            measPathText.setText(uomFile.getPath());
            saveProperty(MEAS, uomFile.getPath());
        });



        VBox vBox = new VBox(backButton, measPathText, selectMeasButton, uomPathText, selectUomButton);


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

            XSSFRow row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(moveOut.getId());
            row.createCell(1).setCellValue(moveOut.getProductName());
            row.createCell(2).setCellValue(moveOut.getQuantity());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue(moveOut.getProductSubTotal() / moveOut.getQuantity());
        }

        try{
            
            FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
            // FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\comag\\Desktop\\" + outputFileNameStr + ".xlsx");
            workbook.write(fileOutputStream);

            fileOutputStream.close();
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
        inputStream.close();

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