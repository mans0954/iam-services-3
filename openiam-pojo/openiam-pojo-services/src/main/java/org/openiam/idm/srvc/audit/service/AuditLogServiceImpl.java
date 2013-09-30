package org.openiam.idm.srvc.audit.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
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
public class AuditLogServiceImpl implements AuditLogService {
    
	@Autowired
    private IdmAuditLogDAO logDAO;
    
	@Autowired
    private IdmAuditLogCustomDAO logAttributeDAO;
	
	@Autowired
    private HashDigest hash;
	
    @Autowired
    private LoginDataService loginManager;
    
    @Autowired
    private SysConfiguration sysConfiguration;
    
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
    
    @Override
    @Transactional
    public void save(final IdmAuditLogEntity log) {
    	if(log != null) {
    		if(StringUtils.isBlank(log.getId())) {
    			prepare(log);
    			//logDAO.save(log);
    		}
    	}
    }
    
    private void prepare(final IdmAuditLogEntity log) {
    	log.setHash(hash.HexEncodedHash(log.concat()));
    	log.setTimestamp(new Date());
    	log.setNodeIP(nodeIP);
    }

    //TODO:  put on JMS queue
	@Override
	public void enqueue(IdmAuditLogEntity log) {
		// TODO Auto-generated method stub
		
	}
}
