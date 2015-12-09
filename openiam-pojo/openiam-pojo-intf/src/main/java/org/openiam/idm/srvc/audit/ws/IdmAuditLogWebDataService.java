package org.openiam.idm.srvc.audit.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/audit/service", name = "AuditDataService")
public interface IdmAuditLogWebDataService {
    
	@WebMethod
	IdmAuditLogEntity getLogRecord(final @WebParam(name = "id", targetNamespace = "") String id);
	
	@WebMethod
	Response addLogs(final List<IdmAuditLogEntity> events);

    @WebMethod
	Response addLog(final IdmAuditLogEntity record);

	@WebMethod
	List<IdmAuditLogEntity> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean,
								final @WebParam(name = "from", targetNamespace = "") int from,
								final @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
	List<String> getIds(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean,
						final @WebParam(name = "from", targetNamespace = "") int from,
						final @WebParam(name = "size", targetNamespace = "") int size);
	
	@WebMethod
	int count(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean);

}