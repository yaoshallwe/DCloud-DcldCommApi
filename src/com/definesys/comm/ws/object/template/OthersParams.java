package com.definesys.comm.ws.object.template;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("othersParams")
public class OthersParams {
    public OthersParams() {
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
