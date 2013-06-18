package org.openiam.spml2.spi.common;

import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;

public interface ConnectorCommand<Request extends RequestType, Response extends ResponseType> {
    public Response execute(Request request) throws ConnectorDataException;
}
