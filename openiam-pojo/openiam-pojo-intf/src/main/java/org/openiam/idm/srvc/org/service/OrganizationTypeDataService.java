package org.openiam.idm.srvc.org.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.org.dto.OrganizationType;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationTypeDataService")
public interface OrganizationTypeDataService {

	@WebMethod
	@Deprecated
	public OrganizationType findById(final @WebParam(name = "id", targetNamespace = "") String id);
	
	public OrganizationType findByIdLocalized(final @WebParam(name = "id", targetNamespace = "") String id,
											  final @WebParam(name = "language", targetNamespace = "") Language language);
	
	@Deprecated
    public List<OrganizationType> findAllowedChildrenByDelegationFilter(final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
	
	public List<OrganizationType> findAllowedChildrenByDelegationFilterLocalized(final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
																				 final @WebParam(name = "language", targetNamespace = "") Language language);
	
	@WebMethod
	public List<OrganizationType> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationTypeSearchBean searchBean,
											final @WebParam(name = "from", targetNamespace = "") int from,
											final @WebParam(name = "size", targetNamespace = "") int size,
											final @WebParam(name = "language", targetNamespace = "") Language language);
	
	@WebMethod
	public int count(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationTypeSearchBean searchBean);
	
	@WebMethod
	public Response save(final @WebParam(name = "entity", targetNamespace = "") OrganizationType type);
	
	@WebMethod
	public Response delete(final @WebParam(name = "id", targetNamespace = "") String id);
	
	@WebMethod
	public Response addChild(final @WebParam(name = "id", targetNamespace = "") String id,
							 final @WebParam(name = "childId", targetNamespace = "") String childId);
	
	@WebMethod
	public Response removeChild(final @WebParam(name = "id", targetNamespace = "") String id,
							 	final @WebParam(name = "childId", targetNamespace = "") String childId);
}
