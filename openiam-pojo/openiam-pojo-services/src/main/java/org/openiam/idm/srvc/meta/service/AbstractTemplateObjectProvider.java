package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.KeyDTO;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by alexander on 30/12/15.
 */
public abstract class AbstractTemplateObjectProvider<Entity, DTO extends KeyDTO> implements TemplateObjectProvider<Entity, DTO> {
    @Autowired
    @Qualifier("entityValidator")
    private EntityValidator defaultEntityValidator;

    @Autowired
    private MetadataElementDAO elementDAO;


    public Map<String, AbstractAttributeEntity> getAttributeName2ObjectAttributeMap(String objectId){
        final List<? extends AbstractAttributeEntity> attributes = getAttributes(objectId);
        final Map<String, AbstractAttributeEntity> attributeName2UserAttributeMap = new HashMap<>();
        for(final AbstractAttributeEntity attribute : attributes) {
            attributeName2UserAttributeMap.put(attribute.getName(), attribute);
        }
        return attributeName2UserAttributeMap;
    }
    @Override
    public Map<String, List<AbstractAttributeEntity>> getMetadataId2ObjectAttributeMap(String objectId, final Set<String> metadataTypes) {
        final List<? extends AbstractAttributeEntity> attributes = getAttributes(objectId, metadataTypes);
        final Map<String, List<AbstractAttributeEntity>> metadataId2UserAttributeMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(attributes)) {
            for(final AbstractAttributeEntity attribute : attributes) {
                final String elementId = attribute.getElement().getId();
                if(!metadataId2UserAttributeMap.containsKey(elementId)) {
                    metadataId2UserAttributeMap.put(elementId, new LinkedList<AbstractAttributeEntity>());
                }
                metadataId2UserAttributeMap.get(elementId).add(attribute);
            }
        }
        return metadataId2UserAttributeMap;
    }

    public boolean isValid(Entity entity) throws BasicDataServiceException{
        return this.getValidator().isValid(entity);
    }


    protected String getElementName(final MetadataElementEntity entity, final LanguageEntity language) {
        String elementName = null;
        if(entity != null && language != null && MapUtils.isNotEmpty(entity.getLanguageMap())) {
            final LanguageMappingEntity mapping = entity.getLanguageMap().get(language.getId());
            if(mapping != null) {
                elementName = mapping.getValue();
            }
        }
        return elementName;
    }

    protected EntityValidator getValidator(){
        return this.defaultEntityValidator;
    }
    protected abstract List<? extends AbstractAttributeEntity> getAttributes(String objectId);
    protected abstract List<? extends AbstractAttributeEntity> getAttributes(String objectId, final Set<String> metadataTypes);
}
