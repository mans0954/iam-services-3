package org.openiam.connector.type;

import org.openiam.provision.constant.ErrorCode;

public class ConnectorDataException  extends Exception {

    private ErrorCode code;

    public ConnectorDataException(final ErrorCode code) {
        this.code = code;
    }

    public ConnectorDataException(final ErrorCode code, final Throwable originalCause) {
        super(originalCause);
        this.code = code;
    }

    public ConnectorDataException(final ErrorCode code, final String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

}
