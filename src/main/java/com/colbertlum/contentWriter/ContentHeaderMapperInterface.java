package com.colbertlum.contentWriter;

import java.util.List;

public interface ContentHeaderMapperInterface<T> {
    
    public String onCell(String header, T item);

    public List<String> onHeader();
}
