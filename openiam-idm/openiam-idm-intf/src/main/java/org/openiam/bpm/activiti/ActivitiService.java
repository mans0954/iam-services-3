package org.openiam.bpm.activiti;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.bpm.request.NewHireRequest;
import org.openiam.bpm.response.NewHireResponse;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.provision.dto.ProvisionUser;

@WebService(targetNamespace = "urn:idm.openiam.org/bpm/request/service", name = "ActivitiService")
public interface ActivitiService {

	@WebMethod
	public String sayHello();
	
	@WebMethod
	public NewHireResponse initiateNewHireRequest(final NewHireRequest newHireRequest);
	
	@WebMethod
	public NewHireResponse claimNewHireRequest(final NewHireRequest newHireRequest);
	
	@WebMethod
	public NewHireResponse acceptNewHireRequest(final NewHireRequest newHireRequest);
	
	@WebMethod
	public NewHireResponse rejectNewHireRequest(final NewHireRequest newHireRequest);
}
