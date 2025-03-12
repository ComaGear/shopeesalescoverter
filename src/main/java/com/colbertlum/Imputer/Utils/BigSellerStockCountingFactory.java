package com.colbertlum.Imputer.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.contentHandler.BigSellerStockCountingContentHandler;
import com.colbertlum.entity.BigSellerStockCounting;
import com.colbertlum.entity.ListingStock;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;

public class BigSellerStockCountingFactory {

    public static List<BigSellerStockCounting> getBigSellerStockCountingList(File file) throws IOException {
        List<BigSellerStockCounting> stockCountings = new ArrayList<BigSellerStockCounting>();
        try {
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            BigSellerStockCountingContentHandler contentHandler = new BigSellerStockCountingContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), stockCountings);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        localizingDataRelation(stockCountings);

        return stockCountings;
    }

    private static void localizingDataRelation(List<BigSellerStockCounting> stockCountings) {
        for(BigSellerStockCounting counting : stockCountings){
            counting.setStock(counting.getOnHand());
        }
    }

    public void retieveUpdateBigSellerCounting(ListingStock info, List<ListingStock> listingStocks, List<Meas> measList){
        listingStocks.sort((o1, o2) -> {
            return o1.getName().compareTo(o2.getName());
        });

        int lo = 0;
        int hi = measList.size()-1;
        ListingStock foundInfo = null;

        while(lo <= hi){
            int mid = lo + (hi - lo) / 2;

            if(listingStocks.get(mid).getName().compareTo(info.getName()) > 0) hi = mid-1;
            else if(listingStocks.get(mid).getName().compareTo(info.getName()) < 0) lo = mid+1;
            else {
                foundInfo = listingStocks.get(mid);
                break;
            }
        }

        if(foundInfo != null && foundInfo instanceof BigSellerStockCounting && info instanceof BigSellerStockCounting){
            BigSellerStockCounting salesInfo = (BigSellerStockCounting) foundInfo;
            BigSellerStockCounting valueInfo = (BigSellerStockCounting) info;

            salesInfo.setStock(valueInfo.getStock());
            salesInfo.setSku(valueInfo.getSku());
        }
    }

    public static void saveOutputToFile(List<ListingStock> listingInfoList, File file) throws IOException{
        if(listingInfoList.isEmpty() && !(listingInfoList.get(0) instanceof OnlineSalesInfo)) {
            return;
        }
        List<BigSellerStockCounting> infoList = new ArrayList<BigSellerStockCounting>(listingInfoList.size());
        for(ListingStock listingStock : listingInfoList){
            if(listingStock instanceof BigSellerStockCounting){
                infoList.add((BigSellerStockCounting) listingStock);
            }
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        try (XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            int rowNum = 1; // start write at second row.

            for(BigSellerStockCounting info : infoList){
                
                Row row = sheet.createRow(rowNum);
                rowNum++;

                Cell skuCell = row.createCell(0);
                if(info.getSku() != null && !info.getSku().isEmpty()) {
                    skuCell.setCellValue(info.getSku());
                }

                Cell titleCell = row.createCell(1);
                if(info.getName() != null && !info.getName().isEmpty()) {
                    titleCell.setCellValue(info.getName());
                }

                Cell warehouseCell = row.createCell(2);
                if(info.getWarehouse() != null && !info.getWarehouse().isEmpty()) {
                    warehouseCell.setCellValue(info.getWarehouse());
                }

                Cell shelfCell = row.createCell(3);
                if(info.getShelf() != null && !info.getShelf().isEmpty()) {
                    shelfCell.setCellValue(info.getShelf());
                }

                // column 4 is warehouse area

                Cell onHandCell = row.createCell(5);
                onHandCell.setCellValue(info.getOnHand());

                Cell countCell = row.createCell(6);
                countCell.setCellValue(info.getStock());

                Cell noteCell = row.createCell(7);
                if(info.getNote() != null && !info.getNote().isEmpty()) {
                    noteCell.setCellValue(info.getNote());
                }
            }

            fileInputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
        }
    }
}
