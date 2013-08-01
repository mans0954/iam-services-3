package org.openiam.connector.util;

import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.ResponseType;
import org.openiam.connector.type.StatusCodeType;

public final class ResponseBuilder {

	public static void populateResponse(final ResponseType response, final StatusCodeType status,
            final ErrorCode err, final String msg) {
		response.setStatus(status);
		response.setError(err);
		response.addErrorMessage(msg);
	}
}
