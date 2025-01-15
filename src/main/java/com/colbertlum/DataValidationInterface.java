package com.colbertlum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DataValidationInterface {

    Map<String, List<String>> expectDataOfColumnMap = new HashMap<String, List<String>>();
    Map<String, List<String>> expectContainsTextInDataOfColumnMap = new HashMap<String, List<String>>();

    Map<String, List<String>> actualDataOfColumnMap = new HashMap<String, List<String>>();
    
    void appendHandlingColumnExpectData(String column, String data);
    void appendHandlingColumnContainsTextInExpectData(String column, String data);
    void appendHandlingColumn();
    
    void monitorData(String column, String data);

    boolean hasUnExpectDataFromColumn();
    List<String> getContainedUnexpectDataColumn();
    List<String> getUnExpectDataFromColumn(String column);
}
