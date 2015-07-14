package org.openiam.rest.response;

import org.openiam.base.ws.ResponseStatus;

import java.util.List;

/**
 * Created by zaporozhec on 7/14/15.
 */

public abstract class AbstractRestResponse<ReturnObject> {

    private List<ReturnObject> objectList;
    private ReturnObject object;
    private String errorText;
    private ResponseStatus status;
    private Integer responseCode;

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

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public List<ReturnObject> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<ReturnObject> objectList) {
        this.objectList = objectList;
    }
}
