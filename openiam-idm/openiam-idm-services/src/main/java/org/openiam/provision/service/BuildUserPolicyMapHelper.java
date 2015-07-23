package org.openiam.provision.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BuildUserPolicyMapHelper {

    @Autowired
    private UserDozerConverter userDozerConverter;

    @Autowired
    private ManagedSystemService managedSystemService;

    @Autowired
    private ProvisionSelectedResourceHelper provisionSelectedResourceHelper;

    @Autowired
    private LoginDozerConverter loginDozerConverter;

    @Autowired
    private UserDataService userMgr;

    @Autowired
    private ManagedSystemWebService managedSysService;

    @Autowired
    private LoginDataService loginManager;

    @Autowired
    private SysConfiguration sysConfiguration;

    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    public ExtensibleUser buildMngSysAttributes(Login login, String operation) {
        String userId = login.getUserId();
        String managedSysId = login.getManagedSysId();

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            return null;
        }
        MngSysPolicyDto mngSysPolicy = managedSystemService.getManagedSysPolicyByMngSysIdAndMetadataType(managedSysId, "USER_OBJECT");
        List<AttributeMap> attrMap = managedSystemService.getAttributeMapsByMngSysPolicyId(mngSysPolicy.getId());
        List<ExtensibleAttribute> requestedExtensibleAttributes = new ArrayList<ExtensibleAttribute>();
        for (AttributeMap ame : attrMap) {
            if ("USER".equalsIgnoreCase(ame.getMapForObjectType()) && "ACTIVE".equalsIgnoreCase(ame.getStatus())) {
                requestedExtensibleAttributes.add(new ExtensibleAttribute(ame.getName(), null));
            }
        }

        List<ExtensibleAttribute> mngSysAttrs = new ArrayList<ExtensibleAttribute>();
        LookupUserResponse lookupUserResponse = provisionService.getTargetSystemUser(login.getLogin(), managedSysId, requestedExtensibleAttributes);
        boolean targetSystemUserExists = false;
        if (ResponseStatus.SUCCESS.equals(lookupUserResponse.getStatus())) {
            targetSystemUserExists = true;
            mngSysAttrs = lookupUserResponse.getAttrList();
        }

        ProvisionUser pUser = new ProvisionUser(usr);

        return buildMngSysAttributesForIDMUser(pUser, targetSystemUserExists, mngSysAttrs, managedSysId, operation);
    }

    public ExtensibleUser buildMngSysAttributesForIDMUser(ProvisionUser pUser, boolean targetSystemUserExists, List<ExtensibleAttribute> mngSysAttrs, String managedSysId,
                                                          String operation) {

        Map<String, Object> bindingMap = new HashMap<>();
        bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
        bindingMap.put("org", pUser.getPrimaryOrganizationId());
        bindingMap.put("operation", operation);
        bindingMap.put(AbstractProvisioningService.USER, pUser);
        bindingMap.put(AbstractProvisioningService.USER_ATTRIBUTES, userMgr.getUserAttributesDto(pUser.getId()));

        UserEntity userEntity = userMgr.getUser(pUser.getId());

        LoginEntity identityEntity = UserUtils.getUserManagedSysIdentityEntity(managedSysId,
                userEntity.getPrincipalList());
        Login identity = (identityEntity != null) ? loginDozerConverter.convertToDTO(
                identityEntity, false) : null;
        if (identity != null) {
            String decPassword = null;
            String password = identity.getPassword();
            if (password != null) {
                try {
                    decPassword = loginManager.decryptPassword(identity.getUserId(), password);
                } catch (Exception e) {
                }
                bindingMap.put("password", decPassword);
            }
            bindingMap.put("lg", identity);
        }

        ProvisionUser u = new ProvisionUser(userDozerConverter.convertToDTO(userEntity, true));
        provisionSelectedResourceHelper.setCurrentSuperiors(u);
        bindingMap.put("userBeforeModify", u);

        bindingMap.put(AbstractProvisioningService.TARGET_SYS_MANAGED_SYS_ID, managedSysId);
        ManagedSysDto managedSys = managedSysService.getManagedSys(managedSysId);
        bindingMap.put(AbstractProvisioningService.TARGET_SYS_RES_ID, managedSys.getResourceId());

        ManagedSystemObjectMatch matchObj = null;
        ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(managedSysId, ManagedSystemObjectMatch.USER);
        if (matchObjAry != null && matchObjAry.length > 0) {
            matchObj = matchObjAry[0];
            bindingMap.put(AbstractProvisioningService.MATCH_PARAM, matchObj);
        }

        LoginEntity mLg = null;
        for (LoginEntity l : userEntity.getPrincipalList()) {
            if (managedSysId != null && managedSysId.equals(l.getManagedSysId())) {
                mLg = l;
                break;
            }
        }

        Map<String, ExtensibleAttribute> curValueMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(mngSysAttrs)) {
            for (ExtensibleAttribute attr : mngSysAttrs) {
                curValueMap.put(attr.getName(), attr);
            }
        }

        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_USER_EXISTS, targetSystemUserExists);
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_ATTRIBUTES, curValueMap);
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY_STATUS, AbstractProvisioningService.IDENTITY_EXIST);
        bindingMap.put(AbstractProvisioningService.TARGET_SYSTEM_IDENTITY, mLg != null ? mLg.getLogin() : null);

        return provisionSelectedResourceHelper.buildFromRules(managedSysId, bindingMap);
    }

}
