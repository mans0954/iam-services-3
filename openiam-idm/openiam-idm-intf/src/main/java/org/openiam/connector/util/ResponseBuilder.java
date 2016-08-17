package org.openiam.connector.util;

import org.openiam.provision.constant.ErrorCode;
import org.openiam.base.response.ResponseType;
import org.openiam.provision.constant.StatusCodeType;

public final class ResponseBuilder {

	public static void populateResponse(final ResponseType response, final StatusCodeType status,
            final ErrorCode err, final String msg) {
		response.setStatus(status);
		response.setError(err);
		response.addErrorMessage(msg);
	}
}
