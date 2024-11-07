package com.zhu.datasource;

import com.zhu.model.enums.SearchTypeEnums;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceRegister {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private VideoDataSource videoDataSource;

    private Map<String, DataSource> DatasourceHandlerMap;

    @PostConstruct
    public void doInit() {
        DatasourceHandlerMap = new HashMap() {{
            put(SearchTypeEnums.SEARCH_VIDEO.getValue(), videoDataSource);
            put(SearchTypeEnums.SEARCH_USER.getValue(), userDataSource);
        }};
    }

    public DataSource getDataSourceByType(String type) {
        if (DatasourceHandlerMap == null) {
            return null;
        }
        return DatasourceHandlerMap.get(type);
    }

}
