package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.am.srvc.dto.Attribute;

import java.util.List;

public interface AuthResourceAttributeService {
    public AuthResourceAttributeEntity getAttributeMap(String attributeId) throws Exception;

    public List<AuthResourceAttributeEntity> getAttributeMapCollection(String resourceId) throws Exception;

    public List<AuthResourceAttributeEntity> getAttributeMapCollection(String resourceId, Integer from, Integer size) throws Exception;

    public List<AuthResourceAttributeEntity> getAttributeMapCollection(AuthResourceAttributeEntity searchBean) throws Exception;

    public List<AuthResourceAttributeEntity> getAttributeMapCollection(AuthResourceAttributeEntity searchBean, Integer from, Integer size) throws Exception;

    public Integer getNumOfAttributeMapList(String resourceId) throws Exception;

    public Integer getNumOfAttributeMapList(AuthResourceAttributeEntity searchBean) throws Exception;

    public AuthResourceAttributeEntity addAttributeMap(AuthResourceAttributeEntity attribute) throws Exception;

    public void addAttributeMapCollection(List<AuthResourceAttributeEntity> attributeList) throws Exception;

    public AuthResourceAttributeEntity updateAttributeMap(AuthResourceAttributeEntity attribute) throws Exception;

    public void removeAttributeMap(String attributeId) throws Exception;

    public int removeResourceAttributeMaps(String resourceId) throws Exception;

    public List<Attribute> getSSOAttributes(String resourceId, String principalName, String securityDomain, String managedSysId, Integer from, Integer size);

}
