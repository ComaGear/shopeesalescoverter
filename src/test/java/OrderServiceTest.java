import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.assertj.core.internal.bytebuddy.asm.Advice.Argument;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.SalesConverter;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.contentHandler.ShopeeOrderReportContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class OrderServiceTest {

    private static final String 

    private static final String ORDER_REPOSITORY_XLSX = "./OrderRepository.xlsx";
    private static final String UOM_SOURCE = "uom.xlsx";
    private static final String MEAS_SOURCE = "meas.xlsx";
    private static final String ORDER_SALES_REPORT = "orderSalesReport.xlsx";
    private static final String SECOND_ORDER_SALES_REPORT = "orderSalesReport2.xlsx";

    private OrderRepository orderRepository;
    private OrderService orderService;
    private static List<Meas> measList;
    private static List<Order> orders;
    private static List<MoveOut> allMoveOuts;
    private static Map<String, List<MoveOut>> classificedMoveOuts;
    private static Map<String, List<Order>> classificedOrders;

    private static List<Order> secondOrders;
    private static List<MoveOut> secondMoveOutsList;
    private static Map<String, List<MoveOut>> secondClassificedMoveOuts;
    private static Map<String, List<Order>> secondClassificedOrders;

    @BeforeAll
    public static void sources() throws IOException, InvalidFormatException, OpenXML4JException, SAXException, ParserConfigurationException {
        // System.out.println(new File(getClass().getClassLoader().getResource(UOM_SOURCE).getFile()).getAbsolutePath());
        System.out.println(new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(MEAS_SOURCE).getFile()).getAbsolutePath());
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
        classificedOrders = new HashMap<String, List<Order>>();
        classificedOrders.put(OrderService.STATUS_CANCEL, new ArrayList<Order>());
        classificedOrders.put(OrderService.STATUS_COMPLETE, new ArrayList<Order>());
        classificedOrders.put(OrderService.STATUS_DELIVERED, new ArrayList<Order>());
        classificedOrders.put(OrderService.STATUS_RECEIVED, new ArrayList<Order>());
        classificedOrders.put(OrderService.STATUS_SHIPPING, new ArrayList<Order>());
        classificedOrders.put(OrderService.STATUS_TO_SHIP, new ArrayList<Order>());
        classificedOrders.put(OrderService.STATUS_UNPAID, new ArrayList<Order>());
        for(Order order : orders) {
            classificedOrders.get(order.getStatus()).add(order);
        }

        ShopeeSalesConvertApplication.saveProperty(ShopeeSalesConvertApplication.REPORT, 
            new File(ShopeeSalesConvertApplication.class.getClassLoader().getResource(SECOND_ORDER_SALES_REPORT).getFile()).getAbsolutePath());
        secondMoveOutsList = new ArrayList<MoveOut>();
        String pathStr2 = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.REPORT);
        File file2 = new File(pathStr);
        XSSFReader xssfReader2 = new XSSFReader(OPCPackage.open(file2));
        XMLReader xmlReader2 = XMLHelper.newXMLReader();
        ShopeeOrderReportContentHandler contentHandler2 = new ShopeeOrderReportContentHandler(xssfReader2.getSharedStringsTable(), xssfReader2.getStylesTable(), secondMoveOutsList);
        xmlReader.setContentHandler(contentHandler2);
        InputSource sheetData2 = new InputSource(xssfReader2.getSheetsData().next());
        xmlReader.parse(sheetData2);
        secondOrders = contentHandler2.getOrders();

        secondClassificedMoveOuts = new HashMap<String, List<MoveOut>>();
        secondClassificedMoveOuts.put(OrderService.STATUS_CANCEL, new ArrayList<MoveOut>());
        secondClassificedMoveOuts.put(OrderService.STATUS_COMPLETE, new ArrayList<MoveOut>());
        secondClassificedMoveOuts.put(OrderService.STATUS_DELIVERED, new ArrayList<MoveOut>());
        secondClassificedMoveOuts.put(OrderService.STATUS_RECEIVED, new ArrayList<MoveOut>());
        secondClassificedMoveOuts.put(OrderService.STATUS_SHIPPING, new ArrayList<MoveOut>());
        secondClassificedMoveOuts.put(OrderService.STATUS_TO_SHIP, new ArrayList<MoveOut>());
        secondClassificedMoveOuts.put(OrderService.STATUS_UNPAID, new ArrayList<MoveOut>());
        for(MoveOut moveOut : secondMoveOutsList) {
            secondClassificedMoveOuts.get(moveOut.getOrder().getStatus()).add(moveOut);
        }
        secondClassificedOrders = new HashMap<String, List<Order>>();
        secondClassificedOrders.put(OrderService.STATUS_CANCEL, new ArrayList<Order>());
        secondClassificedOrders.put(OrderService.STATUS_COMPLETE, new ArrayList<Order>());
        secondClassificedOrders.put(OrderService.STATUS_DELIVERED, new ArrayList<Order>());
        secondClassificedOrders.put(OrderService.STATUS_RECEIVED, new ArrayList<Order>());
        secondClassificedOrders.put(OrderService.STATUS_SHIPPING, new ArrayList<Order>());
        secondClassificedOrders.put(OrderService.STATUS_TO_SHIP, new ArrayList<Order>());
        secondClassificedOrders.put(OrderService.STATUS_UNPAID, new ArrayList<Order>());
        for(Order order : secondOrders) {
            secondClassificedOrders.get(order.getStatus()).add(order);
        }
        SalesConverter salesConverter = new SalesConverter(allMoveOuts, measList);
        salesConverter.process();
        SalesConverter salesConverter2 = new SalesConverter(secondMoveOutsList, measList);
        salesConverter2.process();
    }

    private static Stream<List<MoveOut>> provide() {
        List<List<MoveOut>> list = new ArrayList<List<MoveOut>>();
        for(List<MoveOut> moveOuts : classificedMoveOuts.values()){
            list.add(moveOuts);
        }
        return list.stream();
    }

    private static Stream<Arguments> provideSameStatusMultiList() {
        List<Arguments> list = new ArrayList<Arguments>();
        for(String key : classificedMoveOuts.keySet()){
            list.add(Arguments.of(classificedMoveOuts.get(key), secondClassificedMoveOuts.get(key)) );
        }
        return list.stream();
    }

    private static Stream<Arguments> provideRandomStatusMultiList() {

        Random random = new Random();
        List<String> keyList = new ArrayList<String>(classificedMoveOuts.keySet());
        List<List<MoveOut>> moveOutsList = new ArrayList<List<MoveOut>>();
        while(keyList.size() > 0){
            int index = random.nextInt(keyList.size() - 0) + 0;
            moveOutsList.add(secondClassificedMoveOuts.get(keyList.get(index)));
            keyList.remove(index);
        }

        List<Arguments> list = new ArrayList<Arguments>();
        Iterator<String> iterator = classificedMoveOuts.keySet().iterator();
        int i = 0;
        while(iterator.hasNext()){
            list.add(Arguments.of(classificedMoveOuts.get(iterator.next()), moveOutsList.get(i)));
            i++;
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
    @MethodSource("provideRandomStatusMultiList")
    @ParameterizedTest
    public void newShippingAndExistedShippedRecordInTempMovementListAndSuccessOutputFile(List<MoveOut> moveOuts, List<MoveOut> secondMoveOuts){

        // String firstStatus = moveOuts.get(0).getOrder().getStatus();
        // String secondStatus = secondMoveOuts.get(0).getOrder().getStatus();

        orderService.process(moveOuts);
        orderService.process(secondMoveOuts);

        if(moveOuts.isEmpty() && secondMoveOuts.isEmpty()) {
            assertTrue(true);
            return;
        }

        String firstStatus = "";
        String secondStatus = "";
        if(!moveOuts.isEmpty()) firstStatus = moveOuts.get(0).getOrder().getStatus();
        if(!secondMoveOuts.isEmpty()) secondStatus = secondMoveOuts.get(0).getOrder().getStatus();
        if(firstStatus.equals(secondStatus)) {
            String status = moveOuts.get(0).getOrder().getStatus();
            List<Order> list = classificedOrders.get(status);
            list.addAll(secondClassificedOrders.get(status));
            List<Order> repositoryList = getRelativeStatusOrderListBySampleOrder(orderRepository, moveOuts.get(0).getOrder());
            assertTrue(repositoryList.containsAll(list));

            ArrayList<Order> cloneList = new ArrayList<Order>(repositoryList);
            removeSame(cloneList, list);
            assertTrue(cloneList.isEmpty());

        } else {
            if(!firstStatus.isEmpty()) {
                String status = moveOuts.get(0).getOrder().getStatus();
                List<Order> list = classificedOrders.get(status);
                List<Order> repositoryList = getRelativeStatusOrderListBySampleOrder(orderRepository, moveOuts.get(0).getOrder());
                if(!moveOuts.get(0).getOrder().getStatus().equals(OrderService.STATUS_CANCEL) 
                    && !moveOuts.get(0).getOrder().getStatus().equals(OrderService.STATUS_TO_SHIP)){
                    assertTrue(repositoryList.containsAll(list));
                }

                if(repositoryList != null && !repositoryList.isEmpty()) {
                    ArrayList<Order> cloneList = new ArrayList<Order>(repositoryList);
                    removeSame(cloneList, list);
                    assertTrue(cloneList.isEmpty());
                }
            }
            if(!secondStatus.isEmpty()) {
                String status = secondMoveOuts.get(0).getOrder().getStatus();
                List<Order> list = secondClassificedOrders.get(status);
                List<Order> repositoryList = getRelativeStatusOrderListBySampleOrder(orderRepository, secondMoveOuts.get(0).getOrder());
                if(!secondMoveOuts.get(0).getOrder().getStatus().equals(OrderService.STATUS_CANCEL) 
                    && !secondMoveOuts.get(0).getOrder().getStatus().equals(OrderService.STATUS_TO_SHIP)){
                    assertTrue(repositoryList.containsAll(list));
                }

                if(repositoryList != null && !repositoryList.isEmpty()) {
                    ArrayList<Order> cloneList = new ArrayList<Order>(repositoryList);
                    removeSame(cloneList, list);
                    assertTrue(cloneList.isEmpty());
                }
            }

        }
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

    private void removeSame(List<Order> list, List<Order> toRemoveList) {
        Comparator<Order> comparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                if(!o1.getId().equals(o2.getId())) {
                    return o1.getId().compareTo(o2.getId());
                }
                return 0;
            }
            
        };

        toRemoveList.sort(comparator);
        list.sort(comparator);

        list.removeIf(new Predicate<Order>() {

            @Override
            public boolean test(Order t) {
                if(Lookup.lookupOrder(toRemoveList, t.getId()) != null) return true;
                return false;
            }
            
        });
    }

    private List<Order> classifyOrders(Map<String, List<Order>> map, List<Order> orders) {

        map.put(OrderService.STATUS_CANCEL, new ArrayList<Order>());
        map.put(OrderService.STATUS_COMPLETE, new ArrayList<Order>());
        map.put(OrderService.STATUS_DELIVERED, new ArrayList<Order>());
        map.put(OrderService.STATUS_RECEIVED, new ArrayList<Order>());
        map.put(OrderService.STATUS_SHIPPING, new ArrayList<Order>());
        map.put(OrderService.STATUS_TO_SHIP, new ArrayList<Order>());
        map.put(OrderService.STATUS_UNPAID, new ArrayList<Order>());

        for(Order order : orders) {
            if(order.getStatus().equals(OrderService.STATUS_COMPLETE) && order.isRequestApproved()){
                return repository.getReturnAfterCompletedOrders();
                continue;
            }
        }
        if(order.getStatus().equals(OrderService.STATUS_DELIVERED) && order.isRequestApproved()){
            return repository.getReturnAfterCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_RECEIVED) && order.isRequestApproved()){
            return repository.getReturnAfterCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_DELIVERED)) {
            return repository.getCompletedOrders();
        }
        if(order.getStatus().contains(OrderService.STATUS_RECEIVED)) {
            return repository.getCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_COMPLETE)){
            return repository.getCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_SHIPPING)){
            return repository.getShippingOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null){
            return repository.getReturnAfterShippingOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_TO_SHIP)){
            return new ArrayList<>();
        }
    }

    private List<Order> getRelativeStatusOrderListBySampleOrder(OrderRepository repository, Order order) {
        if(order.getStatus().equals(OrderService.STATUS_COMPLETE) && order.isRequestApproved()){
            return repository.getReturnAfterCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_DELIVERED) && order.isRequestApproved()){
            return repository.getReturnAfterCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_RECEIVED) && order.isRequestApproved()){
            return repository.getReturnAfterCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_DELIVERED)) {
            return repository.getCompletedOrders();
        }
        if(order.getStatus().contains(OrderService.STATUS_RECEIVED)) {
            return repository.getCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_COMPLETE)){
            return repository.getCompletedOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_SHIPPING)){
            return repository.getShippingOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_CANCEL) && order.getShipOutDate() != null){
            return repository.getReturnAfterShippingOrders();
        }
        if(order.getStatus().equals(OrderService.STATUS_TO_SHIP)){
            return new ArrayList<>();
        }
        return null;
    }
}
