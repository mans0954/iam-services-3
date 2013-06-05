package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.dto.SSOAttribute;

import java.util.List;

public interface AuthResourceAttributeService {
    /*
    *==================================================
    * AuthResourceAMAttribute section
    *===================================================
    */
    public AuthResourceAMAttributeEntity getAmAttribute(String attributeId);

    public List<AuthResourceAMAttributeEntity> getAmAttributeList();

    public AuthResourceAMAttributeEntity saveAmAttribute(AuthResourceAMAttributeEntity attribute);

    public void deleteAmAttribute(String attributeId);

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */
    public AuthResourceAttributeMapEntity getAttributeMap(String attributeMapId) ;

    public List<AuthResourceAttributeMapEntity> getAttributeMapList(String providerId) ;

    public AuthResourceAttributeMapEntity saveAttributeMap(AuthResourceAttributeMapEntity attribute) throws Exception;

    public void saveAttributeMapCollection(List<AuthResourceAttributeMapEntity> attributeList) throws Exception;

    public void removeAttributeMap(String attributeMapId) throws Exception;

    public void removeAttributeMaps(String providerId) throws Exception;

    public List<SSOAttribute> getSSOAttributes(String providerId, String userId);

}
