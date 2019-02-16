package com.definesys.comm.ws.object.template;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("businessParams")
public class BusinessParams {
    public BusinessParams() {
        super();
    }
    
    private List<ParamsItem> paramsList;

    public void setParamsList(List<ParamsItem> paramsList) {
        this.paramsList = paramsList;
    }

    public List<ParamsItem> getParamsList() {
        return paramsList;
    }
}
