package org.openiam.idm.srvc.audit.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.util.encrypt.HashDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for <code>IdmAuditLogDataService</code>. All audit logging activities 
 * persisted through this service.
 */
@Service("auditDataService")
public class AuditLogServiceImpl implements AuditLogService {
    
	@Autowired
	private AuditLogSender auditLogSender;
	
	@Autowired
    private HashDigest hash;
    
    private static final Log LOG = LogFactory.getLog(AuditLogServiceImpl.class);
    
    private String nodeIP = null;
    
    @PostConstruct
    public void init() {
    	try {
    		nodeIP = InetAddress.getLocalHost().getCanonicalHostName();
    	} catch(Throwable e) {
    		LOG.error("Can't get IP Address of this ESB - nodeIP for all audit logs will be set to null", e);
    	}
    }
    
    private void prepare(final IdmAuditLogEntity log, final String coorelationId) {
    	if(log != null) {
    		log.setHash(hash.HexEncodedHash(log.concat()));
    		log.setNodeIP(nodeIP);
    		log.setCoorelationId(coorelationId);
    		if(CollectionUtils.isNotEmpty(log.getChildLogs())) {
    			for(final IdmAuditLogEntity entity : log.getChildLogs()) {
    				prepare(entity, coorelationId);
    			}
    		}
    		
    		if(CollectionUtils.isNotEmpty(log.getCustomRecords())) {
    			for(final IdmAuditLogCustomEntity attribute : log.getCustomRecords()) {
    				attribute.setId(UUIDGen.getUUID());
    				attribute.setLog(log);
    			}
    		}
    	}
    }

    //TODO:  put on JMS queue
	@Override
	public void enqueue(final AuditLogBuilder builder) {
		final IdmAuditLogEntity log = builder.getEntity();
		prepare(log, UUIDGen.getUUID());
		auditLogSender.send(log);
	}
}
