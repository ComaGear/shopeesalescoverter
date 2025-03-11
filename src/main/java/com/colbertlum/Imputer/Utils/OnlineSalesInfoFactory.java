package com.colbertlum.Imputer.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

import com.colbertlum.contentHandler.OnlineSalesInfoContentHandler;
import com.colbertlum.entity.ListingStock;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;

public class OnlineSalesInfoFactory {

    public static List<OnlineSalesInfo> getOnlineSalesInfoList(File file) throws IOException{
        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        try {
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
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

        // List<ListingStock> listingStocks = new ArrayList<ListingStock>(onlineSalesInfoList);
        return onlineSalesInfoList;
    }

    public static void retieveUpdateOnlineSalesInfo(ListingStock info, List<ListingStock> infoList ,List<Meas> measList){
        
        infoList.sort(new Comparator<ListingStock>() {

            @Override
            public int compare(ListingStock o1, ListingStock o2) {
                return o1.getName().compareTo(o2.getName());
                // int compareTo = o1.getProductId().compareTo(o2.getProductId());
                // if(compareTo == 0){
                //     return o1.getVariationId().compareTo(o2.getVariationId());
                // }
                // return compareTo;
            }
        });

        int lo = 0;
        int hi = measList.size()-1;
        ListingStock foundInfo = null;

        while(lo <= hi){
            int mid = lo + (hi - lo) / 2;

            if(infoList.get(mid).getName().compareTo(info.getName()) > 0) hi = mid-1;
            else if(infoList.get(mid).getName().compareTo(info.getName()) < 0) lo = mid+1;
            else {
                foundInfo = infoList.get(mid);
                break;
            }

            // if(infoList.get(mid).getProductId().compareTo(info.getProductId()) > 0) hi = mid-1;
            // else if(infoList.get(mid).getProductId().compareTo(info.getProductId()) < 0) lo = mid+1;
            // else {
            //     if(infoList.get(mid).getVariationId().compareTo(info.getVariationId()) > 0) hi = mid-1;
            //     else if(infoList.get(mid).getVariationId().compareTo(info.getVariationId()) < 0) lo = mid+1;
            //     foundInfo = infoList.get(mid);
            //     break;
            // }
        }

        if(foundInfo != null && foundInfo instanceof OnlineSalesInfo && info instanceof OnlineSalesInfo){
            OnlineSalesInfo salesInfo = (OnlineSalesInfo) foundInfo;
            OnlineSalesInfo valueInfo = (OnlineSalesInfo) info;

            salesInfo.setQuantity(valueInfo.getQuantity());
            salesInfo.setParentSku(valueInfo.getParentSku());
            String sku = valueInfo.getSku();
            if(sku != null && !sku.isEmpty() && sku.contains("-")){
                salesInfo.setSku(sku);
            } else {
                salesInfo.setParentSku(sku);
            }
            salesInfo.setPrice(valueInfo.getPrice());
        }
    }

    
    public static void saveOutputToFile(List<ListingStock> listingInfoList, File file) throws IOException{
        if(listingInfoList.isEmpty() && !(listingInfoList.get(0) instanceof OnlineSalesInfo)) {
            return;
        }
        List<OnlineSalesInfo> infoList = new ArrayList<OnlineSalesInfo>(listingInfoList.size());
        for(ListingStock listingStock : listingInfoList){
            if(listingStock instanceof OnlineSalesInfo){
                infoList.add((OnlineSalesInfo) listingStock);
            }
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        try (XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for(OnlineSalesInfo info : infoList){
                int foundRow = info.getFoundRow();
                Row row = sheet.getRow(foundRow);
                Cell cell = row.getCell(0);
                if(cell == null || !cell.getStringCellValue().equals(info.getProductId())
                    || row.getCell(2) == null || !row.getCell(2).getStringCellValue().equals(info.getVariationId())){
                    return;
                }

                if(info.getSku() != null && info.getSku().contains("-")) {
                    Cell skuCell = row.getCell(5);
                    if(skuCell == null) skuCell = row.createCell(5);
                    skuCell.setCellValue(info.getSku());
                } else {
                    Cell parentSKuCell = row.getCell(4);
                    if(parentSKuCell == null) parentSKuCell = row.createCell(4);
                    parentSKuCell.setCellValue(info.getParentSku());
                }

                Cell priceCell = row.getCell(6);
                if(priceCell == null) priceCell = row.createCell(6);
                priceCell.setCellValue(info.getPrice());

                Cell stockCell = row.getCell(7);
                if(stockCell == null) stockCell = row.createCell(7);
                stockCell.setCellValue(Double.valueOf(info.getQuantity()));
            }

            fileInputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
        }
    }
}
