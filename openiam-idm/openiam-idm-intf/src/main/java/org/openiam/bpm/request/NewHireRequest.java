package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.provision.dto.ProvisionUser;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NewHireRequest", propOrder = {
    "provisionUser"
})
public class NewHireRequest extends RequestorInformation {
	
	private static final long serialVersionUID = 6556361291027451888L;
	
	private ProvisionUser provisionUser;
	
	public NewHireRequest() {
		
	}
	
	public ProvisionUser getProvisionUser() {
		return provisionUser;
	}

	public void setProvisionUser(ProvisionUser provisionUser) {
		this.provisionUser = provisionUser;
	}
}
