package org.openiam.idm.srvc.meta.service;

import org.openiam.base.KeyDTO;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexander on 30/12/15.
 */
public interface TemplateObjectProvider<Entity, DTO extends KeyDTO> {
    public Map<String, List<AbstractAttributeEntity>> getMetadataId2ObjectAttributeMap(String objectId, final Set<String> attributeNames);
    public Map<String, AbstractAttributeEntity> getAttributeName2ObjectAttributeMap(String objectId);

    public AbstractAttributeEntity getNewAttributeInstance(String name, Entity objectId);
    public Entity getEntity(String objectId);
    public String getObjectMetadataTypeId(Entity entity);
    public boolean isValid(Entity entity) throws BasicDataServiceException;
    public boolean isValid(DTO dto) throws BasicDataServiceException;

}
