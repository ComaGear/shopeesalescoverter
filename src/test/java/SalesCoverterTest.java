import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;
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

import com.colbertlum.SalesConverter;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.contentHandler.OnlineSalesInfoContentHandler;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.Order;

public class SalesCoverterTest {
    
    // @Test
    // public void convertMoveOut(){
    //     ShopeeSalesConvertApplication shopeeSalesConvertApplication = new ShopeeSalesConvertApplication();
    //     SalesConverter salesConverter = new SalesConverter(shopeeSalesConvertApplication.getMoveOuts(), ShopeeSalesConvertApplication.getMeasList());
    //     salesConverter.process();

    // }

    @Test
    public void dateTest(){
        LocalDateTime localDateTime = LocalDateTime.parse("2024-01-03 15:25", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println(localDateTime.toLocalDate());
        System.out.println(localDateTime.toLocalTime());
    }

    // @Test
    public void readingOrderReport(){
        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        try {
            String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.REPORT);
            File file = new File(pathStr);
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            ShopeeOrderReportContentHandler contentHandler = new ShopeeOrderReportContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOuts);
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

        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 1);

        ArrayList<MoveOut> newMoveOuts = new ArrayList<MoveOut>();

        for(MoveOut moveOut : moveOuts){
            LocalDate shipOutDate = moveOut.getOrder().getShipOutDate();

            String status = moveOut.getOrder().getStatus();
            if(Order.STATUS_CANCEL.equals(status) || Order.STATUS_UNPAID.equals(status) || Order.STATUS_TO_SHIP.equals(status)){
                continue;
            }

            if(shipOutDate.isBefore(startDate) || shipOutDate.isAfter(endDate)){
                continue;
            }
            assertNotNull(moveOut.getOrder().getShipOutDate());
            newMoveOuts.add(moveOut);
        }
        newMoveOuts.get(0); 
    }
}
