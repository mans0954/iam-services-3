package org.openiam.idm.srvc.prov.request.ws;


import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;
import org.openiam.idm.srvc.user.dto.Supervisor;

/*
 * Web Service interface to manage provisioning requests
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/prov/request/service", name = "RequestWebService")
public interface RequestWebService {

	/*
	@WebMethod
	Response addRequest(
			@WebParam(name = "request", targetNamespace = "")
			ProvisionRequest request);
	
	Response updateRequest(ProvisionRequest request);
	*/
	/**
	 * Returns a request
	 * @param requestId
	 * @return
	 */
	/*
	@WebMethod
	ProvisionRequest getRequest(
			@WebParam(name = "requestId", targetNamespace = "")
			String requestId);
	*/
	/**
	 * Method to carry out adhoc search;
	 * @param search
	 * @return
	 */
	/*
	@WebMethod
	List<ProvisionRequest> search(
			@WebParam(name = "search", targetNamespace = "")
			SearchRequest search);
	*/
	/*
	@WebMethod
	List<ProvisionRequest> requestByApprover(
			@WebParam(name = "approverId", targetNamespace = "")
			String approverId, 
			@WebParam(name = "status", targetNamespace = "")
			String status);
	*/
}
