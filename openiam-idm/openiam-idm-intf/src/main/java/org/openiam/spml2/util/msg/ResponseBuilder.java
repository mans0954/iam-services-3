package org.openiam.spml2.util.msg;

import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;

public final class ResponseBuilder {

	public static void populateResponse(final ResponseType response, final StatusCodeType status,
            final ErrorCode err, final String msg) {
		response.setStatus(status);
		response.setError(err);
		response.addErrorMessage(msg);
	}
}
