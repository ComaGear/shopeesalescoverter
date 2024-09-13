import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

import com.colbertlum.contentHandler.BigSellerReportContentHandler;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;

public class ContentHandlerTest {

    private static final String REPORT_PATH = "C:\\Users\\comag\\Downloads\\Order.all.20240517_20240616.xlsx";
    
    @Test
    public void readingOrderMovement() throws InvalidFormatException, IOException, OpenXML4JException, SAXException, ParserConfigurationException{

        ArrayList<OrderStatusTracking> trackings = new ArrayList<OrderStatusTracking>();
        OrderStatusTracking orderStatusTracking = new OrderStatusTracking();
        orderStatusTracking.setOrderId("240517GV78DCMC");
        trackings.add(orderStatusTracking);

        File file = new File(REPORT_PATH);
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        XMLReader xmlReader = XMLHelper.newXMLReader();
        SpecifyOrderMovementContentHandler contentHandler = 
            new SpecifyOrderMovementContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), trackings);
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);

        assertNotNull(trackings.get(0).getItemMovementStatusList().get(0));
    }
}
