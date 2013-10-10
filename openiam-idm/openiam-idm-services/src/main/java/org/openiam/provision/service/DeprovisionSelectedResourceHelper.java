package org.openiam.provision.service;

import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class that implements functionality required for provisioning a selected set of resources.
 */
@Component
public class DeprovisionSelectedResourceHelper extends BaseProvisioningHelper {

	@Autowired
	private UserDozerConverter userDozerConverter;
	
    public ProvisionUserResponse deprovisionSelectedResources( String userId, String requestorUserId, List<String> resourceList)  {

        log.debug("deprovisionSelectedResources().....for userId=" + userId);

        ProvisionUserResponse response = new ProvisionUserResponse(ResponseStatus.SUCCESS);
        Map<String, Object> bindingMap = new HashMap<String, Object>();

        String requestId = "R" + UUIDGen.getUUID();

        if (resourceList == null || resourceList.isEmpty()) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.OBJECT_NOT_FOUND);
            return response;
        }

        User usr = userDozerConverter.convertToDTO(userMgr.getUser(userId), true);
        if (usr == null) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_NOT_FOUND);
            return response;
        }
        ProvisionUser pUser = new ProvisionUser(usr);

        LoginEntity lg = loginManager.getPrimaryIdentity(userId);

        List<LoginEntity> principalList = loginManager.getLoginByUser(userId);

        // setup audit information

        LoginEntity lRequestor = loginManager.getPrimaryIdentity(requestorUserId);
        LoginEntity lTargetUser = loginManager.getPrimaryIdentity(userId);

        if (lRequestor != null && lTargetUser != null) {
        	/*
            auditLog = auditHelper.addLog("DEPROVISION RESOURCE", lRequestor.getDomainId(), lRequestor.getLogin(),
                    "IDM SERVICE", usr.getCreatedBy(), "0", "USER", usr.getUserId(),
                    null, "SUCCESS", null, "USER_STATUS",
                    usr.getStatus().toString(),
                    requestId, null, null, null,
                    null, lTargetUser.getLogin(), lTargetUser.getDomainId());
			*/
        }


        for (String resourceId : resourceList) {

            bindingMap.put("IDENTITY", lg);
            //bindingMap.put("RESOURCE", res);

            Resource res = resourceDataService.getResource(resourceId);
            if (res != null) {
                String preProcessScript = getResProperty(res.getResourceProps(), "PRE_PROCESS");
                if (preProcessScript != null && !preProcessScript.isEmpty()) {
                    PreProcessor ppScript = createPreProcessScript(preProcessScript);
                    if (ppScript != null) {
                        if (executePreProcess(ppScript, bindingMap, pUser, "DELETE") == ProvisioningConstants.FAIL) {
                            continue;
                        }
                    }
                }
            }

            log.debug("Resource object = " + res);

            if (res.getManagedSysId() != null)  {
                String mSysId = res.getManagedSysId();

                if (!mSysId.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId())) {

                    log.debug("Looking up identity for : " + mSysId);

                    LoginEntity l = getLoginForManagedSys(mSysId, principalList);

                    log.debug("Identity for Managedsys =" + l);

                    if (l != null) {

                        l.setStatus(LoginStatusEnum.INACTIVE);
                        l.setAuthFailCount(0);
                        l.setPasswordChangeCount(0);
                        l.setIsLocked(0);
                        loginManager.updateLogin(l);

                        ManagedSysDto mSys = managedSysService.getManagedSys(l.getManagedSysId());

                        ManagedSystemObjectMatch matchObj = null;
                        ManagedSystemObjectMatch[] matchObjAry = managedSysService.managedSysObjectParam(mSys.getManagedSysId(), "USER");
                        if (matchObjAry != null && matchObjAry.length > 0) {
                            matchObj = matchObjAry[0];
                        }
                        log.debug("Deleting id=" + l.getLogin());
                        log.debug("- delete using managed sys id=" + mSys.getManagedSysId());

                        boolean connectorSuccess = false;

                        ObjectResponse resp = delete(loginDozerConverter.convertToDTO(l, true), requestId, mSys, matchObj);
                        if (resp.getStatus() == StatusCodeType.SUCCESS) {
                            connectorSuccess = true;
                        }

                        String postProcessScript = getResProperty(res.getResourceProps(), "POST_PROCESS");
                        if (postProcessScript != null && !postProcessScript.isEmpty()) {
                            PostProcessor ppScript = createPostProcessScript(postProcessScript);
                            if (ppScript != null) {
                                executePostProcess(ppScript, bindingMap, pUser, "DELETE", connectorSuccess);
                            }
                        }

                    }

                }
            }

        }

        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }


    private LoginEntity getLoginForManagedSys(String managedSysId, List<LoginEntity> principalList) {
        for (LoginEntity l : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
                return l;
            }

        }
        return null;
    }
}
