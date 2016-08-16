package org.openiam.base.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAuthorizationManagerResponse", propOrder = {
    "statusMessage",
    "responseStatus"
})
public abstract class AbstractResponse extends Response {

	private static final long serialVersionUID = -1L;
	
	private String statusMessage;
	private ResponseStatus responseStatus = ResponseStatus.FAILURE;

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString() {
		return String
				.format("AbstractAuthorizationManagerResponse [statusMessage=%s, responseStatus=%s]",
						statusMessage, responseStatus);
	}

	
}
