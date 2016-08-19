package org.openiam.idm.srvc.synch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.provision.type.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.AbstractProvisioningService;
import org.openiam.provision.service.ProvisionServiceUtil;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class PolicyMapTransformScript extends AbstractTransformScript {

    protected static final Log log = LogFactory.getLog(PolicyMapTransformScript.class);

    private List<AttributeMap> attrMap;

    public PolicyMapTransformScript() {
    }

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
        System.out.println("Synching object with Policy Maps for: " + rowObj);

        pUser.setStatus(UserStatusEnum.ACTIVE);

        // this configure the loading Pre/Post groovy scrips, should be switch off for performance
        pUser.setSkipPostProcessor(true);
        pUser.setSkipPreprocessor(true);

        // Add default role
//        if(userRoleList == null) {
//            userRoleList = new LinkedList<Role>();
//        }
//        final RoleEntity role = ((RoleDataService)context.getBean("roleDataService")).getRoleByName("End User", null);
//        final RoleDozerConverter roleDozerConverter = ((RoleDozerConverter)context.getBean("roleDozerConverter"));
//        final Role r = roleDozerConverter.convertToDTO(role, false);
//        userRoleList.add(r);
//
//        if (userRoleList != null) {
//        	userRoleList.forEach(e -> {
//        		pUser.addRole(e, null);
//        	});
//        }

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

        ScriptIntegration scriptRunner = SpringContextProvider.getBean("configurableGroovyScriptEngine", ScriptIntegration.class);
        ProvisionServiceUtil provisionServiceUtil = SpringContextProvider.getBean("provisionServiceUtil", ProvisionServiceUtil.class);
        if (attrMap != null) {
            Map<String, Attribute> columnMap = rowObj.getColumnMap();
            for (AttributeMap am : attrMap) {

                if ("INACTIVE".equalsIgnoreCase(am.getStatus())) {
                    continue;
                }
                Attribute attribute = columnMap.get(am.getName());
                try {
                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                        bindingMap.put("objectType", am.getMapForObjectType());
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
                        provisionServiceUtil.setValueFromAttrMap(am, bindingMap, scriptRunner);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
