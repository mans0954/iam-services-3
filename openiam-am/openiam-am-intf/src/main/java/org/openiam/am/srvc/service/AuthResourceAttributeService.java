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
    AuthResourceAMAttributeEntity getAmAttribute(String attributeId);

    List<AuthResourceAMAttributeEntity> getAmAttributeList();

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */

    List<AuthResourceAttributeMapEntity> getAttributeMapList(String providerId) ;
    
    AuthResourceAttributeMapEntity getAttribute(String id);

    void saveAttributeMap(AuthResourceAttributeMapEntity attribute);

    void removeAttributeMap(String attributeMapId);

    List<SSOAttribute> getSSOAttributes(String providerId, String userId);

}
