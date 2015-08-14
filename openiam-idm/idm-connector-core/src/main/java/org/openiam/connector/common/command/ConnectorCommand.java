package org.openiam.connector.common.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;

public interface ConnectorCommand<Request extends RequestType, Response extends ResponseType> {
    Response execute(Request request) throws ConnectorDataException;
}
