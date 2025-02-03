import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.SalesConverter;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class OrderServiceTest {

    private static final String ORDER_REPOSITORY_XLSX = "./OrderRepository.xlsx";
    private static final String UOM_SOURCE = "uom.xlsx";
    private static final String MEAS_SOURCE = "meas.xlsx";
    private static final String ORDER_SALES_REPORT = "orderSalesReport.xlsx";

    private OrderRepository orderRepository;
    private OrderService orderService;
    private static List<Meas> measList;
    private static List<Order> orders;

    private static List<MoveOut> allMoveOuts;
    private static Map<String, List<MoveOut>> classificedMoveOuts;

    @BeforeAll
    public static void sources() throws IOException, InvalidFormatException, OpenXML4JException, SAXException, ParserConfigurationException {
        // System.out.println(new File(getClass().getClassLoader().getResource(UOM_SOURCE).getFile()).getAbsolutePath());
        System.out.println(new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(MEAS_SOURCE).getFile()).getAbsolutePath());
        System.out.println("bofore");
        ShopeeSalesConvertApplication.saveProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH,
            new File(ORDER_REPOSITORY_XLSX).getAbsolutePath());
        ShopeeSalesConvertApplication.saveProperty(ShopeeSalesConvertApplication.UOM_STRING, 
            new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(UOM_SOURCE).getFile()).getAbsolutePath());
        ShopeeSalesConvertApplication.saveProperty(ShopeeSalesConvertApplication.MEAS, 
            new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(MEAS_SOURCE).getFile()).getAbsolutePath());
        ShopeeSalesConvertApplication.saveProperty(ShopeeSalesConvertApplication.REPORT, 
            new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(ORDER_SALES_REPORT).getFile()).getAbsolutePath());

        measList = ShopeeSalesConvertApplication.getMeasList();
        
        // report sources
        allMoveOuts = new ArrayList<MoveOut>();
        String pathStr = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.REPORT);
        File file = new File(pathStr);
        XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
        XMLReader xmlReader = XMLHelper.newXMLReader();
        ShopeeOrderReportContentHandler contentHandler = new ShopeeOrderReportContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), allMoveOuts);
        xmlReader.setContentHandler(contentHandler);
        InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
        xmlReader.parse(sheetData);
        orders = contentHandler.getOrders();

        classificedMoveOuts = new HashMap<String, List<MoveOut>>();
        classificedMoveOuts.put(OrderService.STATUS_CANCEL, new ArrayList<MoveOut>());
        classificedMoveOuts.put(OrderService.STATUS_COMPLETE, new ArrayList<MoveOut>());
        classificedMoveOuts.put(OrderService.STATUS_DELIVERED, new ArrayList<MoveOut>());
        classificedMoveOuts.put(OrderService.STATUS_RECEIVED, new ArrayList<MoveOut>());
        classificedMoveOuts.put(OrderService.STATUS_SHIPPING, new ArrayList<MoveOut>());
        classificedMoveOuts.put(OrderService.STATUS_TO_SHIP, new ArrayList<MoveOut>());
        classificedMoveOuts.put(OrderService.STATUS_UNPAID, new ArrayList<MoveOut>());
        for(MoveOut moveOut : allMoveOuts) {
            classificedMoveOuts.get(moveOut.getOrder().getStatus()).add(moveOut);
        }

        SalesConverter salesConverter = new SalesConverter(allMoveOuts, measList);
        salesConverter.process();
    }

    private static Stream<List<MoveOut>> provide() {
        List<List<MoveOut>> list = new ArrayList<List<MoveOut>>();
        for(List<MoveOut> moveOuts : classificedMoveOuts.values()){
            list.add(moveOuts);
        }
        return list.stream();
    }

    @BeforeEach
    public void createLoadingRepository() throws IOException{
        orderRepository = new OrderRepository(false);
        orderRepository.createRepositoryFile();
        orderRepository = new OrderRepository(true);
        
        orderService = new OrderService(orderRepository);
    }
    
    @AfterEach
    public void cleanUp(){
        new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH)).delete();
        new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.TEMP_MOVEMENT_FILE_PATH)).delete();
    }

    // @Test
    // public void shippingOrderToCompletedTest(){
    //     ArrayList<Order> orders = new ArrayList<Order>();
    //     Order order = new Order();
    //     order.setId("sses");
    //     order.setStatus(OrderService.STATUS_SHIPPING);
    //     orders.add(order);
    //     order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());

    //     new MoveOut();
    // }


    // test newShipping order will represent correcly at repository's shipping orders
    @MethodSource("provide")
    @ParameterizedTest
    public void newShippingOrderShouldExistedAtRepository(List<MoveOut> moveOuts){

        ArrayList<Order> shippingOrders = new ArrayList<Order>();
        for(MoveOut moveOut : classificedMoveOuts.get(OrderService.STATUS_SHIPPING)) {
            if(shippingOrders.contains(moveOut.getOrder())) return;
            shippingOrders.add(moveOut.getOrder());
        }
    
        orderService.process(moveOuts);

        if(shippingOrders.isEmpty()) assertTrue(orderRepository.getShippingOrders().isEmpty());
        else assertTrue(orderRepository.getShippingOrders().containsAll(shippingOrders));
        
    }
    // test newShipping and repository on shipping order will report a temporary movement record
    @MethodSource
    @ParameterizedTest
    public void newShippingAndExistedShippedRecordInTempMovementFile(List<MoveOut> moveOuts){

    }

    // test newCompleted order will represent correcly at repository's completed orders
    // test newCompleted order will removed from repository's shipping orders
    // test newCompleted order will report a movement record at completed order stage

    // test newReturnFailedDelivery order will represent correcly at repository's ReturnAfterShipping orders
    // test newReturnFailedDelivery order will removed from repository's shipping orders

    // test newReturnOnceCompleted order will represent correcly at repository's ReturnAfterCompleted orders
    // test newReturnOnceCompleted order will removed from repository's shipping orders
    // test newReturnOnceCompleted order will removed from repository's completed orders
    // test newReturnOnceCompleted order will report a movement record at completed order stage

    // test newReturnFalledDelivery order will represent at repository's inReturn movements
    // test newReturnOnceCompleted order will represent at repository's inReturn movements
    // test all inReturn movement's received stage reporting Credit Note movement.
    // test all inReturn movement's particularly received stage reporting Credit Note movement for reason both return and damaged.
    // test all inReturn movement's damaged stage reporting Credit Note movement for reason damaged.
    // test all inReturn movement's lost stage reporting Credit Note movement for reason damaged.
    // test all inReturn movement's none stage will not reporting.

}
