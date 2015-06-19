package org.openiam.idm.srvc.synch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.script.ScriptIntegration;

import java.util.*;

public class PolicyMapTransformScript extends AbstractTransformScript {

    protected static final Log log = LogFactory.getLog(PolicyMapTransformScript.class);

    private List<AttributeMap> attrMap;

    public PolicyMapTransformScript(List<AttributeMap> attrMap) {
        this.attrMap = attrMap;
    }
    @Override
    public int execute(LineObject rowObj, ProvisionUser pUser) {

        System.out.println("Is New User: " + isNewUser);
        if (!isNewUser) {
            System.out.println("User Object: " + user);
            System.out.println("Principal List: " + principalList);
            System.out.println("User Roles: " + userRoleList);
        }
        System.out.println("---------------------------------");
        System.out.println("Synching object with Policy Maps for: " + rowObj );

        pUser.setStatus(UserStatusEnum.ACTIVE);

        // this configure the loading Pre/Post groovy scrips, should be switch off for performance
        pUser.setSkipPostProcessor(true);
        pUser.setSkipPreprocessor(true);

        // Add default role
        if(userRoleList == null) {
            userRoleList = new LinkedList<Role>();
        }
        final RoleEntity role = ((RoleDataService)context.getBean("roleDataService")).getRoleByName("End User", null);
        final RoleDozerConverter roleDozerConverter = ((RoleDozerConverter)context.getBean("roleDozerConverter"));
        final Role r = roleDozerConverter.convertToDTO(role, false);
        userRoleList.add(r);

        if (userRoleList != null) {
        	userRoleList.forEach(e -> {
        		pUser.addRole(e, null);
        	});
        }

        populateUser(rowObj, pUser);

        return TransformScript.NO_DELETE;
    }

    private void populateUser(LineObject rowObj, ProvisionUser pUser) {
        if (isNewUser) {
            pUser.setId(null);
        }
        if (principalList == null) {
            principalList = new ArrayList<Login>();
        }
        ScriptIntegration scriptRunner = (ScriptIntegration)context.getBean("configurableGroovyScriptEngine");
        if (attrMap != null) {
            Map<String,Attribute> columnMap =  rowObj.getColumnMap();
            for (AttributeMap am : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(am.getStatus())) {
                    continue;
                }

                Attribute attribute = columnMap.get(am.getName());
                try {
                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                    Policy policy = (am.getReconResAttribute() != null) ?
                            am.getReconResAttribute().getAttributePolicy() : null;

                    if (policy != null) {
                        bindingMap.put("objectType", am.getMapForObjectType());
                        bindingMap.put("policy", policy);
                        bindingMap.put("rowObj", rowObj);
                        bindingMap.put("attributeName", am.getName());
                        bindingMap.put("attribute", attribute);
                        bindingMap.put("pUser", pUser);
                        bindingMap.put("user", user);
                        bindingMap.put("principalList", principalList);
                        bindingMap.put("userRoleList", userRoleList);
                        bindingMap.put("isNewUser", isNewUser);
                        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_MAP, am);
                        bindingMap.put(AbstractProvisioningService.ATTRIBUTE_DEFAULT_VALUE, am.getDefaultValue());

                        SynchServiceUtil.setUserDataFromPolicy(policy, bindingMap, scriptRunner);

                    }

                } catch (ScriptEngineException e) {
                    log.error(e);
                }
            }
        }
    }

    @Override
    public void init() {}
}
