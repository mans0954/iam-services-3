package org.openiam.idm.srvc.synch.service.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.synch.service.SynchConfigDAO;
import org.openiam.idm.srvc.synch.service.SynchConfigDataMappingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Implementation object for <code>GenericObjectSynchService</code> which is used to synchronize objects such as
 * Groups, Roles, Organizations and others. Synchronization for these activities is initiated from
 * startSynchronization()
 */
@Service
public class GenericObjectSynchServiceImpl implements GenericObjectSynchService {
    @Autowired
    protected SynchConfigDAO synchConfigDao;
    @Autowired
    protected SynchConfigDataMappingDAO synchConfigMappingDao;
    @Autowired
    protected SourceAdapterFactory adapterFactory;

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Autowired
    private AuditLogService auditLogService;

    private static final Log log = LogFactory.getLog(GenericObjectSynchServiceImpl.class);

    public SyncResponse startSynchronization(SynchConfig config) {
        log.debug("- Generic Object Synchronization started..^^^^^^^^");
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.SYNCHRONIZATION.value());
        idmAuditLog.setAuditDescription("- Generic Object Synchronization started..^^^^^^^^");
        try {
            SourceAdapter adapt = adapterFactory.create(config);

            long newLastExecTime = System.currentTimeMillis();


            SyncResponse resp = adapt.startSynch(config);

            log.debug("SyncResponse updateTime value=" + resp.getLastRecordTime());

            if (resp.getLastRecordTime() == null) {

                synchConfigDao.updateExecTime(config.getSynchConfigId(), new Timestamp( newLastExecTime ));
            }else {
                synchConfigDao.updateExecTime(config.getSynchConfigId(), new Timestamp( resp.getLastRecordTime().getTime() ));
            }

            if (resp.getLastRecProcessed() != null) {

                synchConfigDao.updateLastRecProcessed(config.getSynchConfigId(),resp.getLastRecProcessed() );
            }


            log.debug("-Generic Object Synchronization COMPLETE.^^^^^^^^");
            idmAuditLog.succeed();
            idmAuditLog.setAuditDescription("-Generic Object Synchronization COMPLETE.^^^^^^^^");
            return resp;
        }catch( ClassNotFoundException cnfe) {

            cnfe.printStackTrace();

            log.error(cnfe);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(cnfe.getMessage());
            return resp;
        }catch(Exception e) {

            e.printStackTrace();

            log.error(e);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
            return resp;
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
    }

}
