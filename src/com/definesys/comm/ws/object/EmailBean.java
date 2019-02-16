package com.definesys.comm.ws.object;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("email")
public class EmailBean {
    public EmailBean() {
        super();
    }
    
    private String from;
    private String to;
    private String subject;
    private String cc;
    private String bcc;
    private String content;
    private String emailSpliter;

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCc() {
        return cc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setEmailSpliter(String emailSpliter) {
        this.emailSpliter = emailSpliter;
    }

    public String getEmailSpliter() {
        return emailSpliter;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}
