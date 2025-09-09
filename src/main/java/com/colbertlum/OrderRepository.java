package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.Imputer.MeasImputer;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.Imputer.Utils.OrderFactory;
import com.colbertlum.constants.OrderInternalStatus;
import com.colbertlum.constants.Columns.RepositorySheetName;
import com.colbertlum.contentHandler.RepositoryItemMovementStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryOrderContentHandler;
import com.colbertlum.contentHandler.RepositoryShopeeOrderStatusContentHandler;
import com.colbertlum.contentHandler.RepositoryTikTokOrderStatusContentHandler;
import com.colbertlum.contentWriter.ContentHeaderMapperInterface;
import com.colbertlum.contentWriter.ContentWriter;
import com.colbertlum.contentWriter.RepositoryItemMovementMapper;
import com.colbertlum.contentWriter.RepositoryOrderMapper;
import com.colbertlum.contentWriter.RepositoryReturnMovementMapper;
import com.colbertlum.contentWriter.RepositoryShopeeOrderMapper;
import com.colbertlum.contentWriter.RepositoryTikTokOrderMapper;
import com.colbertlum.contentHandler.RepositoryReturnMovementContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;
import com.colbertlum.entity.ShopeeOrder;
import com.colbertlum.entity.TikTokOrder;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;
import javafx.scene.control.ButtonType;

public class OrderRepository {

    private static Logger logger = LogManager.getLogger(OrderRepository.class);

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private List<Order> orders;
    private List<Order> shippingOrders;
    private List<Order> completedOrders;
    private List<Order> settledOrders;
    private List<Order> returnAfterShippingOrders;
    private List<Order> returnAfterCompletedOrders;
    private List<MoveOut> moveOutList;
    private List<ReturnOrder> returnOrders;
    private List<ReturnMoveOut> returnMoveOuts;

    public List<Order> getOrders(){
        return orders;
    }

    public List<ReturnMoveOut> getReturnMoveOuts() {
        return returnMoveOuts;
    }

    public List<ReturnOrder> getReturnOrders() {
        return returnOrders;
    }

    public List<Order> getReturnAfterShippingOrders() {
        return returnAfterShippingOrders;
    }

    public List<Order> getReturnAfterCompletedOrders() {
        return returnAfterCompletedOrders;
    }

    public List<Order> getShippingOrders() {
        return shippingOrders;
    }
    public List<Order> getCompletedOrders() {
        return completedOrders;
    }
    public List<Order> getSettledOrders(){
        return settledOrders;
    }

    public List<Order> getCompletedOrdersByLocalDate(LocalDate localDate) {
        if(localDate == null) return null;

        completedOrders.sort((o1, o2) -> {
            return o2.getCompletedDate().compareTo(o1.getCompletedDate());
        });

        List<Order> list = new ArrayList<Order>();
        boolean inDateRange = false;
        for(Order order : completedOrders) {
            if(order.getCompletedDate().equals(localDate)) {
                list.add(order);
                inDateRange = true;
            } else if(inDateRange == true) {
                return list;
            }
        }
        if(list.isEmpty()) return null;
        return list;
    }

    public List<Order> getReturnAfterCompletedOrdersByLocalDate(LocalDate localDate) {
        if(localDate == null) return null;

        returnAfterCompletedOrders.sort((o1, o2) -> {
            return o2.getCompletedDate().compareTo(o1.getCompletedDate());
        });

        List<Order> list = new ArrayList<Order>();
        boolean inDateRange = false;
        for(Order order : returnAfterCompletedOrders) {
            if(order.getCompletedDate().equals(localDate)) {
                list.add(order);
                inDateRange = true;
            } else if(inDateRange == true) {
                return list;
            }
        }
        if(list.isEmpty()) return null;
        return list;
    }

    private void loadRepository(){

        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));
        
        
        orders = new ArrayList<Order>();
        shippingOrders = new ArrayList<Order>();
        completedOrders = new ArrayList<Order>();
        returnAfterShippingOrders = new ArrayList<Order>();
        returnAfterCompletedOrders = new ArrayList<Order>();

        moveOutList = new ArrayList<MoveOut>();
        returnMoveOuts = new ArrayList<ReturnMoveOut>();
        returnOrders = new ArrayList<ReturnOrder>();

        try {
            if(!file.exists()) {
                createRepositoryFile();
            }

            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            // MeasContentHandler contentHandler = new MeasContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(),
            //     measList);
            // xmlReader.setContentHandler(contentHandler);
            Iterator<InputStream> inputIterator = xssfReader.getSheetsData();

            List<ShopeeOrder> shopeeOrders = new ArrayList<ShopeeOrder>();
            List<TikTokOrder> tikTokOrders = new ArrayList<TikTokOrder>();
            if(inputIterator instanceof XSSFReader.SheetIterator){

                XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) inputIterator;
                while (sheetIterator.hasNext()) {
                    InputStream inputStream = sheetIterator.next();
                    String sheetName = sheetIterator.getSheetName();
                    XMLReader xmlReader = XMLHelper.newXMLReader();
                    if(sheetName.equals(RepositorySheetName.ORDERS)){

                        RepositoryOrderContentHandler contentHandler = new RepositoryOrderContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), orders);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals(RepositorySheetName.MOVEMENTS)) {

                        RepositoryItemMovementStatusContentHandler contentHandler = new RepositoryItemMovementStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), moveOutList);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals(RepositorySheetName.RETURN_MOVEMENTS)){

                        RepositoryReturnMovementContentHandler contentHandler = new RepositoryReturnMovementContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), returnMoveOuts);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));
                                                
                    } else if(sheetName.equals(RepositorySheetName.SHOPEE_ORDERS)) {

                        RepositoryShopeeOrderStatusContentHandler contentHandler = new RepositoryShopeeOrderStatusContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), shopeeOrders);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    } else if(sheetName.equals(RepositorySheetName.TIKTOK_ORDERS)) {

                        RepositoryTikTokOrderStatusContentHandler contentHandler = new RepositoryTikTokOrderStatusContentHandler(xssfReader.getSharedStringsTable(),xssfReader.getStylesTable(), tikTokOrders);
                        xmlReader.setContentHandler(contentHandler);
                        xmlReader.parse(new InputSource(inputStream));

                    }
                    inputStream.close();
                }
            }

            OrderFactory.bindToSingleInstanceOrder(orders, shopeeOrders);
            OrderFactory.bindToSingleInstanceOrder(orders, tikTokOrders);

        } catch (IOException | OpenXML4JException e) {
            Alert warningStage = new Alert(AlertType.ERROR, "you must select meas file");
            warningStage.showAndWait();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MeasImputer measImputer = new MeasImputer();
        ArrayList<Meas> measList = measImputer.getMeasList();
        measList.sort((o1, o2) -> o1.getRelativeId().compareTo(o2.getRelativeId()));
        for(MoveOut moveOut : moveOutList) {
            Meas meas = measImputer.getMeas(moveOut.getSku(), measList);
            if(meas == null) continue;
            moveOut.setProductId(meas.getId());
        }
        for(ReturnMoveOut moveOut : returnMoveOuts) {
            Meas meas = measImputer.getMeas(moveOut.getSku(), measList);
            if(meas == null) continue;
            moveOut.setProductId(meas.getId());
        }

        HashMap<String, List<SoftReference<MoveOut>>> orderIdMap = new HashMap<String, List<SoftReference<MoveOut>>>();
        for(MoveOut moveOut : moveOutList){
            if(orderIdMap.containsKey(moveOut.getOrderId())){
                orderIdMap.get(moveOut.getOrderId()).add(new SoftReference<MoveOut>(moveOut));
            } else {
                List<SoftReference<MoveOut>> list = new ArrayList<SoftReference<MoveOut>>();
                list.add(new SoftReference<MoveOut>(moveOut));
                orderIdMap.put(moveOut.getOrderId(), list);
            }
        }
        for(Order order : orders){
            order.setMoveOutList(orderIdMap.get(order.getId()));
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                softMoveOut.get().setOrder(order);
            }
        }

        // orders.sort((o1, o2) -> {
        //     return o1.getId().compareTo(o2.getId());
        // });
        
        // for(MoveOut moveOut : moveOutList){
        //     SoftReference<MoveOut> softMoveOut = new SoftReference<MoveOut>(moveOut);
        //     // System.out.println("searching " + moveOut.getOrderId());
        //     Order lookupOrder = Lookup.lookupOrder(orders, moveOut.getOrderId());
        //     if(lookupOrder != null && lookupOrder.getMoveOutList() != null){
        //         if(lookupOrder.getMoveOutList() == null) lookupOrder.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        //         lookupOrder.getMoveOutList().add(softMoveOut);
        //     } else {
        //         // System.out.println("not found");
        //     }
        // }
        for(Order order : orders){
            if(order.getInternalStatus().equals(OrderInternalStatus.SHIPPING)){
                shippingOrders.add(order);
            } else if(order.getInternalStatus().equals(OrderInternalStatus.AFTER_SALES_RETURN)){
                returnAfterCompletedOrders.add(order);
            } else if(order.getInternalStatus().equals(OrderInternalStatus.RETURNING)){
                returnAfterShippingOrders.add(order);
            } else if(order.getInternalStatus().equals(OrderInternalStatus.COMPLETED)){
                completedOrders.add(order);
            }
        }

        HashMap<String, List<SoftReference<ReturnMoveOut>>> returnOrderIdMap = new HashMap<String, List<SoftReference<ReturnMoveOut>>>();
        for(ReturnMoveOut returnMoveOut : returnMoveOuts){
            if(returnOrderIdMap.containsKey(returnMoveOut.getOrderId())){
                returnOrderIdMap.get(returnMoveOut.getOrderId()).add(new SoftReference<ReturnMoveOut>(returnMoveOut));
            } else {
                List<SoftReference<ReturnMoveOut>> list = new ArrayList<SoftReference<ReturnMoveOut>>();
                list.add(new SoftReference<ReturnMoveOut>(returnMoveOut));
                returnOrderIdMap.put(returnMoveOut.getOrderId(), list);
            }
        }
        for(Order order : returnAfterCompletedOrders){
            ReturnOrder returnOrder = new ReturnOrder(order);
            returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            returnOrder.setReturnMoveOutList(returnOrderIdMap.get(returnOrder.getId()));
            returnOrders.add(returnOrder);
        }
        for(Order order : returnAfterShippingOrders){
            ReturnOrder returnOrder = new ReturnOrder(order);
            returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            returnOrder.setReturnMoveOutList(returnOrderIdMap.get(returnOrder.getId()));
            returnOrders.add(returnOrder);
        }

        logger.trace("Repository Shipping Orders size : " + shippingOrders.size());
        logger.trace("Repository Completed Orders size : " + completedOrders.size());
        logger.trace("Repository returnAfterCompleted Orders size : " + returnAfterCompletedOrders.size());
        logger.trace("Repository retrunAfterShipping Orders size : " + returnAfterShippingOrders.size());
    }

    public void createRepositoryFile() throws IOException{
        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));
        XSSFWorkbook workbook;
        if(!file.exists()) file.createNewFile();
        
        workbook = new XSSFWorkbook();
        XSSFSheet orderSheet = workbook.createSheet(RepositorySheetName.ORDERS);
        XSSFSheet movementSheet = workbook.createSheet(RepositorySheetName.MOVEMENTS);
        XSSFSheet returnMovementSheet = workbook.createSheet(RepositorySheetName.RETURN_MOVEMENTS);
        XSSFSheet TiktokOrderSheet = workbook.createSheet(RepositorySheetName.TIKTOK_ORDERS);
        XSSFSheet ShopeeOrderSheet = workbook.createSheet(RepositorySheetName.SHOPEE_ORDERS);

        createOrderSheet(orderSheet);
        createMovementSheet(movementSheet);
        createReturnMovementSheet(returnMovementSheet);
        createTikTokOrderSheet(TiktokOrderSheet);
        createShopeeOrderSheet(ShopeeOrderSheet);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();
    }

    private void createTikTokOrderSheet(XSSFSheet sheet) {
        ContentHeaderMapperInterface<TikTokOrder> mapper = new RepositoryTikTokOrderMapper();
        ContentWriter<TikTokOrder> writer = new ContentWriter<>(sheet, mapper, new ArrayList<>());
        
        writer.createHeader();
    }

    private void createShopeeOrderSheet(XSSFSheet sheet) {
        ContentHeaderMapperInterface<ShopeeOrder> mapper = new RepositoryShopeeOrderMapper();
        ContentWriter<ShopeeOrder> writer = new ContentWriter<>(sheet, mapper, new ArrayList<>());
        
        writer.createHeader();
    }

    private void createReturnMovementSheet(XSSFSheet returnMovementSheet) {

        ContentHeaderMapperInterface<ReturnMoveOut> mapper = new RepositoryReturnMovementMapper();
        ContentWriter<ReturnMoveOut> writer = new ContentWriter<>(returnMovementSheet, mapper, new ArrayList<>());
        
        writer.createHeader();
    }

    private void createMovementSheet(XSSFSheet movementSheet) {

        ContentHeaderMapperInterface<MoveOut> mapper = new RepositoryItemMovementMapper();
        ContentWriter<MoveOut> writer = new ContentWriter<>(movementSheet, mapper, new ArrayList<>());
        
        writer.createHeader();
    }

    private void createOrderSheet(XSSFSheet orderSheet) {

        ContentHeaderMapperInterface<Order> mapper = new RepositoryOrderMapper();
        ContentWriter<Order> writer = new ContentWriter<>(orderSheet, mapper, new ArrayList<>());
        
        writer.createHeader();
    }

    public void saveToRepository(List<Order> orders) throws IOException{

        String dateString = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_REMAIN_AFTER_DATE);
        if(dateString != null && !dateString.isEmpty()){
            LocalDate remainAfterDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_PATTERN));
            orders.removeIf(order -> order.getShipOutDate().isBefore(remainAfterDate));
            if(orders.isEmpty()) return;
        }

        // ask user comfirm process will update to repository or skip following part.
        if(!Window.getWindows().isEmpty()) {
            try {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setContentText("Are you sure this process is valid and save which order to repository");
                Optional<ButtonType> result = alert.showAndWait();
                if(!result.isPresent() || result.get() != ButtonType.OK){
                    return;
                }
            } catch(RuntimeException e){
                logger.error(e.getMessage());
            }
        }

        File file = new File(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.ORDER_REPOSITORY_PATH));

        if(!file.exists()) createRepositoryFile();

        FileInputStream fileInputStream = new FileInputStream(file);
        // XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(fileInputStream);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet orderSheet = workbook.getSheetAt(0);
        XSSFSheet movementSheet = workbook.getSheetAt(1);
        XSSFSheet returnMovementSheet = workbook.getSheetAt(2);
        XSSFSheet tikTokOrderSheet = workbook.getSheetAt(3);
        XSSFSheet ShopeeOrderSheet = workbook.getSheetAt(4);

        writeOrderSheetCell(orderSheet, orders);
        writeMovementSheetCell(movementSheet, moveOutList);
        writeReturnMovementSheetCell(returnMovementSheet, returnMoveOuts);

        List<ShopeeOrder> shopeeOrders = new ArrayList<ShopeeOrder>();
        List<TikTokOrder> tiktokOrders = new ArrayList<TikTokOrder>();
        for(Order order : orders) {
            if(order instanceof ShopeeOrder) {
                ShopeeOrder shopeeOrder = (ShopeeOrder) order;
                shopeeOrders.add(shopeeOrder);
            } else if(order instanceof TikTokOrder) {
                TikTokOrder tikTokOrder = (TikTokOrder) order;
                tiktokOrders.add(tikTokOrder);
            }
        }
        writeTikTokOrderPlatformSheetCell(tikTokOrderSheet, tiktokOrders);
        writeShopeeOrderPlatformSheetCell(ShopeeOrderSheet, shopeeOrders);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    private void writeReturnMovementSheetCell(XSSFSheet returnMovementSheet, List<ReturnMoveOut> returnMoveOuts) {

        ContentHeaderMapperInterface<ReturnMoveOut> mapper = new RepositoryReturnMovementMapper();
        ContentWriter<ReturnMoveOut> writer = new ContentWriter<>(returnMovementSheet, mapper, returnMoveOuts);

        writer.writeAll();

        // clean
        // int lastRowNum = returnMovementSheet.getLastRowNum();
        // for (int i = lastRowNum; i >= 1; i--) {
        //     Row row = returnMovementSheet.getRow(i);
        //     if (row != null) {
        //         returnMovementSheet.removeRow(row);
        //     }
        // }

        // int index = 1;
        // if(returnMoveOuts == null) return;
        // for(ReturnMoveOut returnMoveOut : returnMoveOuts){
        //     Row row = returnMovementSheet.createRow(index);
        //     index++;

        //     Cell orderIdCell = row.getCell(0);
        //     if(orderIdCell == null) orderIdCell = row.createCell(0);
        //     orderIdCell.setCellValue(returnMoveOut.getOrderId());

        //     Cell skuCell = row.getCell(1);
        //     if(skuCell == null) skuCell = row.createCell(1);
        //     skuCell.setCellValue(returnMoveOut.getSku());

        //     Cell productNameCell = row.getCell(2);
        //     if(productNameCell == null) productNameCell = row.createCell(2);
        //     productNameCell.setCellValue(returnMoveOut.getProductName());

        //     Cell variationNameCell = row.getCell(3);
        //     if(variationNameCell == null) variationNameCell = row.createCell(3);
        //     variationNameCell.setCellValue(returnMoveOut.getVariationName());

        //     Cell quantityCell = row.getCell(4);
        //     if(quantityCell == null) quantityCell = row.createCell(4);
        //     quantityCell.setCellValue(returnMoveOut.getQuantity());
            
        //     Cell priceCell = row.getCell(5);
        //     if(priceCell == null) priceCell = row.createCell(5);
        //     priceCell.setCellValue(returnMoveOut.getPrice());
            
        //     Cell returnStatusCell = row.getCell(6);
        //     if(returnStatusCell == null) returnStatusCell = row.createCell(6);
        //     returnStatusCell.setCellValue(returnMoveOut.getReturnStatus());

        //     Cell statusQuantityCell = row.getCell(7);
        //     if(statusQuantityCell == null) statusQuantityCell = row.createCell(7);
        //     statusQuantityCell.setCellValue(returnMoveOut.getStatusQuantity());
        // }

        // while(returnMovementSheet.getRow(index) != null) {
        //     Row row = returnMovementSheet.getRow(index);
        //     returnMovementSheet.removeRow(row);
        // }
    }

    private void writeMovementSheetCell(XSSFSheet movementSheet, List<MoveOut> moveOuts) {

        ContentHeaderMapperInterface<MoveOut> mapper = new RepositoryItemMovementMapper();
        ContentWriter<MoveOut> writer = new ContentWriter<>(movementSheet, mapper, moveOuts);

        writer.writeAll();

        // clean
        // int lastRowNum = movementSheet.getLastRowNum();
        // for (int i = lastRowNum; i >= 1; i--) {
        //     Row row = movementSheet.getRow(i);
        //     if (row != null) {
        //         movementSheet.removeRow(row);
        //     }
        // }

        // int index = 1;
        // if(moveOuts == null) return;
        // for(MoveOut moveOut : moveOuts){
        //     Row row = movementSheet.createRow(index);
        //     index++;

        //     Cell orderIdCell = row.getCell(0);
        //     if(orderIdCell == null) orderIdCell = row.createCell(0);
        //     orderIdCell.setCellValue(moveOut.getOrderId());

        //     Cell skuCell = row.getCell(1);
        //     if(skuCell == null) skuCell = row.createCell(1);
        //     skuCell.setCellValue(moveOut.getSku());

        //     Cell productNameCell = row.getCell(2);
        //     if(productNameCell == null) productNameCell = row.createCell(2);
        //     productNameCell.setCellValue(moveOut.getProductName());

        //     Cell variationNameCell = row.getCell(3);
        //     if(variationNameCell == null) variationNameCell = row.createCell(3);
        //     variationNameCell.setCellValue(moveOut.getVariationName());

        //     Cell quantityCell = row.getCell(4);
        //     if(quantityCell == null) quantityCell = row.createCell(4);
        //     quantityCell.setCellValue(moveOut.getQuantity());
            
        //     Cell priceCell = row.getCell(5);
        //     if(priceCell == null) priceCell = row.createCell(5);
        //     priceCell.setCellValue(moveOut.getPrice());
        // }

        // while(movementSheet.getRow(index) != null) {
        //     Row row = movementSheet.getRow(index);
        //     movementSheet.removeRow(row);
        // }
    }

    private void writeTikTokOrderPlatformSheetCell(XSSFSheet orderSheet, List<TikTokOrder> orders){
        ContentHeaderMapperInterface<TikTokOrder> mapper = new RepositoryTikTokOrderMapper();
        ContentWriter<TikTokOrder> OrderWriter = new ContentWriter<>(orderSheet, mapper, orders);

        OrderWriter.writeAll();
    }

    private void writeShopeeOrderPlatformSheetCell(XSSFSheet orderSheet, List<ShopeeOrder> orders){
        ContentHeaderMapperInterface<ShopeeOrder> mapper = new RepositoryShopeeOrderMapper();
        ContentWriter<ShopeeOrder> OrderWriter = new ContentWriter<>(orderSheet, mapper, orders);

        OrderWriter.writeAll();
    }

    private void writeOrderSheetCell(XSSFSheet orderSheet, List<Order> orders) {

        ContentHeaderMapperInterface<Order> mapper = new RepositoryOrderMapper();
        ContentWriter<Order> OrderWriter = new ContentWriter<>(orderSheet, mapper, orders);

        OrderWriter.writeAll();

        // clean
        // int lastRowNum = orderSheet.getLastRowNum();
        // for (int i = lastRowNum; i >= 1; i--) {
        //     Row row = orderSheet.getRow(i);
        //     if (row != null) {
        //         orderSheet.removeRow(row);
        //     }
        // }

        // int index = 1;
        // if(orders == null) return;
        // for(Order order : orders){
        //     XSSFRow row = orderSheet.createRow(index);
        //     index++;

        //     Cell orderIdCell = row.getCell(0);
        //     if(orderIdCell == null) orderIdCell = row.createCell(0);
        //     orderIdCell.setCellValue(order.getId());

        //     Cell trackingNumberCell = row.getCell(1);
        //     if(trackingNumberCell == null) trackingNumberCell = row.createCell(1);
        //     trackingNumberCell.setCellValue(order.getTrackingNumber());

        //     Cell creationDateCell = row.getCell(2);
        //     if(creationDateCell == null) creationDateCell = row.createCell(2);
        //     creationDateCell.setCellValue(order.getCreationDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

        //     Cell shipOutDateCell = row.getCell(3);
        //     if(shipOutDateCell == null) shipOutDateCell = row.createCell(3);
        //     shipOutDateCell.setCellValue(order.getShipOutDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

        //     Cell completedDateCell = row.getCell(4);
        //     if(completedDateCell == null) completedDateCell = row.createCell(4);
        //     if(order.getCompletedDate() != null)
        //         completedDateCell.setCellValue(order.getCompletedDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            
        //     Cell RequestReturnRefundCell = row.getCell(5);
        //     if(RequestReturnRefundCell == null) RequestReturnRefundCell = row.createCell(5);
        //     RequestReturnRefundCell.setCellValue(order.isRequestApproved() ? "Request Approved" : "");

        //     Cell statusCell = row.getCell(6);
        //     if(statusCell == null) statusCell = row.createCell(6);
        //     statusCell.setCellValue(order.getStatus());

        //     Cell orderTotalAmountCell = row.getCell(7);
        //     if(orderTotalAmountCell == null) orderTotalAmountCell = row.createCell(7);
        //     orderTotalAmountCell.setCellValue(order.getOrderTotalAmount());

        //     Cell managementFeeCell = row.getCell(8);
        //     if(managementFeeCell == null) managementFeeCell = row.createCell(8);
        //     managementFeeCell.setCellValue(order.getManagementFee());

        //     Cell transactionFeeCell = row.getCell(9);
        //     if(transactionFeeCell == null) transactionFeeCell = row.createCell(9);
        //     transactionFeeCell.setCellValue(order.getTransactionFee());

        //     Cell serviceFeeCell = row.getCell(10);
        //     if(serviceFeeCell == null) serviceFeeCell = row.createCell(10);
        //     serviceFeeCell.setCellValue(order.getServiceFee());

        //     Cell commissionFeeCell = row.getCell(11);
        //     if(commissionFeeCell == null) commissionFeeCell = row.createCell(11);
        //     commissionFeeCell.setCellValue(order.getCommissionFee());

        //     Cell shopeeVoucherCell = row.getCell(12);
        //     if(shopeeVoucherCell == null) shopeeVoucherCell = row.createCell(12);
        //     shopeeVoucherCell.setCellValue(order.getServiceFee());

        //     Cell shippingFeeCell = row.getCell(13);
        //     if(shippingFeeCell == null) shippingFeeCell = row.createCell(13);
        //     shippingFeeCell.setCellValue(order.getShippingFee());

        //     Cell shippingRebateCell = row.getCell(14);
        //     if(shippingRebateCell == null) shippingRebateCell = row.createCell(14);
        //     shippingRebateCell.setCellValue(order.getServiceFee());
        // }

        // while(orderSheet.getRow(index) != null) {
        //     Row row = orderSheet.getRow(index);
        //     orderSheet.removeRow(row);
        // }
    }

    public OrderRepository(boolean loadingRepository){
        if(loadingRepository) {
            loadRepository();
        }
    }

    public void removeCompletedOrders(List<Order> removeOrders) {

        for(Order order : removeOrders){
            Order lookupOrder = Lookup.lookupOrder(completedOrders, order.getId());
            if(lookupOrder != null) {
                completedOrders.remove(lookupOrder);
                orders.remove(lookupOrder);

                List<SoftReference<MoveOut>> softMoveOuts = lookupOrder.getMoveOutList();
                ArrayList<MoveOut> removeMoveOuts = new ArrayList<MoveOut>();
                for(SoftReference<MoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                moveOutList.removeAll(removeMoveOuts);
            }
        }
    }

    public void addReturnAfterShippingOrder(List<Order> newReturnAfterShippingOrders) {
        orders.addAll(newReturnAfterShippingOrders);
        returnAfterShippingOrders.addAll(newReturnAfterShippingOrders);

        ArrayList<ReturnMoveOut> returningMoveOuts = new ArrayList<ReturnMoveOut>();
        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        for(Order order : newReturnAfterShippingOrders){
            ReturnOrder returnOrder = new ReturnOrder(order);
            returnOrder.setReturnType(ReturnOrder.FAILED_DELIVERY_TYPE);
            returnOrders.add(returnOrder);
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                MoveOut moveOut = softMoveOut.get();
                ReturnMoveOut returnMoveOut = new ReturnMoveOut(moveOut);
                returningMoveOuts.add(returnMoveOut);
                returnMoveOut.setReturnOrder(returnOrder);
                moveOuts.add(moveOut);
            }
        }
        returnMoveOuts.addAll(returningMoveOuts);
        this.moveOutList.addAll(moveOuts);
    }

    public void addCompletedOrders(List<Order> newCompletedOrders){
        orders.addAll(newCompletedOrders);
        completedOrders.addAll(newCompletedOrders);
        for(Order order : newCompletedOrders){
            List<SoftReference<MoveOut>> moveOuts = order.getMoveOutList();
            for(SoftReference<MoveOut> softMoveOut : moveOuts){
                MoveOut moveOut = softMoveOut.get();
                moveOutList.add(moveOut);
            }
        }
    }

    public void addSettledOrders(List<Order> newSettledOrders) {
        orders.addAll(newSettledOrders);
        settledOrders.addAll(newSettledOrders);
        for(Order order : newSettledOrders){
            List<SoftReference<MoveOut>> moveOuts = order.getMoveOutList();
            for(SoftReference<MoveOut> softMoveOut : moveOuts){
                MoveOut moveOut = softMoveOut.get();
                moveOutList.add(moveOut);
            }
        }
    }

    public void addReturnAfterCompletedOrder(List<Order> newReturnAfterCompletedOrder) {
        orders.addAll(newReturnAfterCompletedOrder);
        returnAfterCompletedOrders.addAll(newReturnAfterCompletedOrder);

        ArrayList<ReturnMoveOut> returningMoveOuts = new ArrayList<ReturnMoveOut>();
        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        for(Order order : newReturnAfterCompletedOrder){
            ReturnOrder returnOrder = new ReturnOrder(order);
            returnOrder.setReturnType(ReturnOrder.REQUEST_RETURN_REFUND);
            
            for(SoftReference<MoveOut> softMoveOut : order.getMoveOutList()){
                MoveOut moveOut = softMoveOut.get();
                ReturnMoveOut returnMoveOut = new ReturnMoveOut(moveOut);
                returningMoveOuts.add(returnMoveOut);
                returnMoveOut.setReturnOrder(returnOrder);
                moveOuts.add(moveOut);
            }
        }
        returnMoveOuts.addAll(returningMoveOuts);
        this.moveOutList.addAll(moveOuts);
    }

    public void removeReturnAfterCompletedOrders(List<Order> removeOrders){
        for(Order order : removeOrders){
            Order lookupOrder = Lookup.lookupOrder(returnAfterCompletedOrders, order.getId());
            if(lookupOrder != null) {
                returnAfterCompletedOrders.remove(lookupOrder);
                orders.remove(lookupOrder);
                
                List<SoftReference<MoveOut>> softMoveOuts = lookupOrder.getMoveOutList();
                ArrayList<MoveOut> removeMoveOuts = new ArrayList<MoveOut>();
                for(SoftReference<MoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                moveOutList.removeAll(removeMoveOuts);
            }

            ReturnOrder lookupReturnOrder = Lookup.lookupReturnOrder(returnOrders, order.getId());
            if(lookupReturnOrder != null) {
                returnOrders.remove(lookupReturnOrder);
                
                List<SoftReference<ReturnMoveOut>> softMoveOuts = lookupReturnOrder.getReturnMoveOutList();
                ArrayList<ReturnMoveOut> removeMoveOuts = new ArrayList<ReturnMoveOut>();
                for(SoftReference<ReturnMoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                returnMoveOuts.removeAll(removeMoveOuts);
            }

        }
    }

    public void removeReturnAfterShippingOrders(List<Order> removeOrders) {
        for(Order order : removeOrders){
            Order lookupOrder = Lookup.lookupOrder(returnAfterShippingOrders, order.getId());
            if(lookupOrder != null) {
                returnAfterShippingOrders.remove(lookupOrder);
                orders.remove(lookupOrder);
                
                List<SoftReference<MoveOut>> softMoveOuts = lookupOrder.getMoveOutList();
                ArrayList<MoveOut> removeMoveOuts = new ArrayList<MoveOut>();
                for(SoftReference<MoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                moveOutList.removeAll(removeMoveOuts);
            }

            ReturnOrder lookupReturnOrder = Lookup.lookupReturnOrder(returnOrders, order.getId());
            if(lookupReturnOrder != null) {
                returnOrders.remove(lookupReturnOrder);
                
                List<SoftReference<ReturnMoveOut>> softMoveOuts = lookupReturnOrder.getReturnMoveOutList();
                ArrayList<ReturnMoveOut> removeMoveOuts = new ArrayList<ReturnMoveOut>();
                for(SoftReference<ReturnMoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                returnMoveOuts.removeAll(removeMoveOuts);
            }

        }
    }

    public void removeShippingOrders(List<Order> removeOrders) {

        for(Order order : removeOrders){
            Order lookupOrder = Lookup.lookupOrder(shippingOrders, order.getId());
            if(lookupOrder != null) {
                shippingOrders.remove(lookupOrder);
                orders.remove(lookupOrder);
                
                List<SoftReference<MoveOut>> softMoveOuts = lookupOrder.getMoveOutList();
                ArrayList<MoveOut> removeMoveOuts = new ArrayList<MoveOut>();
                for(SoftReference<MoveOut> softMoveOut : softMoveOuts){
                    removeMoveOuts.add(softMoveOut.get());
                }
                moveOutList.removeAll(removeMoveOuts);
            }
        }
    }

    public void addShippingOrders(List<Order> newShippingOrders){
        orders.addAll(newShippingOrders);
        shippingOrders.addAll(newShippingOrders);
        for(Order order : newShippingOrders){
            List<SoftReference<MoveOut>> moveOuts = order.getMoveOutList();
            for(SoftReference<MoveOut> softMoveOut : moveOuts){
                MoveOut moveOut = softMoveOut.get();
                moveOutList.add(moveOut);
            }
        }
    }

    public void submitTransaction() {
        try {
            saveToRepository(orders);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addOrders(List<Order> newOrders) {
        orders.addAll(newOrders);
    }

    public void addReturnMoveOutsAsSortReference(List<SoftReference<ReturnMoveOut>> returnMoveOutList) {
        ArrayList<ReturnMoveOut> toAdd = new ArrayList<ReturnMoveOut>();
        for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnMoveOutList) {
            ReturnMoveOut returnMoveOut = softReturnMoveOut.get();
            toAdd.add(returnMoveOut);
        }
        returnMoveOuts.addAll(toAdd);
    }

    public void removeReturnMoveOutsAsSortReference(List<SoftReference<ReturnMoveOut>> returnMoveOutList) {
        List<ReturnMoveOut> toRemove = new ArrayList<ReturnMoveOut>();
        for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnMoveOutList) {
            ReturnMoveOut returnMoveOut = softReturnMoveOut.get();
            toRemove.add(returnMoveOut);
        }
        returnMoveOuts.removeAll(toRemove);
    }
}