package org.openiam.service.integration.provisioning;


import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;


public class UserHistoryServiceTest extends AbstractUserManagementServiceTest {

    @Autowired
    @Qualifier("roleServiceClient")
    protected RoleDataWebService roleServiceClient;

    @Autowired
    @Qualifier("challengeResponseServiceClient")
    protected ChallengeResponseWebService challengeResponseServiceClient;

    @Autowired
    @Qualifier("auditServiceClient")
    protected IdmAuditLogWebDataService auditLogService;

    @Autowired
    @Qualifier("userServiceClient")
    protected UserDataWebService userServiceClient;

    @Autowired
    @Qualifier("loginServiceClient")
    protected LoginDataWebService loginServiceClient;

    protected String ROLE_ID="1";
    protected String ROLE_ID2="2";


    @Test(groups ={"GET_USER_HISTORY"})
    public void checkUserHistory() throws Exception {
        User user = doCreate();
        User foundUser = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser.getDefaultLogin());
        User user2 = doCreate();
        User foundUser2 = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser2.getDefaultLogin());
        User user3 = doCreate();
        User foundUser3 = getAndAssert(user.getId());
        Assert.assertNotNull(foundUser3.getDefaultLogin());

        Role role = roleServiceClient.getRoleLocalized(ROLE_ID, user.getId(), getDefaultLanguage());
        role.setOperation(AttributeOperationEnum.ADD);
        Assert.assertNotNull(role, "Cann't find role with ID :" + ROLE_ID);
        Role role2 = roleServiceClient.getRoleLocalized(ROLE_ID2, user3.getId(), getDefaultLanguage());
        role2.setOperation(AttributeOperationEnum.ADD);
        Assert.assertNotNull(role2, "Cann't find role with ID :" + ROLE_ID2);


        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(user.getId());

        final ProvisionUser pUser = new ProvisionUser(user2);
        pUser.setRequestorUserId(user.getId());
        role.setOperation(AttributeOperationEnum.ADD);
        pUser.getRoles().add(role);
        pUser.getRoles().add(role2);
        idmAuditLog.setAction(AuditAction.ADD_USER_TO_ROLE.value());
        idmAuditLog.setAuditDescription("Add user to role");
        auditLogService.addLog(idmAuditLog);

        Response wsResponse = null;
        wsResponse = provisionService.modifyUser(pUser);

        Thread.sleep(5000);

        AuditLogSearchBean searchBean = new AuditLogSearchBean();
        searchBean.setDeepCopy(false);
        searchBean.setParentOnly();

        searchBean.setUserId(StringUtils.trimToNull(user2.getId()));
        searchBean.setTargetId(user2.getId());
        searchBean.setTargetType(AuditTarget.USER.value());

        int count = auditLogService.count(searchBean);
        List<String> resultsTargetIds = auditLogService.getIds(searchBean, 0, 99);

        Assert.assertEquals(count, 2, "Don't correct count rows. Must be 2.");
        for (String id : resultsTargetIds) {
            Assert.assertFalse(((id == user.getId()) || (id == user3.getId())), "Wrong user_ID in UserHistory");
        }

        //SET REQUESTOR
        searchBean.setUserId(user.getId());
        searchBean.setTargetId(user2.getId());
        searchBean.setTargetType(AuditTarget.USER.value());
        searchBean.setUserVsTargetAndFlag(true);

        count = auditLogService.count(searchBean);
        resultsTargetIds = auditLogService.getIds(searchBean, 0, 99);

        Assert.assertEquals(count, 1, "Don't correct count rows. Must be 1.");
        for (String id : resultsTargetIds) {
            Assert.assertFalse((id == user.getId()), "Wrong user_ID in UserHistory");
        }

    }

}
