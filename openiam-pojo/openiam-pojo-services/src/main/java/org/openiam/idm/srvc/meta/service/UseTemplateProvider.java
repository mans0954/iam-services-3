package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserAttributeDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by alexander on 30/12/15.
 */
@Component("useTemplateProvider")
public class UseTemplateProvider extends AbstractTemplateObjectProvider<UserEntity, User> {
    @Autowired
    private UserAttributeDAO attributeDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserDozerConverter userDozerConverter;


    protected List<? extends AbstractAttributeEntity> getAttributes(String objectId, final Set<String> attributeNames){
        return attributeDAO.findUserAttributes(objectId, attributeNames);
    }

    protected List<? extends AbstractAttributeEntity> getAttributes(String objectId){
        return attributeDAO.findUserAttributesLocalized(objectId);
    }

    @Override
    public boolean isValid(User dto) throws BasicDataServiceException {
        UserEntity entity = userDozerConverter.convertToEntity(dto, true);
        return this.isValid(entity);
    }
    @Transactional(readOnly = true)
    public UserEntity getEntity(String objectId) {
        return (objectId != null) ? userDAO.findById(objectId) : new UserEntity();
    }
    @Transactional(readOnly = true)
    public String getObjectMetadataTypeId(UserEntity entity){
        return entity.getType() != null ? entity.getType().getId() : null;
    }
    public AbstractAttributeEntity getNewAttributeInstance(String name, UserEntity entity){
        UserAttributeEntity userAttribute = new UserAttributeEntity();
        userAttribute.setName(name);
        userAttribute.setUserId(entity.getId());
        return userAttribute;
    }
}
