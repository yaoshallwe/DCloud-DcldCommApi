package com.definesys.comm.ws.object.template;

import com.definesys.comm.ws.object.EmailBean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("emailTemplateData")
@XmlRootElement
public class EmailTemplateData {
    public EmailTemplateData() {
        super();
    }
    
    @XmlElement(required = true)
    private String instanceId;
    private String taskId;
    @XmlElement(required = true)
    private String outcome;
    @XmlElement(defaultValue = "zh")
    private String locale;
    private EmailBean emailInfo;
    private BusinessParams businessParams;
    private ProcessParams processParams;
    private OthersParams othersParams;


    public void setEmailInfo(EmailBean emailInfo) {
        this.emailInfo = emailInfo;
    }

    public EmailBean getEmailInfo() {
        return emailInfo;
    }

    public void setBusinessParams(BusinessParams businessParams) {
        this.businessParams = businessParams;
    }

    public BusinessParams getBusinessParams() {
        return businessParams;
    }

    public void setProcessParams(ProcessParams processParams) {
        this.processParams = processParams;
    }

    public ProcessParams getProcessParams() {
        return processParams;
    }

    public void setOthersParams(OthersParams othersParams) {
        this.othersParams = othersParams;
    }

    public OthersParams getOthersParams() {
        return othersParams;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
