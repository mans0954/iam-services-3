package org.openiam.idm.srvc.meta.service;

import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupAttributeDAO;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by alexander on 30/12/15.
 */
@Component("groupTemplateProvider")
public class GroupTemplateProvider extends AbstractTemplateObjectProvider<GroupEntity, Group> {
    @Autowired
    @Qualifier("groupEntityValidator")
    private EntityValidator groupEntityValidator;

    @Autowired
    private GroupDozerConverter groupDozerConverter;
    @Autowired
    private GroupDAO groupDao;
    @Autowired
    private GroupAttributeDAO attributeDAO;

    protected EntityValidator getValidator(){
        return this.groupEntityValidator;
    }

    protected List<? extends AbstractAttributeEntity> getAttributes(String objectId, final Set<String> attributeNames){
        return attributeDAO.findGroupAttributes(objectId, attributeNames);
    }

    protected List<? extends AbstractAttributeEntity> getAttributes(String objectId){
        return attributeDAO.findGroupAttributes(objectId);
    }

    @Override
    public boolean isValid(Group dto) throws BasicDataServiceException {
        GroupEntity entity = groupDozerConverter.convertToEntity(dto, true);
        return this.isValid(entity);
    }
    @Transactional(readOnly = true)
    public GroupEntity getEntity(String objectId) {
        return (objectId != null) ? groupDao.findById(objectId) : new GroupEntity();
    }
    @Transactional(readOnly = true)
    public String getObjectMetadataTypeId(GroupEntity entity){
        return entity.getType() != null ? entity.getType().getId() : null;
    }

    public AbstractAttributeEntity getNewAttributeInstance(String name, GroupEntity entity){
        GroupAttributeEntity groupAttribute = new GroupAttributeEntity();
        groupAttribute.setName(name);
        groupAttribute.setGroup(entity);
        return groupAttribute;
    }
}
