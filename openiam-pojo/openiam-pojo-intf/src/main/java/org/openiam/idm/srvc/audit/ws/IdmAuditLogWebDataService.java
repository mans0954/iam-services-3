package org.openiam.idm.srvc.audit.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/audit/service", name = "AuditDataService")
public interface IdmAuditLogWebDataService {
    
	@WebMethod
	public IdmAuditLog getLogRecord(final @WebParam(name = "id", targetNamespace = "") String id);
	
	@WebMethod
    public Response addLogs(final List<IdmAuditLog> events);
    
	@WebMethod
    public List<IdmAuditLog> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean,
    								   final @WebParam(name = "from", targetNamespace = "") int from,
    								   final @WebParam(name = "size", targetNamespace = "") int size);
	
	@WebMethod
	public int count(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean);

}