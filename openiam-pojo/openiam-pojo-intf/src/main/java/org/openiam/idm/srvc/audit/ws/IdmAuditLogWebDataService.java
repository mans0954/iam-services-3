package org.openiam.idm.srvc.audit.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.AuditLogBuilderDto;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.SearchAudit;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
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
    public Response addLogs(final List<AuditLogBuilderDto> logList);
    
	@WebMethod
    public List<IdmAuditLog> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean,
    								   final @WebParam(name = "from", targetNamespace = "") int from,
    								   final @WebParam(name = "size", targetNamespace = "") int size);
	
	@WebMethod
	public int count(final @WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean);

    /*
    public IdmAuditLogListResponse searchEvents(SearchAudit search, Integer from, Integer size);
    public Integer countEvents(SearchAudit search);

    IdmAuditLogListResponse eventsAboutUser(String principal, Date startDate);
    public IdmAuditLogListResponse searchEventsAboutUser(String principal, Date startDate, Date endDate, Integer from, Integer size);

    public Integer countEventsAboutUser(String principal, Date startDate, Date endDate);
	*/
}