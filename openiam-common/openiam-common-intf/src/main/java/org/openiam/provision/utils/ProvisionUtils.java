package org.openiam.provision.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.response.SearchResponse;
import org.openiam.provision.PostProcessor;
import org.openiam.provision.PreProcessor;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.request.LookupRequest;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 16/08/16.
 */
public class ProvisionUtils {
    private static final Log log = LogFactory.getLog(ProvisionUtils.class);

    /**
     * Update the list of attributes with the correct operation values so that
     * they can be passed to the connector
     */
    public static ExtensibleObject updateAttributeList(org.openiam.provision.type.ExtensibleObject extObject,
                                                Map<String, ExtensibleAttribute> currentValueMap) {
        if (extObject == null) {
            return null;
        }
        if(log.isDebugEnabled()) {
            log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");
        }

        List<ExtensibleAttribute> extAttrList = extObject.getAttributes();
        if (extAttrList == null) {
            if(log.isDebugEnabled()) {
                log.debug("Extended object attributes is null");
            }
            return null;
        }

        if (currentValueMap == null) {
            for (ExtensibleAttribute attr : extAttrList) {
                if(attr.getOperation() == -1) {
                    attr.setOperation(AttributeOperationEnum.ADD.getValue());
                }
            }
        } else {
            for (ExtensibleAttribute attr : extAttrList) {
                if(attr.getOperation() == -1) {
                    String nm = attr.getName();
                    ExtensibleAttribute curAttr = currentValueMap.get(nm);
                    attr.setOperation(AttributeOperationEnum.NO_CHANGE.getValue());
                    if (attr.valuesAreEqual(curAttr)) {
                        if(log.isDebugEnabled()) {
                            log.debug("- Op = 0 - AttrName = " + nm);
                        }
                        attr.setOperation(AttributeOperationEnum.NO_CHANGE.getValue());
                    } else if (curAttr == null || !curAttr.containsAnyValue()) {
                        if(log.isDebugEnabled()) {
                            log.debug("- Op = 1 - AttrName = " + nm);
                        }
                        attr.setOperation(AttributeOperationEnum.ADD.getValue());
                    } else if (!attr.containsAnyValue() && curAttr.containsAnyValue()) {
                        if(log.isDebugEnabled()) {
                            log.debug("- Op = 3 - AttrName = " + nm);
                        }
                        attr.setOperation(AttributeOperationEnum.DELETE.getValue());
                    } else if (attr.containsAnyValue() && curAttr.containsAnyValue()) {
                        if(log.isDebugEnabled()) {
                            log.debug("- Op = 2 - AttrName = " + nm);
                        }
                        attr.setOperation(AttributeOperationEnum.REPLACE.getValue());
                    }
                }
            }
        }
        return extObject;
    }


    public static int executePreProcess(PreProcessor<ProvisionUser> ppScript,
                                        Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, LookupRequest lookupRequest, String operation) {
        log.info("======= call PreProcessor: ppScript=" + ppScript + ", operation=" + operation);
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap);
        } else if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap);
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap);
        } else if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap);
        } else if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap);
        } else if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap);
        } else if ("LOOKUP".equalsIgnoreCase(operation)) {
            return ppScript.lookupRequest(lookupRequest);
        }

        return 0;
    }

    public static int executePostProcess(PostProcessor<ProvisionUser> ppScript,
                                         Map<String, Object> bindingMap, ProvisionUser user, PasswordSync passwordSync, SearchResponse searchResponse, String operation, boolean success) {
        log.info("======= call PostProcessor: ppScript=" + ppScript + ", operation=" + operation);
        if ("ADD".equalsIgnoreCase(operation)) {
            return ppScript.add(user, bindingMap, success);
        } else if ("MODIFY".equalsIgnoreCase(operation)) {
            return ppScript.modify(user, bindingMap, success);
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return ppScript.delete(user, bindingMap, success);
        } else if ("SET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.setPassword(passwordSync, bindingMap, success);
        } else if ("RESET_PASSWORD".equalsIgnoreCase(operation)) {
            return ppScript.resetPassword(passwordSync, bindingMap, success);
        } else if ("DISABLE".equalsIgnoreCase(operation)) {
            return ppScript.disable(user, bindingMap, success);
        } else if ("LOOKUP".equalsIgnoreCase(operation)) {
            return ppScript.lookupRequest(searchResponse);
        }
        return 0;
    }
}
