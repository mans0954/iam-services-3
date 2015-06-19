package org.openiam.provision.service;

import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.script.ScriptIntegration;

import java.util.List;
import java.util.Map;

/**
 * Created by zaporozhec on 6/19/15.
 */
public interface ProvisionServiceUtil {
    Object getOutputFromAttrMap(AttributeMap attr,
                                Map<String, Object> tmpMap, ScriptIntegration se)
            throws Exception;

    String buildUserPrincipalName(List<AttributeMap> attrMap,
                                  ScriptIntegration se, Map<String, Object> bindingMap)
            throws Exception;

    String buildGroupPrincipalName(List<AttributeMap> attrMap,
                                   ScriptIntegration se, Map<String, Object> bindingMap)
            throws Exception;
}
