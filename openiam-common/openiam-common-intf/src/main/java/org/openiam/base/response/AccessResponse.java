package org.openiam.base.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthoriationManagerAccessResponse", propOrder = {
    "result"
})
public class AccessResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private boolean result;
	
	public AccessResponse() {
		
	}
	
	public AccessResponse(final ResponseStatus responseStatus) {
		setResponseStatus(responseStatus);
	}
	
	public boolean getResult() {
		return result;
	}
	
	public void setResult(final boolean result) {
		this.result = result;
	}
}
