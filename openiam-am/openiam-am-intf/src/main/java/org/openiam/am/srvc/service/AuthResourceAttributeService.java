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

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */

    public List<AuthResourceAttributeMapEntity> getAttributeMapList(String providerId) ;
    
    public AuthResourceAttributeMapEntity getAttribute(String id);

    public void saveAttributeMap(AuthResourceAttributeMapEntity attribute);

    public void removeAttributeMap(String attributeMapId);

    public List<SSOAttribute> getSSOAttributes(String providerId, String userId);

}
