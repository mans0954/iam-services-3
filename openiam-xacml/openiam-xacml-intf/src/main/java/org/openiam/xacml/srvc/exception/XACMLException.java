package org.openiam.xacml.srvc.exception;

import org.openiam.xacml.srvc.constants.XACMLError;

/**
 * Created by zaporozhec on 7/14/15.
 */
public class XACMLException extends Exception {

    XACMLError error;

    public XACMLException(XACMLError error, String message) {
        super(message);
        this.error = error;
    }

    public XACMLError getError() {
        return error;
    }

    public void setError(XACMLError error) {
        this.error = error;
    }

    public String getReport() {
        return error.value() + ":" + this.getMessage();
    }
}
