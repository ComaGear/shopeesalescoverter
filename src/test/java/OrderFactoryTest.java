import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.colbertlum.Imputer.Utils.OrderFactory;
import com.colbertlum.constants.OrderInternalStatus;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class OrderFactoryTest {

    private String fileName = "ShopeeOrderSalesReport.xlsx";
    
    @Test
    public void testOrderStatusMappingMustSuccess() throws InvalidFormatException, IOException, OpenXML4JException, SAXException, ParserConfigurationException{

        List<MoveOut> allMoveOuts = new ArrayList<MoveOut>();

        File file = new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(fileName).getFile());
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        XMLReader xmlReader = XMLHelper.newXMLReader();
        ShopeeOrderReportContentHandler contentHandler = new ShopeeOrderReportContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), allMoveOuts);
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);
        List<Order> orders = contentHandler.getOrders();

        for(Order order : orders){
            OrderFactory.mappingOrderInternalStatus(order);
        }

        assertTrue(searchOrder(orders, "0CEXA").getInternalStatus() == OrderInternalStatus.SHIPPING);
        assertTrue(searchOrder(orders, "Q750V").getInternalStatus() == OrderInternalStatus.COMPLETED);
        assertTrue(searchOrder(orders, "32DKM").getInternalStatus() == OrderInternalStatus.CANCELLED);
        assertTrue(searchOrder(orders, "EWWXR").getInternalStatus() == OrderInternalStatus.AFTER_SALES_RETURN);
        assertTrue(searchOrder(orders, "D712S").getInternalStatus() == OrderInternalStatus.RETURNING);
    }

    Order searchOrder(List<Order> orders, String id){
        for(Order order : orders) {
            if(order.getId().equals(id)) return order;
        }
        return null;
    }
}
