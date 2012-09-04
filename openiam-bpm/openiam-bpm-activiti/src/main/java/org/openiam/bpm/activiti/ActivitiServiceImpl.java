package org.openiam.bpm.activiti;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.beans.factory.InitializingBean;

@WebService(endpointInterface = "org.openiam.bpm.activiti.ActivitiService", 
targetNamespace = "urn:idm.openiam.org/bpm/request/service", 
serviceName = "ActivitiService")
public class ActivitiServiceImpl implements ActivitiService {

	@Override
	@WebMethod
	public String sayHello() {
		return "Hello";
	}
}
