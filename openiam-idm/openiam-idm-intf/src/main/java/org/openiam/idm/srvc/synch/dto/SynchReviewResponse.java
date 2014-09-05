package org.openiam.idm.srvc.synch.dto;

import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

import javax.xml.bind.annotation.*;
import java.util.Date;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SyncReviewResponse", propOrder = {
        "status",
        "errorCode",
        "errorText"
})
public class SynchReviewResponse {
    @XmlAttribute(required = true)
    protected ResponseStatus status;
    protected ResponseCode errorCode;
    protected String errorText;

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public ResponseCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ResponseCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
