/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.StringUtils;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.base.BaseAttributeContainer;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Builds a list of attributes that are to be sent to the connectors. This list
 * can be generated from the groovy scripts that contain rules for provisioning
 * or by sending in the complete User object.
 * 
 * @author suneet
 */
@Component
public class AttributeListBuilder {

    protected static final Log log = LogFactory
            .getLog(AttributeListBuilder.class);

    public ExtensibleUser buildFromRules(ProvisionUser pUser,
            List<AttributeMap> attrMap, ScriptIntegration se,
            String managedSysId, Map<String, Object> bindingMap, String createdBy) {

        final ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

            if (log.isDebugEnabled()) {
                log.debug("buildFromRules: attrMap IS NOT null");
            }

            final Login identity = new Login();

            // init values
            identity.setManagedSysId(managedSysId);

            for (final AttributeMap attr : attrMap) {

                if (StringUtils.equalsIgnoreCase(attr.getStatus(), "INACTIVE")) {
                    continue;
                }

                boolean scripExistsForAttr = false;

                Object output = null;
                try {
                    output = ProvisionServiceUtil.getOutputFromAttrMap(attr,
                            bindingMap, se);
                } catch (ScriptEngineException ex) {
                    log.error(ex);
                }
                if (output != null) {

                    scripExistsForAttr = true;

                    final String objectType = attr.getMapForObjectType();
                    if (objectType != null) {
                        if (StringUtils.equalsIgnoreCase(PolicyMapObjectTypeOptions.PRINCIPAL.name(),
                                objectType)) {
                            if (log.isDebugEnabled()) {
                                log.debug(String
                                        .format("buildFromRules: ManagedSysId=%s, login=%s",
                                                managedSysId, output));
                            }

                            identity.setLogin((String) output);
                            extUser.setPrincipalFieldName(attr
                                    .getAttributeName());
                            extUser.setPrincipalFieldDataType(attr
                                    .getDataType().getValue());

                        }

                        if (StringUtils.equalsIgnoreCase(objectType, "USER")
                                || StringUtils.equalsIgnoreCase(objectType,
                                        "PASSWORD")) {

                            if (log.isDebugEnabled()) {
                                log.debug(String.format(
                                        "buildFromRules: attribute: %s->%s",
                                        attr.getAttributeName(), output));
                            }

                            if (output instanceof String) {

                                output = (StringUtils.isBlank((String) output)) ? attr
                                        .getDefaultValue() : output;
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr
                                                .getAttributeName(),
                                                (String) output, 1, attr
                                                        .getDataType().getValue()));

                            } else if (output instanceof Date) {
                                final Date d = (Date) output;
                                final String DATE_FORMAT = "MM/dd/yyyy";
                                final SimpleDateFormat sdf = new SimpleDateFormat(
                                        DATE_FORMAT);
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr
                                                .getAttributeName(), sdf
                                                .format(d), 1, attr
                                                .getDataType().getValue()));

                            } else if (output instanceof BaseAttributeContainer) {

                                // process a complex object which can be passed
                                // to the connector

                                ExtensibleAttribute newAttr = new ExtensibleAttribute(
                                        attr.getAttributeName(),
                                        (BaseAttributeContainer) output, 1,
                                        attr.getDataType().getValue());
                                newAttr.setObjectType(objectType);
                                extUser.getAttributes().add(newAttr);

                            } else {
                                extUser.getAttributes().add(
                                        new ExtensibleAttribute(attr
                                                .getAttributeName(),
                                                (List) output, 1, attr
                                                        .getDataType().getValue()));
                            }
                        }

                    }
                }
                if (!scripExistsForAttr) {
                    extUser.getAttributes().add(
                            new ExtensibleAttribute(attr.getAttributeName(),
                                    attr.getDefaultValue(), 1, attr
                                            .getDataType().getValue()));
                }

            }
            identity.setAuthFailCount(0);
            identity.setCreateDate(new Date(System.currentTimeMillis()));
            identity.setCreatedBy(createdBy);
            identity.setIsLocked(0);
            identity.setFirstTimeLogin(1);
            identity.setStatus(LoginStatusEnum.ACTIVE);
            if (pUser.getPrincipalList() == null) {
                List<Login> idList = new ArrayList<Login>();
                idList.add(identity);
                pUser.setPrincipalList(idList);
            } else {
                pUser.getPrincipalList().add(identity);
            }

        } else {
            log.debug("- attMap IS null");
        }

        // show the identities in the pUser object

        return extUser;

    }



    public Login buildIdentity(List<AttributeMap> attrMap,
            ScriptIntegration se, String managedSysId, Map<String, Object> bindingMap, String createdBy) {

        Login newIdentity = new Login();

        for (AttributeMap attr : attrMap) {
            String objectType = attr.getMapForObjectType();
            if (objectType != null) {
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {
                    try {
                        String output = (String)ProvisionServiceUtil
                                .getOutputFromAttrMap(attr, bindingMap, se);
                        newIdentity.setLogin(output);
                    } catch (ScriptEngineException ex) {
                        log.error(ex);
                    }
                }
                if (objectType.equalsIgnoreCase("PASSWORD")) {
                    try {
                        String output = (String)ProvisionServiceUtil
                                .getOutputFromAttrMap(attr, bindingMap, se);
                        newIdentity.setPassword(output);
                    } catch (ScriptEngineException ex) {
                        log.error(ex);
                    }
                }
            }

        }
        if (newIdentity.getLogin() == null) {
            return null;
        }
        newIdentity.setManagedSysId(managedSysId);
        newIdentity.setAuthFailCount(0);
        newIdentity.setCreateDate(new Date(System.currentTimeMillis()));
        newIdentity.setFirstTimeLogin(0);
        newIdentity.setIsLocked(0);
        newIdentity.setStatus(LoginStatusEnum.ACTIVE);
        newIdentity.setCreatedBy(createdBy);
        return newIdentity;

    }

    public ExtensibleUser buildModifyFromRules(ProvisionUser pUser,
            Login currentIdentity, List<AttributeMap> attrMap,
            ScriptIntegration se, String managedSysId, Map<String, Object> bindingMap, String createdBy) {

        ExtensibleUser extUser = new ExtensibleUser();

        if (attrMap != null) {

            log.debug("buildFromRules: attrMap IS NOT null");

            for (AttributeMap attr : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(attr.getStatus())) {
                    continue;
                }

                try {
                    Object output = ProvisionServiceUtil.getOutputFromAttrMap(
                            attr, bindingMap, se);
                    if (output != null) {
                        String objectType = attr.getMapForObjectType();
                        if (objectType != null) {

                            log.debug("buildFromRules: OBJECTTYPE="
                                    + objectType + " SCRIPT OUTPUT=" + output
                                    + " attribute name="
                                    + attr.getAttributeName());

                            if (objectType.equalsIgnoreCase("USER")
                                    || objectType.equalsIgnoreCase("PASSWORD")) {

                                ExtensibleAttribute newAttr;
                                if (output instanceof String) {

                                    // if its memberOf object than dont add
                                    // it to the list
                                    // the connectors can detect a delete if
                                    // an attribute is not in the list

                                    newAttr = new ExtensibleAttribute(
                                            attr.getAttributeName(),
                                            (String) output, 1,
                                            attr.getDataType().getValue());
                                    newAttr.setObjectType(objectType);
                                    extUser.getAttributes().add(newAttr);

                                } else if (output instanceof Date) {
                                    // date
                                    Date d = (Date) output;
                                    String DATE_FORMAT = "MM/dd/yyyy";
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            DATE_FORMAT);

                                    newAttr = new ExtensibleAttribute(
                                            attr.getAttributeName(),
                                            sdf.format(d), 1,
                                            attr.getDataType().getValue());
                                    newAttr.setObjectType(objectType);

                                    extUser.getAttributes().add(newAttr);
                                } else if (output instanceof BaseAttributeContainer) {
                                    // process a complex object which can be
                                    // passed to the connector
                                    newAttr = new ExtensibleAttribute(
                                            attr.getAttributeName(),
                                            (BaseAttributeContainer) output, 1,
                                            attr.getDataType().getValue());
                                    newAttr.setObjectType(objectType);
                                    extUser.getAttributes().add(newAttr);

                                } else {
                                    // process a list - multi-valued object
                                    newAttr = new ExtensibleAttribute(
                                            attr.getAttributeName(),
                                            (List) output, 1,
                                            attr.getDataType().getValue());
                                    newAttr.setObjectType(objectType);

                                    extUser.getAttributes().add(newAttr);

                                    log.debug("buildFromRules: added attribute to extUser:"
                                            + attr.getAttributeName());
                                }

                            } else if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(objectType)) {

                                extUser.setPrincipalFieldName(attr
                                        .getAttributeName());
                                extUser.setPrincipalFieldDataType(attr
                                        .getDataType().getValue());

                            }
                        }
                    }
                } catch (ScriptEngineException e) {
                    log.error(e);
                }
            }

            if (pUser.getPrincipalList() == null) {
                List<Login> principalList = new ArrayList<Login>();
                principalList.add(currentIdentity);
                pUser.setPrincipalList(principalList);
            } else {
                pUser.getPrincipalList().add(currentIdentity);
            }

        }

        return extUser;

    }

}
