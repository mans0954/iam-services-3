package org.openiam.provision.dto.accessmodel;

import org.openiam.base.ws.ResponseStatus;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Map;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlType(propOrder = {"status", "error","bean"})
@XmlRootElement(name = "response")
public class UserAccessControlResponse {
    private ResponseStatus status;
    private String error;
    private UserAccessControlBean bean;

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public UserAccessControlBean getBean() {
        return bean;
    }

    public void setBean(UserAccessControlBean bean) {
        this.bean = bean;
    }
}
