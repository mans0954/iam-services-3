package org.openiam.xacml.srvc.rest.response;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;

/**
 * Created by zaporozhec on 7/14/15.
 */
public abstract class AbstractRestResponse<ReturnObject> {
    private ReturnObject object;
    private String errorText;
    private ResponseStatus status;
    private String resposeCode;

    public ReturnObject getObject() {
        return object;
    }

    public void setObject(ReturnObject object) {
        this.object = object;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getResposeCode() {
        return resposeCode;
    }

    public void setResposeCode(String resposeCode) {
        this.resposeCode = resposeCode;
    }
}
