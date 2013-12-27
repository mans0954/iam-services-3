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
    public AttributeMap getAttributeMap(String attributeId) throws Exception;

    public List<AttributeMap> getAttributeMapCollection(String resourceId) throws Exception;

    public AttributeMap addAttributeMap(AttributeMap attribute) throws Exception;

    public void addAttributeMapCollection(List<AttributeMap> attributeList) throws Exception;

    public AttributeMap updateAttributeMap(AttributeMap attribute) throws Exception;

    public void removeAttributeMap(String attributeId) throws Exception;

    public int removeResourceAttributeMaps(String resourceId) throws Exception;

    public List<Attribute> getSSOAttributes(String resourceId, String principalName, String managedSysId);
}
