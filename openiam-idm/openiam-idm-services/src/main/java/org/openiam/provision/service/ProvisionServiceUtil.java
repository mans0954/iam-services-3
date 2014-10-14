package org.openiam.provision.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.script.ScriptIntegration;

public class ProvisionServiceUtil {
    public static Object getOutputFromAttrMap(AttributeMap attr,
                                              Map<String, Object> tmpMap, ScriptIntegration se)
            throws ScriptEngineException {
        Object output = "";
        Map<String, Object> bindingMap = new HashMap<String, Object>(tmpMap);
        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_MAP, attr);
        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_DEFAULT_VALUE, attr.getDefaultValue());

        if (attr.getReconResAttribute().getAttributePolicy() != null) {
            Policy policy = attr.getReconResAttribute().getAttributePolicy();
            String url = policy.getRuleSrcUrl();
            if (url != null) {
                output = se.execute(bindingMap, url);
            }
        } else if (attr.getReconResAttribute().getDefaultAttributePolicy() != null) {
            output = attr.getReconResAttribute().getDefaultAttributePolicy()
                    .getDefaultAttributeMapId();
        }
        return output;
    }

    public static Object getOutputFromAttrMap(AttributeMapEntity attr,
                                              Map<String, Object> tmpMap, ScriptIntegration se)
            throws ScriptEngineException {
        Object output = "";
        Map<String, Object> bindingMap = new HashMap(tmpMap);
        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_MAP, attr);
        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_DEFAULT_VALUE, attr.getDefaultValue());

        if (attr.getReconResAttribute().getAttributePolicy() != null) {
            PolicyEntity policy = attr.getReconResAttribute()
                    .getAttributePolicy();
            String url = policy.getRuleSrcUrl();
            if (url != null) {
                output = se.execute(bindingMap, url);
            }
        } else if (attr.getReconResAttribute().getDefaultAttributePolicy() != null) {
            output = attr.getReconResAttribute().getDefaultAttributePolicy()
                    .getDefaultAttributeMapId();
        }
        return output;
    }

    /**
     * Generate the principalName for a targetSystem
     *
     * @return
     * @throws ScriptEngineException
     */
    public static String buildUserPrincipalName(List<AttributeMap> attrMap,
                                                ScriptIntegration se, Map<String, Object> bindingMap)
            throws ScriptEngineException {
        for (AttributeMap attr : attrMap) {
            if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())
                    && !"INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                return (String) ProvisionServiceUtil.getOutputFromAttrMap(attr,
                        bindingMap, se);
            }
        }
        return null;
    }

    /**
     * Generate the principalName for a targetSystem
     *
     * @return
     * @throws ScriptEngineException
     */
    public static String buildGroupPrincipalName(List<AttributeMap> attrMap,
                                                ScriptIntegration se, Map<String, Object> bindingMap)
            throws ScriptEngineException {
        for (AttributeMap attr : attrMap) {
            if (PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())
                    && !"INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                return (String) ProvisionServiceUtil.getOutputFromAttrMap(attr,
                        bindingMap, se);
            }
        }
        return null;
    }
}