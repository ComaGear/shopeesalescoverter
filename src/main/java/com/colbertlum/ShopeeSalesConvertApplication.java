package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Stack;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ShopeeSalesConvertApplication extends Application {


    private Stack<Scene> sceneStack;
    private Stage priStage;
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.priStage = primaryStage;

        primaryStage.setTitle("Shopee Sales Converter");
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);

        MenuBar menuBar = setupMenuBar();

        VBox vBox = new VBox(menuBar);


        Scene scene = new Scene(vBox, 600, 300);
        
        // primaryStage.setScene(scene);

        pushScene(scene);
        primaryStage.show();

        // Stage salesImputerStage = new Stage();
        // SalesImputer salesImputer = new SalesImputer();
        // salesImputer.initDialog(salesImputerStage);
        // salesImputerStage.show();
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

    private Scene initSettingScene() {

        Button backButton = new Button("back");
        backButton.setOnAction(e -> {
            popScene();
        });

        Font font = new Font("monospace", 16);
        Text uomPathText = new Text(getProperty("uom"));
        uomPathText.setFont(font);
        Text measPathText = new Text(getProperty("meas"));
        measPathText.setFont(font);


        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel File", "*.xlsx"));
        // fileChooser.setInitialDirectory(new File(pathname));

        Button selectUomButton = new Button("select Report");
        selectUomButton.setOnAction(e -> {
            File report = fileChooser.showOpenDialog(priStage);
            uomPathText.setText(report.getPath());
            saveProperty("uom", report.getPath());
        });

        Button selectMeasButton = new Button("select uom file");
        selectMeasButton.setOnAction(e -> {
            File uomFile = fileChooser.showOpenDialog(priStage);
            measPathText.setText(uomFile.getPath());
            saveProperty("meas", uomFile.getPath());
        });



        VBox vBox = new VBox(backButton, measPathText, selectMeasButton, uomPathText, selectUomButton);


        return new Scene(vBox, 600, 400);
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

    private static String getProperty(String key){
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
        FileOutputStream fileOutputStream = new FileOutputStream("./IrsSalesConverter.properties");

        properties.store(fileOutputStream, null);

        fileOutputStream.close();
    }
}