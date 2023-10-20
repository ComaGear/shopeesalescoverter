

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
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.StockImputer;
import com.colbertlum.contentHandler.OnlineSalesInfoContentHandler;
import com.colbertlum.contentHandler.StockReportContentReader;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.ProductStock;

public class StockImputerTest {

    @Test
    public void readingStockReport(){
        List<ProductStock> stockReport = null;
        try {
            stockReport = StockReportContentReader.getStockReport();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(stockReport);
        assertNotEquals("Product Code", stockReport.get(0).getId());
    }

    @Test
    public void readingOnlineSalesInfo(){
        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        try {
            String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ONLINE_SALES_PATH);
            File file = new File(pathStr);
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertNotEquals(0, onlineSalesInfoList.size());
        assertNotNull(onlineSalesInfoList.get(0).getSku());
    }
    
    @Test
    public void readingUpdateRuleCSV(){
        StockImputer stockImputer = new StockImputer(null, null);
        Double updateRuleMeasure = 0d;
        try {
            updateRuleMeasure = stockImputer.getUpdateRuleMeasure("default");
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals(0.4d, updateRuleMeasure);
    }
}
