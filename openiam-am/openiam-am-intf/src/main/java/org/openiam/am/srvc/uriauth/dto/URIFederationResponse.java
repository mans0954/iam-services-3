package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIFederationResponse", propOrder = {
	"status",
	"errorCode"
})
public class URIFederationResponse extends Response {

	private Integer requiredAuthLevel;
	
	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public Integer getRequiredAuthLevel() {
		return requiredAuthLevel;
	}

	public void setRequiredAuthLevel(Integer requiredAuthLevel) {
		this.requiredAuthLevel = requiredAuthLevel;
	}
}
