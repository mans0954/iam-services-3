package org.openiam.idm.srvc.audit.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.util.encrypt.HashDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for <code>IdmAuditLogDataService</code>. All audit logging activities 
 * persisted through this service.
 */
@Service("auditDataService")
public class AuditLogServiceImpl implements AuditLogService {
    
	@Autowired
    private JmsTemplate jmsTemplate;
	
	@Autowired
    @Qualifier(value = "logQueue")
    private Queue queue;
	
	@Autowired
    private HashDigest hash;
	
	@Autowired
	private IdmAuditLogDAO logDAO;
    
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
    		
    		//required - the UI sends a transient instance to the service, so fix it here
    		if(CollectionUtils.isNotEmpty(log.getCustomRecords())) {
    			for(final IdmAuditLogCustomEntity entity : log.getCustomRecords()) {
    				entity.setLog(log);
    			}
    		}
    		
    		if(CollectionUtils.isNotEmpty(log.getTargets())) {
    			for(final AuditLogTargetEntity entity : log.getTargets()) {
    				entity.setLog(log);
    			}
    		}
    	}
    }

	@Override
	public void enqueue(final AuditLogBuilder builder) {
        if(builder!=null){
		    final IdmAuditLogEntity log = builder.getEntity();
		    prepare(log, UUIDGen.getUUID());
		    send(log);
        }
	}
	
	 private void send(final IdmAuditLogEntity log) {
		 jmsTemplate.send(queue, new MessageCreator() {
			 public javax.jms.Message createMessage(Session session) throws JMSException {
				 javax.jms.Message message = session.createObjectMessage(log);
				 return message;
			 }
		 });
	 }

	@Override
	@Transactional(readOnly=true)
	public List<IdmAuditLogEntity> findBeans(AuditLogSearchBean searchBean,
			int from, int size) {
		return logDAO.getByExample(searchBean, from, size);
	}

	@Override
	@Transactional(readOnly=true)
	public int count(AuditLogSearchBean searchBean) {
		return logDAO.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public IdmAuditLogEntity findById(String id) {
		return logDAO.findById(id);
	}
}
