package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.exception.BasicDataServiceException;

import java.util.List;

public interface AuthResourceAttributeService {
    /*
    *==================================================
    * AuthResourceAMAttribute section
    *===================================================
    */
    AuthResourceAMAttributeEntity getAmAttribute(String attributeId);

    List<AuthResourceAMAttribute> getAmAttributeList();

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */

    List<AuthResourceAttributeMapEntity> getAttributeMapList(String providerId) ;
    
    AuthResourceAttributeMap getAttribute(String id);

    String saveAttributeMap(AuthResourceAttributeMap attribute) throws BasicDataServiceException;

    void removeAttributeMap(String attributeMapId) throws BasicDataServiceException;

    List<SSOAttribute> getSSOAttributes(String providerId, String userId);

}
