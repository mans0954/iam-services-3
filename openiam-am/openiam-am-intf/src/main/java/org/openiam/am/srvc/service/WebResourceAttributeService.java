package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dto.Attribute;
import org.openiam.am.srvc.dto.AttributeMap;

import java.util.List;

/**
 * User: Alexander Duckardt
 * Date: 8/16/12
 */
@Deprecated
public interface WebResourceAttributeService {
    AttributeMap getAttributeMap(String attributeId) throws Exception;

    List<AttributeMap> getAttributeMapCollection(String resourceId) throws Exception;

    AttributeMap addAttributeMap(AttributeMap attribute) throws Exception;

    void addAttributeMapCollection(List<AttributeMap> attributeList) throws Exception;

    AttributeMap updateAttributeMap(AttributeMap attribute) throws Exception;

    void removeAttributeMap(String attributeId) throws Exception;

    int removeResourceAttributeMaps(String resourceId) throws Exception;

    List<Attribute> getSSOAttributes(String resourceId, String principalName, String managedSysId);
}
