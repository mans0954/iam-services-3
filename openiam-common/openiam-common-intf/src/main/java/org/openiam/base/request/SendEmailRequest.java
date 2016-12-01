package org.openiam.base.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexander on 01/12/16.
 */
public class SendEmailRequest extends BaseServiceRequest {
    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String msg;
    private List<String> attachment;
    private boolean isHtmlFormat;
    private Date executionDateTime;


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }

    public boolean isHtmlFormat() {
        return isHtmlFormat;
    }

    public void setHtmlFormat(boolean htmlFormat) {
        isHtmlFormat = htmlFormat;
    }

    public Date getExecutionDateTime() {
        return executionDateTime;
    }

    public void setExecutionDateTime(Date executionDateTime) {
        this.executionDateTime = executionDateTime;
    }

    public void addTo(String to){
        if(this.to==null){
            this.to = new ArrayList<>();
        }
        this.to.add(to);
    }
    public void addCc(String cc){
        if(this.cc==null){
            this.cc = new ArrayList<>();
        }
        this.cc.add(cc);
    }
    public void addBcc(String bcc){
        if(this.bcc==null){
            this.bcc = new ArrayList<>();
        }
        this.bcc.add(bcc);
    }
    public void addAttachment(String attachment){
        if(this.attachment==null){
            this.attachment = new ArrayList<>();
        }
        this.attachment.add(attachment);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SendEmailRequest{");
        sb.append(super.toString());
        sb.append(", from='").append(from).append('\'');
        sb.append(", to=").append(to);
        sb.append(", cc=").append(cc);
        sb.append(", bcc=").append(bcc);
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", attachment=").append(attachment);
        sb.append(", isHtmlFormat=").append(isHtmlFormat);
        sb.append(", executionDateTime=").append(executionDateTime);
        sb.append('}');
        return sb.toString();
    }
}
