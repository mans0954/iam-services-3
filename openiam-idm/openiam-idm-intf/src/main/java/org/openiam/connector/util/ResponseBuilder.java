package org.openiam.connector.util;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.constant.StatusCodeType;

public final class ResponseBuilder {

	public static void populateResponse(final ResponseType response, final StatusCodeType status,
            final ErrorCode err, final String msg) {
		response.setStatus(status);
		response.setError(err);
		response.addErrorMessage(msg);
	}
}
