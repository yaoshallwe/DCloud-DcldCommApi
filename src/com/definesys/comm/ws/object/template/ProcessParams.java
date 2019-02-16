package com.definesys.comm.ws.object.template;

import org.codehaus.jackson.map.annotate.JsonRootName;


@JsonRootName("processParams")
public class ProcessParams {
    public ProcessParams() {
        super();
    }
    
    private String processCode;
    private String processFormNo;
    private String processApplier;
    private String processParam;
    private String processKeyword;
    private String processTitle;
    private String businessSysCode;

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessFormNo(String processFormNo) {
        this.processFormNo = processFormNo;
    }

    public String getProcessFormNo() {
        return processFormNo;
    }

    public void setProcessApplier(String processApplier) {
        this.processApplier = processApplier;
    }

    public String getProcessApplier() {
        return processApplier;
    }

    public void setProcessParam(String processParam) {
        this.processParam = processParam;
    }

    public String getProcessParam() {
        return processParam;
    }

    public void setProcessKeyword(String processKeyword) {
        this.processKeyword = processKeyword;
    }

    public String getProcessKeyword() {
        return processKeyword;
    }

    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    public String getProcessTitle() {
        return processTitle;
    }

    public void setBusinessSysCode(String businessSysCode) {
        this.businessSysCode = businessSysCode;
    }

    public String getBusinessSysCode() {
        return businessSysCode;
    }
}
