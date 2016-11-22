package org.openiam.idm.srvc.audit.service;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.elasticsearch.dao.AuditLogElasticSearchRepository;
import org.openiam.elasticsearch.dao.GroupElasticSearchRepository;
import org.openiam.elasticsearch.dao.LoginElasticSearchRepository;
import org.openiam.elasticsearch.dao.OrganizationElasticSearchRepository;
import org.openiam.elasticsearch.dao.ResourceElasticSearchRepository;
import org.openiam.elasticsearch.dao.RoleElasticSearchRepository;
import org.openiam.elasticsearch.model.GroupDoc;
import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.elasticsearch.model.ResourceDoc;
import org.openiam.elasticsearch.model.RoleDoc;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for <code>IdmAuditLogDataService</code>. All audit logging activities 
 * persisted through this service.
 */
@Service("auditDataService")
public class AuditLogServiceImpl implements AuditLogService {
    
	
	@Autowired
	private AuditLogElasticSearchRepository auditLogRepo;

    @Autowired
    private RoleElasticSearchRepository roleDAO;

    @Autowired
    private GroupElasticSearchRepository groupDAO;

    @Autowired
    private OrganizationElasticSearchRepository organizationDAO;

    @Autowired
    private ResourceElasticSearchRepository resourceDAO;

    @Autowired
    protected SysConfiguration sysConfiguration;
    
    @Autowired
    private LoginElasticSearchRepository loginDAO;

    private static final Log LOG = LogFactory.getLog(AuditLogServiceImpl.class);

    private String nodeIP = null;

/*    @Autowired
    private IdmAuditLogDozerConverter auditLogDozerConverter;

    @Autowired
    private IdmAuditLogCustomDozerConverter idmAuditLogCustomDozerConverter;*/

    @PostConstruct
    public void init() {
    	try {
    		nodeIP = InetAddress.getLocalHost().getCanonicalHostName();
    	} catch(Throwable e) {
    		LOG.error("Can't get IP Address of this ESB - nodeIP for all audit logs will be set to null", e);
    	}
    }
    
    private IdmAuditLogEntity prepare(final IdmAuditLogEntity auditLogEntity) {
        if(auditLogEntity != null) {
        	auditLogEntity.setTimestamp(new Date(System.currentTimeMillis()));
    		if(auditLogEntity.getId() == null || auditLogEntity.getHash() == null) {
                auditLogEntity.setHash(DigestUtils.sha256Hex(auditLogEntity.concat()));
            }
    		if(auditLogEntity.getId() == null) {
    			auditLogEntity.setId(UUIDGen.getUUID());
    		}
            auditLogEntity.setNodeIP(nodeIP);

    		if(CollectionUtils.isNotEmpty(auditLogEntity.getChildLogs())) {
    			auditLogEntity.getChildLogs().forEach(child -> {
    				prepare(child);
    			});
    		}

    		if(CollectionUtils.isNotEmpty(auditLogEntity.getTargets())) {
    			for(final AuditLogTargetEntity target : auditLogEntity.getTargets()) {
    				target.setLogId(auditLogEntity.getId());
                    if(StringUtils.isNotEmpty(target.getTargetId()) && StringUtils.isEmpty(target.getObjectPrincipal())) {
                        if(AuditTarget.USER.value().equals(target.getTargetType())) {
                            final List<LoginEntity> principals = loginDAO.findByUserId(target.getTargetId());
                            final LoginEntity loginEntity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), principals);
                            if(loginEntity != null) {
                            	target.setObjectPrincipal(loginEntity.getLogin());
                            }
                        } else if(AuditTarget.ROLE.value().equals(target.getTargetType())) {
                        	final RoleDoc role = roleDAO.findOne(target.getTargetId());
                        	if(role != null) {
                        		target.setObjectPrincipal(role.getName());
                        	}
                        } else if(AuditTarget.GROUP.value().equals(target.getTargetType())) {
                        	final GroupDoc role = groupDAO.findOne(target.getTargetId());
                        	if(role != null) {
                        		target.setObjectPrincipal(role.getName());
                        	}
                        } else if(AuditTarget.ORG.value().equals(target.getTargetType())) {
                        	final OrganizationDoc org = organizationDAO.findOne(target.getTargetId());
                        	if(org != null) {
                        		target.setObjectPrincipal(org.getName());
                        	}
                        } else if(AuditTarget.RESOURCE.value().equals(target.getTargetType())) {
                        	final ResourceDoc res = resourceDAO.findOne(target.getTargetId());
                            target.setObjectPrincipal(res.getName());
                        }
                    }
                    //auditLogEntity.addTarget(target.getTargetId(),target.getTargetType(), target.getObjectPrincipal());
    			}

    		}
            if(StringUtils.isEmpty(auditLogEntity.getPrincipal()) && StringUtils.isNotEmpty(auditLogEntity.getUserId())) {
                List<LoginEntity> principals = loginDAO.findByUserId(auditLogEntity.getUserId());
                try {
                	LoginEntity loginEntity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), principals);
                	if (loginEntity != null) {
                		auditLogEntity.setPrincipal(loginEntity.getLogin());
                	}
                } catch(Exception e) {
                	//this will fail when inserting an audit lot during a unit test
                	// if this fails, it's due to a missing managed system, and we have
                	// bigger problems than audit log at that point.
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
        if(CollectionUtils.isNotEmpty(target) ? !target.equals(source) : CollectionUtils.isNotEmpty(source))
            return false;
        return true;
    }

	@Override
	@Transactional(readOnly=true)
	public List<IdmAuditLogEntity> findBeans(final AuditLogSearchBean searchBean, final int from, final int size) {
		final int page = Math.floorDiv(from, size);
		final Page<IdmAuditLogEntity> pageResponse = auditLogRepo.find(searchBean, new PageRequest(page, size));
		
		return (pageResponse != null) ? pageResponse.getContent() : null;
	}

    @Override
    @Transactional(readOnly=true)
    public List<String> findIDs(AuditLogSearchBean searchBean, int from, int size) {
    	return auditLogRepo.findIds(searchBean, from, size);
    }

    @Override
	@Transactional(readOnly=true)
	public int count(AuditLogSearchBean searchBean) {
    	return auditLogRepo.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public IdmAuditLogEntity findById(String id) {
        return auditLogRepo.findOne(id);
	}

    @Override
    //@Transactional
    public IdmAuditLogEntity save(IdmAuditLogEntity auditLog) {
        IdmAuditLogEntity auditLogEntity = prepare(auditLog);
        try {
        	auditLogRepo.save(auditLogEntity);
        } catch(Exception ex) {
        	LOG.error("Can't save audit log", ex);
        }
        return auditLogEntity;
    }

}
