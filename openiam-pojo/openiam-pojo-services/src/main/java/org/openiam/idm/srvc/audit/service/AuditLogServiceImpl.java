package org.openiam.idm.srvc.audit.service;

import java.net.InetAddress;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.dozer.converter.IdmAuditLogDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogTargetDozerConverter;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
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

    @Autowired
    private IdmAuditLogDozerConverter auditLogDozerConverter;

    /**
     * Cache for UserId and CorrelationId
     *
     * When new User login: new AuditLog with Correlation Id will be generated and store in cache Map
     * Every login we replace CorrelationId by UserId
     *
     */
    private final Map<String, String> correlationIdByRequesterId = new HashMap<>();

    @PostConstruct
    public void init() {
    	try {
    		nodeIP = InetAddress.getLocalHost().getCanonicalHostName();
    	} catch(Throwable e) {
    		LOG.error("Can't get IP Address of this ESB - nodeIP for all audit logs will be set to null", e);
    	}
    }
    
    private void prepare(final IdmAuditLogEntity log) {
    	if(log != null) {
    		if(log.getId() == null || log.getHash() == null) {
                log.setHash(hash.HexEncodedHash(log.concat()));
            }

            if(StringUtils.isEmpty(log.getCorrelationId())) {
               // log.setCorrelationId(String.valueOf(new Random().nextLong()));
            }
    		log.setNodeIP(nodeIP);

    		if(CollectionUtils.isNotEmpty(log.getChildLogs())) {
    			for(final IdmAuditLogEntity entity : log.getChildLogs()) {
                    if(StringUtils.isEmpty(entity.getCorrelationId())) {
                       // log.setCorrelationId(log.getCorrelationId());
                    }
    				prepare(entity);
    			}
    		}

    		//required - the UI sends a transient instance to the service, so fix it here
    		if(CollectionUtils.isNotEmpty(log.getCustomRecords())) {
    			for(final IdmAuditLogCustomEntity custom : log.getCustomRecords()) {
                    custom.setLog(log);
    			}
    		}

    		if(CollectionUtils.isNotEmpty(log.getTargets())) {
    			for(final AuditLogTargetEntity target : log.getTargets()) {
                    target.setLog(log);
    			}
    		}
    	}
    }

	@Override
	public void enqueue(final IdmAuditLog event) {
        if(event != null){
		    send(event);
        }
	}
	
	 private void send(final IdmAuditLog log) {
		 jmsTemplate.send(queue, new MessageCreator() {
			 public javax.jms.Message createMessage(Session session) throws JMSException {
				 javax.jms.Message message = session.createObjectMessage(log);
				 return message;
			 }
		 });
	 }

	@Override
	@Transactional(readOnly=true)
	public List<IdmAuditLog> findBeans(AuditLogSearchBean searchBean,
			int from, int size) {
		List<IdmAuditLogEntity> idmAuditLogEntities = logDAO.getByExample(searchBean, from, size);
        List<IdmAuditLog> idmAuditLogs = new LinkedList<>();
        if(idmAuditLogEntities != null) {
           idmAuditLogs = auditLogDozerConverter.convertToDTOList(idmAuditLogEntities, false);
        }
        return idmAuditLogs;
	}

	@Override
	@Transactional(readOnly=true)
	public int count(AuditLogSearchBean searchBean) {
		return logDAO.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public IdmAuditLog findById(String id) {
        return auditLogDozerConverter.convertToDTO(logDAO.findById(id), true);
	}

    @Override
    @Transactional
    public String save(IdmAuditLog auditLog) {
        IdmAuditLogEntity auditLogEntity = auditLogDozerConverter.convertToEntity(auditLog, true);

        prepare(auditLogEntity);
        try {
            if (StringUtils.isNotEmpty(auditLogEntity.getId())) {
                logDAO.merge(auditLogEntity);
            } else {
                logDAO.persist(auditLogEntity);
            }
        } catch(Exception ex) {
          ex.printStackTrace();
        }
        return auditLogEntity.getId();
    }
/*
    @Override
    @Transactional(readOnly = true)
    public IdmAuditLog getAuditLogByRequesterId(String requesterId) {
        IdmAuditLog auditLog = null;
        if(correlationIdByUserId.containsKey(requesterId)) {
            IdmAuditLogEntity auditLogEntity = logDAO.findByRequesterId(requesterId,correlationIdByUserId.get(requesterId));
            auditLog = auditLogDozerConverter.convertToDTO(auditLogEntity, true);
        }

        return auditLog;
    }*/
}
