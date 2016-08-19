package org.openiam.provision.service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.auth.service.AuthAttributeProcessor;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.provision.type.Attribute;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("provisionServiceUtil")
public class ProvisionServiceUtilImpl implements ProvisionServiceUtil {
    @Autowired
    private AuthAttributeProcessor authAttributeProcessor;

    @Override
    public Object getOutputFromAttrMap(AttributeMap attr,
                                       Map<String, Object> tmpMap, ScriptIntegration se)
            throws Exception {
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
            EnumMap<AmAttributes, Object> objectMap = new EnumMap<AmAttributes, Object>(AmAttributes.class);
            if (tmpMap.get("lg") != null)
                objectMap.put(AmAttributes.Login, tmpMap.get("lg"));
            if (tmpMap.get("user") != null)
                objectMap.put(AmAttributes.User, tmpMap.get("user"));
            output = authAttributeProcessor.process(attr.getReconResAttribute().getDefaultAttributePolicy().getId(), objectMap);
        }
        return output;
    }

    @Override
    public void setValueFromAttrMap(AttributeMap attr,
                                    Map<String, Object> tmpMap, ScriptIntegration se)
            throws Exception {
        Map<String, Object> bindingMap = new HashMap<String, Object>(tmpMap);
        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_MAP, attr);
        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_DEFAULT_VALUE, attr.getDefaultValue());

        if (attr.getReconResAttribute().getAttributePolicy() != null) {
            Policy policy = attr.getReconResAttribute().getAttributePolicy();
            String url = policy.getRuleSrcUrl();
            if (url != null) {
                se.execute(bindingMap, url);
            }
        } else if (attr.getReconResAttribute().getDefaultAttributePolicy() != null) {
            if (bindingMap.get("pUser") != null && bindingMap.get("attribute") != null) {
                ProvisionUser pUser = (ProvisionUser) bindingMap.get("pUser");
                Attribute attribute = (Attribute) bindingMap.get("attribute");
                if (attribute.getValue() != null)
                    authAttributeProcessor.process(attr.getReconResAttribute().getDefaultAttributePolicy().getId(), pUser, attribute.getValue());
            }
        }
    }

    /**
     * Generate the principalName for a targetSystem
     *
     * @return
     * @throws ScriptEngineException
     */
    @Override
    public String buildUserPrincipalName(List<AttributeMap> attrMap,
                                         ScriptIntegration se, Map<String, Object> bindingMap)
            throws Exception {
        for (AttributeMap attr : attrMap) {
            if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())
                    && !"INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                return (String) this.getOutputFromAttrMap(attr,
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
    @Override
    public String buildGroupPrincipalName(List<AttributeMap> attrMap,
                                          ScriptIntegration se, Map<String, Object> bindingMap)
            throws Exception {
        for (AttributeMap attr : attrMap) {
            if (PolicyMapObjectTypeOptions.GROUP_PRINCIPAL.name().equalsIgnoreCase(attr.getMapForObjectType())
                    && !"INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                return (String) this.getOutputFromAttrMap(attr,
                        bindingMap, se);
            }
        }
        return null;
    }
}