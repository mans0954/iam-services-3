package org.openiam.base.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by alexander on 08/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseServiceRequest", propOrder = {
        "requesterId",
        "language",
        "requestorLogin",
        "requestClientIP",
        "requestorSessionID",
        "testRequest"
})
public class BaseServiceRequest implements Serializable{
    private static final long serialVersionUID = 1L;
    // track the source of the request
    protected String requesterId;
    protected String requestorSessionID;
    protected String requestorLogin;
    protected String requestClientIP;
    private Language language;
    /*
     * if true, means that the request is called as a 'test' - not as a real-world call
     * the service code should (or should not) perform an action based on this flag
     */
    @JsonIgnore
    private boolean testRequest;

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLanguageId(String languageId) {
        this.language = new Language();
        this.language.setId(languageId);
    }

    public String getRequestorSessionID() {
        return requestorSessionID;
    }

    public void setRequestorSessionID(String requestorSessionID) {
        this.requestorSessionID = requestorSessionID;
    }

    public String getRequestorLogin() {
        return requestorLogin;
    }

    public void setRequestorLogin(String requestorLogin) {
        this.requestorLogin = requestorLogin;
    }

    public String getRequestClientIP() {
        return requestClientIP;
    }

    public void setRequestClientIP(String requestClientIP) {
        this.requestClientIP = requestClientIP;
    }

    public boolean isTestRequest() {
        return testRequest;
    }

    public void setTestRequest(boolean testRequest) {
        this.testRequest = testRequest;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BaseServiceRequest{");
        sb.append("requesterId='").append(requesterId).append('\'');
        sb.append(", requestorSessionID=").append(requestorSessionID);
        sb.append(", requestorLogin=").append(requestorLogin);
        sb.append(", requestClientIP=").append(requestClientIP);
        sb.append(", language=").append(language);
        sb.append('}');
        return sb.toString();
    }
}
