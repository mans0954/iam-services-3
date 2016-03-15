package org.openiam.idm.srvc.meta.service;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.hibernate.HibernateUtils;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupAttributeDAO;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationAttributeDAO;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourcePropDAO;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleAttributeDAO;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.thread.Sweepable;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by: Alexander Duckardt
 * Date: 7/22/14.
 */
@Component("metadataDispatcher")
public class MetadataDispatcher {
	private static final Log log = LogFactory.getLog(MetadataDispatcher.class);
    @Autowired
    private UserDataService userManager;
    @Autowired
    private RoleAttributeDAO roleAttributeDAO;
    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private GroupAttributeDAO groupAttributeDAO;

    @Autowired
    private OrganizationDAO organizationDAO;
    @Autowired
    private OrganizationAttributeDAO organizationAttributeDAO;

    @Autowired
    private ResourceDAO resourceDAO;
    @Autowired
    private ResourcePropDAO resourcePropDAO;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    private final Object mutex = new Object();
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisMessageListenerContainer listener;
    
    @PostConstruct
    public void init() {
    	listener.addMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message, byte[] pattern) {
				final MetadataElementEntity entity = (MetadataElementEntity)redisTemplate.getDefaultSerializer().deserialize(message.getBody());
				final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean doInTransaction(TransactionStatus status) {
                        process(entity);
                        /*
                         * I'm keeping this commented out here, in memory of our friends at Minsk
                         * 
                        try {
                            // to give other threads chance to be executed
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            log.warn(e1.getMessage());
                        }
						*/
                        return true;
                    }});
			}
		}, Arrays.asList(new Topic[] { new ChannelTopic("metaElementQueue")}));
    }

    private void process(MetadataElementEntity metadataElementEntity){
        if(metadataElementEntity!=null && metadataElementEntity.isRequired()){
            switch(metadataElementEntity.getMetadataType().getGrouping()){
                case USER_OBJECT_TYPE:
                    updateUserAttributes(metadataElementEntity);
                    break;
                case ROLE_TYPE:
                    updateRoleAttributes(metadataElementEntity);
                    break;
                case GROUP_TYPE:
                    updateGroupAttributes(metadataElementEntity);
                    break;
                case ORG_TYPE:
                    updateOrgAttributes(metadataElementEntity);
                    break;
                case RESOURCE_TYPE:
                    updateResAttributes(metadataElementEntity);
                    break;
            }
        }
    }

    private void updateUserAttributes(MetadataElementEntity metadataElementEntity) {
        try {
            UserSearchBean searchBean = new UserSearchBean();
            searchBean.setUserType(metadataElementEntity.getMetadataType().getId());
            List<UserEntity> userList = userManager.findBeans(searchBean,-1,-1);
            if(CollectionUtils.isNotEmpty(userList)){
                for(UserEntity user: userList){
                    Map<String, UserAttributeEntity> userAttributes = user.getUserAttributes();
                    if(userAttributes==null){
                        userManager.addAttribute(AttributeUtil.buildUserAttribute(user, metadataElementEntity));
                    } else {
                        boolean isFound = false;
                        for(String key: userAttributes.keySet()){
                            UserAttributeEntity attr = userAttributes.get(key);
                            if(attr!=null
                                    && metadataElementEntity.equals(HibernateUtils.unproxy(attr.getElement()))){
                                isFound=true;
                                if(StringUtils.isBlank(attr.getValue())
                                   && CollectionUtils.isEmpty(attr.getValues())){
                                    attr.setValue(metadataElementEntity.getStaticDefaultValue());
                                    attr.setUser(user);
                                    attr.setElement(metadataElementEntity);
                                    userManager.updateAttribute(attr);
                                }
                            }
                        }
                        if(!isFound){
                            userManager.addAttribute(AttributeUtil.buildUserAttribute(user, metadataElementEntity));
                        }
                    }
                }
            }
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateRoleAttributes(MetadataElementEntity metadataElementEntity) {
            RoleSearchBean searchBean = new RoleSearchBean();
            searchBean.setType(metadataElementEntity.getMetadataType().getId());
            List<RoleEntity> roleList = roleDAO.getByExample(searchBean,-1,-1);
            if(CollectionUtils.isNotEmpty(roleList)){
                for(RoleEntity role: roleList){
                    Set<RoleAttributeEntity> roleAttributes = role.getRoleAttributes();
                    if(CollectionUtils.isEmpty(roleAttributes)){
                        roleAttributeDAO.save(AttributeUtil.buildRoleAttribute(role, metadataElementEntity));
                    } else {
                        boolean isFound = false;
                        for(RoleAttributeEntity attr: roleAttributes){
                            if(metadataElementEntity.equals(HibernateUtils.unproxy(attr.getElement()))){
                                isFound=true;
                                if(StringUtils.isBlank(attr.getValue())
                                   && CollectionUtils.isEmpty(attr.getValues())){
                                    attr.setValue(metadataElementEntity.getStaticDefaultValue());
                                    attr.setRole(role);
                                    attr.setElement(metadataElementEntity);
                                    roleAttributeDAO.merge(attr);
                                }
                            }
                        }
                        if(!isFound){
                            roleAttributeDAO.save(AttributeUtil.buildRoleAttribute(role, metadataElementEntity));
                        }
                    }
                }
            }
    }



    private void updateGroupAttributes(MetadataElementEntity metadataElementEntity) {
            GroupSearchBean searchBean = new GroupSearchBean();
            searchBean.setType(metadataElementEntity.getMetadataType().getId());

            List<GroupEntity> groupList = groupDAO.getByExample(searchBean,-1,-1);
            if(CollectionUtils.isNotEmpty(groupList)){
                for(GroupEntity group: groupList){
                    Set<GroupAttributeEntity> groupAttributes = group.getAttributes();
                    if(CollectionUtils.isEmpty(groupAttributes)){
                        groupAttributeDAO.save(AttributeUtil.buildGroupAttribute(group, metadataElementEntity));
                    } else {
                        boolean isFound = false;
                        for(GroupAttributeEntity attr: groupAttributes){
                            if(metadataElementEntity.equals(HibernateUtils.unproxy(attr.getElement()))){
                                isFound=true;
                                if(StringUtils.isBlank(attr.getValue())
                                   && CollectionUtils.isEmpty(attr.getValues())){
                                    attr.setValue(metadataElementEntity.getStaticDefaultValue());
                                    attr.setElement(metadataElementEntity);
                                    groupAttributeDAO.merge(attr);
                                }
                            }
                        }
                        if(!isFound){
                            groupAttributeDAO.save(AttributeUtil.buildGroupAttribute(group, metadataElementEntity));
                        }
                    }
                }
            }
    }
    private void updateOrgAttributes(MetadataElementEntity metadataElementEntity) {
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setMetadataType(metadataElementEntity.getMetadataType().getId());

        List<OrganizationEntity> orgList = organizationDAO.getByExample(searchBean,-1,-1);
        if(CollectionUtils.isNotEmpty(orgList)){
            for(OrganizationEntity org: orgList){
                Set<OrganizationAttributeEntity> orgAttributes = org.getAttributes();
                if(CollectionUtils.isEmpty(orgAttributes)){
                    organizationAttributeDAO.save(AttributeUtil.buildOrgAttribute(org, metadataElementEntity));
                } else {
                    boolean isFound = false;
                    for(OrganizationAttributeEntity attr: orgAttributes){
                        if(metadataElementEntity.equals(HibernateUtils.unproxy(attr.getElement()))){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())
                               && CollectionUtils.isEmpty(attr.getValues())){
                                attr.setValue(metadataElementEntity.getStaticDefaultValue());
                                attr.setElement(metadataElementEntity);
                                organizationAttributeDAO.merge(attr);
                            }
                        }
                    }
                    if(!isFound){
                        organizationAttributeDAO.save(AttributeUtil.buildOrgAttribute(org, metadataElementEntity));
                    }
                }
            }
        }
    }
    //TODO
    private void updateResAttributes(MetadataElementEntity metadataElementEntity) {
        ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.setMetadataType(metadataElementEntity.getMetadataType().getId());

        List<ResourceEntity> resList = resourceDAO.getByExample(searchBean,-1,-1);
        if(CollectionUtils.isNotEmpty(resList)){
            for(ResourceEntity res: resList){
                Set<ResourcePropEntity> resAttributes = res.getResourceProps();
                if(CollectionUtils.isEmpty(resAttributes)){
                    resourcePropDAO.save(AttributeUtil.buildResAttribute(res, metadataElementEntity));
                } else {
                    boolean isFound = false;
                    for(ResourcePropEntity attr: resAttributes){
                        if(metadataElementEntity.equals(HibernateUtils.unproxy(attr.getElement()))){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())){
                                attr.setValue(metadataElementEntity.getStaticDefaultValue());
                                attr.setElement(metadataElementEntity);
                                resourcePropDAO.merge(attr);
                            }
                        }
                    }
                    if(!isFound){
                        resourcePropDAO.save(AttributeUtil.buildResAttribute(res, metadataElementEntity));
                    }
                }
            }
        }
    }



}
