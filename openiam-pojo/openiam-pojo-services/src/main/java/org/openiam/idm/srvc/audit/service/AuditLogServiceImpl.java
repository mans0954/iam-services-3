package org.openiam.idm.srvc.audit.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.dozer.converter.IdmAuditLogCustomDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogDozerConverter;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
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
	private IdmAuditLogDAO logDAO;

    @Autowired
    private LoginDAO loginDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    protected SysConfiguration sysConfiguration;

    private static final Log LOG = LogFactory.getLog(AuditLogServiceImpl.class);

    private String nodeIP = null;

    @Autowired
    private IdmAuditLogDozerConverter auditLogDozerConverter;

    @Autowired
    private IdmAuditLogCustomDozerConverter idmAuditLogCustomDozerConverter;

    @PostConstruct
    public void init() {
    	try {
    		nodeIP = InetAddress.getLocalHost().getCanonicalHostName();
    	} catch(Throwable e) {
    		LOG.error("Can't get IP Address of this ESB - nodeIP for all audit logs will be set to null", e);
    	}
    }
    
    private IdmAuditLogEntity prepare(final IdmAuditLog log) {
//        IdmAuditLogEntity auditLogEntity = log.getId() == null ? auditLogDozerConverter.convertToEntity(log, false) : logDAO.findById(log.getId());
        IdmAuditLogEntity auditLogEntity = auditLogDozerConverter.convertToEntity(log, false);// : logDAO.findById(log.getId());
        auditLogEntity.setTimestamp(new Date(System.currentTimeMillis()));
        if(log != null) {
    		if(auditLogEntity.getId() == null || auditLogEntity.getHash() == null) {
                auditLogEntity.setHash(DigestUtils.sha256Hex(log.concat()));
            }

            auditLogEntity.setNodeIP(nodeIP);

    		if(CollectionUtils.isNotEmpty(log.getChildLogs())) {
    			for(final IdmAuditLog ch : log.getChildLogs()) {
                    IdmAuditLogEntity chEntity = prepare(ch);
                    if(!logExists(auditLogEntity.getChildLogs(), chEntity)) {
//                        System.out.println("ADDING LOG ");
//                    if(!auditLogEntity.getChildLogs().contains(chEntity)) {
                        auditLogEntity.addChild(chEntity);
                        chEntity.addParent(auditLogEntity);
                    } else {
//                        System.out.println("SKIP LOG ");
                    }
    			}
    		}

    		//required - the UI sends a transient instance to the service, so fix it here
    		if(CollectionUtils.isNotEmpty(log.getCustomRecords())) {
                List<IdmAuditLogCustomEntity> auditLogCustomEntities = idmAuditLogCustomDozerConverter.convertToEntityList(new ArrayList<IdmAuditLogCustom>(log.getCustomRecords()), false);
                for(final IdmAuditLogCustomEntity custom : auditLogCustomEntities) {
                    auditLogEntity.addCustomRecord(custom);
    			}
    		}

    		if(CollectionUtils.isNotEmpty(log.getTargets())) {
    			for(final AuditLogTarget target : log.getTargets()) {
                    if(StringUtils.isNotEmpty(target.getTargetId()) && StringUtils.isEmpty(target.getObjectPrincipal())) {
                        if(AuditTarget.USER.value().equals(target.getTargetType())) {
                            final List<LoginEntity> principals = loginDAO.findUser(target.getTargetId());
                            final LoginEntity loginEntity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), principals);
                            if(loginEntity != null) {
                            	target.setObjectPrincipal(loginEntity.getLogin());
                            }
                        } else if(AuditTarget.ROLE.value().equals(target.getTargetType())) {
                        	final RoleEntity role = roleDAO.findById(target.getTargetId());
                            target.setObjectPrincipal(role.getName());
                        } else if(AuditTarget.GROUP.value().equals(target.getTargetType())) {
                        	final GroupEntity role = groupDAO.findById(target.getTargetId());
                            target.setObjectPrincipal(role.getName());
                        } else if(AuditTarget.ORG.value().equals(target.getTargetType())) {
                        	final OrganizationEntity org = organizationDAO.findById(target.getTargetId());
                            target.setObjectPrincipal(org.getName());
                        } else if(AuditTarget.RESOURCE.value().equals(target.getTargetType())) {
                        	final ResourceEntity res = resourceDAO.findById(target.getTargetId());
                            target.setObjectPrincipal(res.getName());
                        }
                    }
                    auditLogEntity.addTarget(target.getTargetId(),target.getTargetType(), target.getObjectPrincipal());
    			}

    		}
            if(StringUtils.isEmpty(log.getPrincipal()) && StringUtils.isNotEmpty(log.getUserId())) {
                List<LoginEntity> principals = loginDAO.findUser(log.getUserId());
                LoginEntity loginEntity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), principals);
                if (loginEntity != null) {
                    auditLogEntity.setPrincipal(loginEntity.getLogin());
                }
            }
            return auditLogEntity;
        }
        return null;
    }

    private boolean logExists(Set<IdmAuditLogEntity> logEntitySet, IdmAuditLogEntity logEntity) {
        if(CollectionUtils.isNotEmpty(logEntitySet)){

            for(IdmAuditLogEntity log : logEntitySet){
                if(log!=null){
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("====================================================\n");
//                    sb.append(String.format("       LOG: %s\n", log));
//                    sb.append(String.format("SOURCE LOG: %s\n", log));
//
//                    sb.append(String.format("LOG.equals(SOURCE LOG): %s\n", log.equals(logEntity)));
//                    sb.append(String.format("logTargetsEquals(LOG, SOURCE LOG): %s\n", logTargetsEquals(log.getTargets(), logEntity.getTargets())));
//
//                    sb.append("====================================================\n");
//
//                    System.out.println(sb.toString());

                    if(log.equals(logEntity)
                        && logTargetsEquals(log.getTargets(), logEntity.getTargets())) {
//                        System.out.println("LOG EXISTS");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean logTargetsEquals(Set<AuditLogTargetEntity> target, Set<AuditLogTargetEntity> source) {
        return !(CollectionUtils.isNotEmpty(target) ? !target.equals(source) : CollectionUtils.isNotEmpty(source));
    }

    @Override
	public void enqueue(final IdmAuditLog event) {
        if(event != null){
		    send(event);
        }
	}
	
	 private void send(final IdmAuditLog log) {
		 jmsTemplate.send("logQueue", new MessageCreator() {
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
    public List<String> findIDs(AuditLogSearchBean searchBean, int from, int size) {
        return logDAO.getIDsByExample(searchBean, from, size);
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
    public IdmAuditLog save(IdmAuditLog auditLog) {

        IdmAuditLogEntity auditLogEntity = prepare(auditLog);
        try {
            if (StringUtils.isNotEmpty(auditLogEntity.getId())) {
                logDAO.merge(auditLogEntity);
            } else {
                logDAO.persist(auditLogEntity);
            }
        } catch(Exception ex) {
        	LOG.error("Can't save audit log", ex);
        }
        final String id = auditLogEntity.getId();
        return auditLogDozerConverter.convertToDTO(auditLogEntity,true);
    }

}
