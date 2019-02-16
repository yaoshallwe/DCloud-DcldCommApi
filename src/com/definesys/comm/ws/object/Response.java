package com.definesys.comm.ws.object;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("response")
public class Response {
    
    public final static String SUCCESS = "SUCCESS";
    public final static String FAILED = "FAILED";
    
    public Response() {
        super();
    }
    
    protected String code;
    protected String devMsg;
    protected String userMsg;
    protected String errorUuid;
    protected Object data;
    
    public void setBaseMsg(boolean success,String userMsg){
        if(success){
            this.code = SUCCESS;
            this.devMsg = SUCCESS;
            this.userMsg = userMsg;
        }
        else{
            this.code = FAILED;
            this.devMsg = FAILED;
            this.userMsg = userMsg;
        }
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setDevMsg(String devMsg) {
        this.devMsg = devMsg;
    }

    public String getDevMsg() {
        return devMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setErrorUuid(String errorUuid) {
        this.errorUuid = errorUuid;
    }

    public String getErrorUuid() {
        return errorUuid;
    }


    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
