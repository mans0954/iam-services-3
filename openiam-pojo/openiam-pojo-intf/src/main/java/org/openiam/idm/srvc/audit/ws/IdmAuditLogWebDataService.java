package org.openiam.idm.srvc.audit.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.SearchAudit;

import javax.jws.WebService;
import java.util.Date;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities
 * persisted through this service.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/audit/service", name = "AuditDataService")
public interface IdmAuditLogWebDataService {

    /**
     * Creates a new audit log entry. The returned object contains the
     *
     * @param log
     * @return
     */
    public Response addLog(IdmAuditLog log);

    /*
    public IdmAuditLogListResponse searchEvents(SearchAudit search, Integer from, Integer size);
    public Integer countEvents(SearchAudit search);

    IdmAuditLogListResponse eventsAboutUser(String principal, Date startDate);
    public IdmAuditLogListResponse searchEventsAboutUser(String principal, Date startDate, Date endDate, Integer from, Integer size);

    public Integer countEventsAboutUser(String principal, Date startDate, Date endDate);
	*/
}