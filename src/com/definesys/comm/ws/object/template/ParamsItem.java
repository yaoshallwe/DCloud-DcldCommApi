package com.definesys.comm.ws.object.template;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("paramsItem")
public class ParamsItem {
    public ParamsItem() {
        super();
    }
    
    private String key;
    private String value;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

