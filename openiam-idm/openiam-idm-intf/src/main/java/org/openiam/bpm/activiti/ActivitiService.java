package org.openiam.bpm.activiti;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace = "urn:idm.openiam.org/bpm/request/service", name = "ActivitiService")
public interface ActivitiService {

	@WebMethod
	public String sayHello();
}
