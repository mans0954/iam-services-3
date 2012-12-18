package org.openiam.idm.srvc.audit.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.dozer.converter.IdmAuditLogCustomDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogDozerConverter;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.util.encrypt.HashDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for <code>IdmAuditLogDataService</code>. All audit logging activities 
 * persisted through this service.
 */
@Service("auditDataService")
public class IdmAuditLogDataServiceImpl implements IdmAuditLogDataService {
    @Autowired
    private IdmAuditLogDAO idmAuditLogDAO;
    @Autowired
    private IdmAuditLogCustomDAO idmAuditLogCustomDAO;
    @Autowired
    private IdmAuditLogDozerConverter idmAuditLogDozerConverter;
    @Autowired
    private IdmAuditLogCustomDozerConverter idmAuditLogCustomDozerConverter;

    @Autowired
    private HashDigest hash;
    protected LoginDataService loginDS;
    protected SysConfiguration sysConfiguration;

    private static final Log sysLog = LogFactory
            .getLog(IdmAuditLogDataServiceImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.audit.service.IdmAuditLogDataService#addLog(org.
     * openiam.idm.srvc.audit.dto.IdmAuditLog)
     */
    @Transactional
    public IdmAuditLog addLog(IdmAuditLog log) {
        sysLog.debug("Persisting new audit event in database.");

        // create a hash that can be used validate the logs integrity
        String str = log.getActionId() + log.getActionStatus()
                + log.getDomainId() + log.getPrincipal() + log.getUserId()
                + log.getObjectTypeId() + log.getObjectId()
                + log.getLinkedLogId() + log.getRequestId()
                + log.getActionDatetime();
        log.setLogHash(hash.HexEncodedHash(str));

        log.updateCustomRecord("PUBLISHED", "0", 2,
                CustomIdmAuditLogType.ATTRIB);

        IdmAuditLogEntity ialcEntity = idmAuditLogDozerConverter
                .convertToEntity(log, false);
        idmAuditLogDAO.save(ialcEntity);

        List<IdmAuditLogCustomEntity> fetchedList = new ArrayList<IdmAuditLogCustomEntity>(
                0);
        if (log.getCustomRecords() != null) {
            fetchedList.addAll(idmAuditLogCustomDozerConverter
                    .convertToEntityList(log.getCustomRecords(), false));
            for (IdmAuditLogCustomEntity custom : fetchedList) {
                custom.setLogId(ialcEntity.getLogId());
            }
            idmAuditLogCustomDAO.save(fetchedList);
        }
        ialcEntity.setCustomRecords(fetchedList);
        log = idmAuditLogDozerConverter.convertToDTO(ialcEntity, true);
        return log;
    }

    public void updateLog(IdmAuditLog log) {
        idmAuditLogDAO.update(idmAuditLogDozerConverter.convertToEntity(log,
                true));
    }

    public List<IdmAuditLog> getCompleteLog() {
        return idmAuditLogDozerConverter.convertToDTOList(
                idmAuditLogDAO.findAll(), true);
    }

    public List<IdmAuditLog> getPasswordChangeLog() {
        return idmAuditLogDozerConverter.convertToDTOList(
                idmAuditLogDAO.findPasswordEvents(), true);
    }

    /**
     * Returns a collection of audit log entries based on the search parameters.
     * @param search
     * @return
     */
    public List<IdmAuditLog> search(SearchAudit search) {
        return idmAuditLogDozerConverter.convertToDTOList(
                idmAuditLogDAO.search(search), true);
    }

    public List<IdmAuditLog> eventsAboutUser(String principal, Date startDate) {

        LoginEntity l = loginDS.getLoginByManagedSys(
                sysConfiguration.getDefaultSecurityDomain(), principal,
                sysConfiguration.getDefaultManagedSysId());

        if (l == null) {
            return null;
        }

        List<LoginEntity> principalList = loginDS.getLoginByUser(l.getUserId());

        if (principalList == null || principalList.isEmpty()) {
            return null;
        }

        List<String> principalListAsStr = getListOfPrincipals(principalList);

        return idmAuditLogDozerConverter.convertToDTOList(idmAuditLogDAO
                .findEventsAboutIdentityList(principalListAsStr, startDate),
                true);

    }

    private List<String> getListOfPrincipals(List<LoginEntity> principalList) {

        List<String> strList = new ArrayList<String>();

        for (LoginEntity l : principalList) {
            strList.add(l.getLogin());

        }
        return strList;

    }

    public HashDigest getHash() {
        return hash;
    }

    public void setHash(HashDigest hash) {
        this.hash = hash;
    }

    public LoginDataService getLoginDS() {
        return loginDS;
    }

    public void setLoginDS(LoginDataService loginDS) {
        this.loginDS = loginDS;
    }

    public SysConfiguration getSysConfiguration() {
        return sysConfiguration;
    }

    public void setSysConfiguration(SysConfiguration sysConfiguration) {
        this.sysConfiguration = sysConfiguration;
    }
}
