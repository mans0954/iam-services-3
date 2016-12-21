package org.openiam.srvc.am;

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

	OrganizationType findById(final @WebParam(name = "id", targetNamespace = "") String id,
									   final @WebParam(name = "lang", targetNamespace = "") Language language);
	
	List<OrganizationType> findAllowedChildrenByDelegationFilter(final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
																		  final @WebParam(name = "lang", targetNamespace = "") Language language);
	
	@WebMethod
	List<OrganizationType> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationTypeSearchBean searchBean,
									 final @WebParam(name = "from", targetNamespace = "") int from,
									 final @WebParam(name = "size", targetNamespace = "") int size,
									 final @WebParam(name = "lang", targetNamespace = "") Language language);
	
	@WebMethod
	int count(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationTypeSearchBean searchBean);
	
	@WebMethod
	Response save(final @WebParam(name = "entity", targetNamespace = "") OrganizationType type);
	
	@WebMethod
	Response delete(final @WebParam(name = "id", targetNamespace = "") String id);
	
	@WebMethod
	Response addChild(final @WebParam(name = "id", targetNamespace = "") String id,
					  final @WebParam(name = "childId", targetNamespace = "") String childId);
	
	@WebMethod
	Response removeChild(final @WebParam(name = "id", targetNamespace = "") String id,
						 final @WebParam(name = "childId", targetNamespace = "") String childId);

    @WebMethod
	List<OrganizationType> getAllowedParents(final @WebParam(name = "organizationTypeId", targetNamespace = "") String organizationTypeId,
											 final @WebParam(name = "lang", targetNamespace = "") Language language);
}
