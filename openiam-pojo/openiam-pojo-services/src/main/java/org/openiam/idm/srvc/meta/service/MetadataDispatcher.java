package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.*;
import javax.jms.Queue;
import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 7/22/14.
 */
@Component("metadataDispatcher")
public class MetadataDispatcher implements Sweepable {
    private static Logger log = Logger.getLogger(MetadataDispatcher.class);
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
    @Qualifier(value = "metaElementQueue")
    private Queue queue;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    private final Object mutex = new Object();
    @Override
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                synchronized (mutex){

                    final StopWatch sw = new StopWatch();
                    sw.start();
                    try {
                        log.info("Starting metadataElement sweeper thread");

                        Enumeration e = browser.getEnumeration();

                        while (e.hasMoreElements()) {
                            final MetadataElementEntity metadataElementEntity = (MetadataElementEntity)((ObjectMessage) jmsTemplate.receive(queue)).getObject();

                            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                                @Override
                                public Boolean doInTransaction(TransactionStatus status) {
                                    process(metadataElementEntity);
                                    try {
                                        // to give other threads chance to be executed
                                        Thread.sleep(100);
                                    } catch (InterruptedException e1) {
                                        log.warn(e1.getMessage());
                                    }

                                    return true;
                                }});

                            e.nextElement();
                        }

                    } finally {
                        log.info(String.format("Done with metadataElement sweeper thread.  Took %s ms", sw.getTime()));
                    }
                    return null;
                }
            }
        });
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
            List<UserEntity> userList = userManager.findBeans(searchBean);
            if(CollectionUtils.isNotEmpty(userList)){
                for(UserEntity user: userList){
                    Map<String, UserAttributeEntity> userAttributes = user.getUserAttributes();
                    if(userAttributes==null){
                        userManager.addAttribute(buildUserAttribute(user, metadataElementEntity));
                    } else {
                        boolean isFound = false;
                        for(String key: userAttributes.keySet()){
                            UserAttributeEntity attr = userAttributes.get(key);
                            if(attr!=null
                                    && metadataElementEntity.equals(attr.getElement())){
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
                            userManager.addAttribute(buildUserAttribute(user, metadataElementEntity));
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

            List<RoleEntity> roleList = roleDAO.getByExample(searchBean);
            if(CollectionUtils.isNotEmpty(roleList)){
                for(RoleEntity role: roleList){
                    Set<RoleAttributeEntity> roleAttributes = role.getRoleAttributes();
                    if(CollectionUtils.isEmpty(roleAttributes)){
                        roleAttributeDAO.save(buildRoleAttribute(role, metadataElementEntity));
                    } else {
                        boolean isFound = false;
                        for(RoleAttributeEntity attr: roleAttributes){
                            if(metadataElementEntity.equals(attr.getElement())){
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
                            roleAttributeDAO.save(buildRoleAttribute(role, metadataElementEntity));
                        }
                    }
                }
            }
    }



    private void updateGroupAttributes(MetadataElementEntity metadataElementEntity) {
            GroupSearchBean searchBean = new GroupSearchBean();
            searchBean.setType(metadataElementEntity.getMetadataType().getId());

            List<GroupEntity> groupList = groupDAO.getByExample(searchBean);
            if(CollectionUtils.isNotEmpty(groupList)){
                for(GroupEntity group: groupList){
                    Set<GroupAttributeEntity> groupAttributes = group.getAttributes();
                    if(CollectionUtils.isEmpty(groupAttributes)){
                        groupAttributeDAO.save(buildGroupAttribute(group, metadataElementEntity));
                    } else {
                        boolean isFound = false;
                        for(GroupAttributeEntity attr: groupAttributes){
                            if(metadataElementEntity.equals(attr.getElement())){
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
                            groupAttributeDAO.save(buildGroupAttribute(group, metadataElementEntity));
                        }
                    }
                }
            }
    }
    private void updateOrgAttributes(MetadataElementEntity metadataElementEntity) {
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setMetadataType(metadataElementEntity.getMetadataType().getId());

        List<OrganizationEntity> orgList = organizationDAO.getByExample(searchBean);
        if(CollectionUtils.isNotEmpty(orgList)){
            for(OrganizationEntity org: orgList){
                Set<OrganizationAttributeEntity> orgAttributes = org.getAttributes();
                if(CollectionUtils.isEmpty(orgAttributes)){
                    organizationAttributeDAO.save(buildOrgAttribute(org, metadataElementEntity));
                } else {
                    boolean isFound = false;
                    for(OrganizationAttributeEntity attr: orgAttributes){
                        if(metadataElementEntity.equals(attr.getElement())){
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
                        organizationAttributeDAO.save(buildOrgAttribute(org, metadataElementEntity));
                    }
                }
            }
        }
    }
    //TODO
    private void updateResAttributes(MetadataElementEntity metadataElementEntity) {
        ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.setMetadataType(metadataElementEntity.getMetadataType().getId());

        List<ResourceEntity> resList = resourceDAO.getByExample(searchBean);
        if(CollectionUtils.isNotEmpty(resList)){
            for(ResourceEntity res: resList){
                Set<ResourcePropEntity> resAttributes = res.getResourceProps();
                if(CollectionUtils.isEmpty(resAttributes)){
                    resourcePropDAO.save(buildResAttribute(res, metadataElementEntity));
                } else {
                    boolean isFound = false;
                    for(ResourcePropEntity attr: resAttributes){
                        if(metadataElementEntity.equals(attr.getElement())){
                            isFound=true;
                            if(StringUtils.isBlank(attr.getValue())){
                                attr.setValue(metadataElementEntity.getStaticDefaultValue());
                                attr.setElement(metadataElementEntity);
                                resourcePropDAO.merge(attr);
                            }
                        }
                    }
                    if(!isFound){
                        resourcePropDAO.save(buildResAttribute(res, metadataElementEntity));
                    }
                }
            }
        }
    }

    private UserAttributeEntity buildUserAttribute(UserEntity user, MetadataElementEntity metadataElementEntity){
        UserAttributeEntity attribute = new UserAttributeEntity();
        attribute.setUser(user);
        attribute.setElement(metadataElementEntity);
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());

        return attribute;
    }

    private RoleAttributeEntity buildRoleAttribute(RoleEntity role, MetadataElementEntity metadataElementEntity){
        RoleAttributeEntity attribute = new RoleAttributeEntity();
        attribute.setRole(role);
        attribute.setElement(metadataElementEntity);
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }

    private GroupAttributeEntity buildGroupAttribute(GroupEntity group, MetadataElementEntity metadataElementEntity) {
        GroupAttributeEntity attribute = new GroupAttributeEntity();
        attribute.setGroup(group);
        attribute.setElement(metadataElementEntity);
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }
    private OrganizationAttributeEntity buildOrgAttribute(OrganizationEntity org,
                                                          MetadataElementEntity metadataElementEntity) {
        OrganizationAttributeEntity attribute = new OrganizationAttributeEntity();
        attribute.setOrganization(org);
        attribute.setElement(metadataElementEntity);
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }
    private ResourcePropEntity buildResAttribute(ResourceEntity res, MetadataElementEntity metadataElementEntity) {
        ResourcePropEntity attribute = new ResourcePropEntity();
        attribute.setResource(res);
        attribute.setElement(metadataElementEntity);
        attribute.setName(metadataElementEntity.getAttributeName());
        attribute.setValue(metadataElementEntity.getStaticDefaultValue());
        return attribute;
    }

}
