package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.provision.dto.ProvisionUser;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NewHireRequest", propOrder = {
    "provisionUser",
    "requestorInformation"
})
public class NewHireRequest implements Serializable {
	
	private static final long serialVersionUID = 6556361291027451888L;
	
	private ProvisionUser provisionUser;
	private RequestorInformation requestorInformation = new RequestorInformation();
	
	public NewHireRequest() {
		
	}
	
	public ProvisionUser getProvisionUser() {
		return provisionUser;
	}

	public void setProvisionUser(ProvisionUser provisionUser) {
		this.provisionUser = provisionUser;
	}

	public RequestorInformation getRequestorInformation() {
		return requestorInformation;
	}

	public void setRequestorInformation(RequestorInformation requestorInformation) {
		this.requestorInformation = requestorInformation;
	}
}
