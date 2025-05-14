

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Exception.ListingStockException;
import com.colbertlum.Imputer.StockImputer;
import com.colbertlum.Imputer.Utils.OnlineSalesInfoFactory;
import com.colbertlum.contentHandler.OnlineSalesInfoContentHandler;
import com.colbertlum.contentHandler.StockReportContentFactory;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.ListingStockReason;
import com.colbertlum.entity.ProductStock;

public class StockImputerTest {

    // @Test
    public void updateMassUpdateFileStock() throws IOException, InvalidFormatException, OpenXML4JException, SAXException, ParserConfigurationException{
        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ONLINE_SALES_PATH);
        File file = new File(pathStr);
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
        XMLReader xmlReader = XMLHelper.newXMLReader();
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);

        StockImputer stockImputer = new StockImputer(StockReportContentFactory.getStockReport(), ShopeeSalesConvertApplication.getMeasList());
        // try {
        //     stockImputer.figureStock(onlineSalesInfoList);
        // } catch (ListingStockException e) {
        //     List<ListingStockReason> onlineSalesInfoStatusList = e.getListingStockStatusList();
        //     for(ListingStockReason status : onlineSalesInfoStatusList){
        //         OnlineSalesInfo onlineSalesInfo = status.getOnlineSalesInfo();
        //         onlineSalesInfo.setQuantity(0);
        //         stockImputer.retieveUpdateOnlineSalesInfo(onlineSalesInfo, onlineSalesInfoList);
        //     }
        // }

        // StockImputer.saveOutputToFile(onlineSalesInfoList, new File(pathStr));
    }
    

    // @Test
    public void saveFileShouldSuccess() throws IOException, InvalidFormatException, OpenXML4JException, SAXException, ParserConfigurationException{

        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ONLINE_SALES_PATH);
        File file = new File(pathStr);
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
        XMLReader xmlReader = XMLHelper.newXMLReader();
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);

        // try modify some record;
        onlineSalesInfoList.get(0).setParentSku("22");
        onlineSalesInfoList.get(0).setSku("22-a");
        onlineSalesInfoList.get(0).setPrice(1d);
        onlineSalesInfoList.get(3).setQuantity(0);

        // StockImputer.saveOutputToFile(onlineSalesInfoList, new File(pathStr));
        // OnlineSalesInfoFactory.saveOutputToFile(onlineSalesInfoList, new File(pathStr));
    }

    // @Test
    public void figureStockShouldSuccess() throws IOException, SAXException, InvalidFormatException, OpenXML4JException, ParserConfigurationException{
        List<ProductStock> stockReport;
        stockReport = StockReportContentFactory.getStockReport();
        ArrayList<Meas> measList = ShopeeSalesConvertApplication.getMeasList();
        StockImputer stockImputer = new StockImputer(stockReport, measList);


        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ONLINE_SALES_PATH);
        File file = new File(pathStr);
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
        XMLReader xmlReader = XMLHelper.newXMLReader();
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);
        
        int originStock = 0;
        for(OnlineSalesInfo info : onlineSalesInfoList){
            originStock += info.getQuantity();
        }

        
        // try {
        //     stockImputer.figureStock(onlineSalesInfoList);
        // } catch (ListingStockException e) {
        //     List<ListingStockReason> onlineSalesInfoStatusList = e.getListingStockStatusList();
        //     for(ListingStockReason status : onlineSalesInfoStatusList){
        //         assertNotNull(status.getStatus());
        //         assertNotNull(status.getOnlineSalesInfo());
        //     }
        // }

        int stock = 0;
        for(OnlineSalesInfo info : onlineSalesInfoList){
            stock += info.getQuantity();
        }

        assertNotEquals(originStock, stock);
        
        
    }
    @Test
    public void readingStockReport(){
        List<ProductStock> stockReport = null;
        try {
            stockReport = StockReportContentFactory.getStockReport();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(stockReport);
        assertNotEquals("Product Code", stockReport.get(0).getId());
        for(ProductStock stock : stockReport){
            assertNotNull(stock.getId());
            assertNotNull(stock.getAvailableStock());
        }
    }

    // @Test
    public void readingOnlineSalesInfo() throws InvalidFormatException, IOException, OpenXML4JException, SAXException, ParserConfigurationException{
        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ONLINE_SALES_PATH);
        File file = new File(pathStr);
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
        XMLReader xmlReader = XMLHelper.newXMLReader();
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);


        assertNotEquals(0, onlineSalesInfoList.size());
        assertNotNull(onlineSalesInfoList.get(3).getSku());
        for(OnlineSalesInfo info : onlineSalesInfoList){
            assertNotNull(info.getProductId());
            assertNotEquals(0d, info.getPrice());
            assertNotNull(info.getQuantity());
        }
    }
    
    @Test
    public void readingUpdateRuleCSV(){
        StockImputer stockImputer = new StockImputer(null, null);
        Double updateRuleMeasure = 0d;
        try {
            updateRuleMeasure = stockImputer.getUpdateRuleMeasure("default");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        assertTrue(0.4d == updateRuleMeasure);
    }
}
