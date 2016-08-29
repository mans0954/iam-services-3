package org.openiam.connector.common.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;

public interface ConnectorCommand<Request extends RequestType, Response extends ResponseType> {
    Response execute(Request request) throws ConnectorDataException;
}
