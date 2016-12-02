package org.openiam.idm.srvc.auth.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.msg.service.MailTemplateParameters;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@WebService(endpointInterface = "org.openiam.idm.srvc.auth.ws.LoginDataWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/auth/service",
        serviceName = "LoginDataWebService",
        portName = "LoginDataWebServicePort")
@Service("loginWS")
@Transactional
public class LoginDataWebServiceImpl implements LoginDataWebService {

    @Autowired
    private LoginDataService loginDS;
    @Autowired
    private UserDataService userService;

    @Autowired
    private LoginDozerConverter loginDozerConverter;

    @Autowired
    private MailService mailService;

    private static final Log log = LogFactory.getLog(LoginDataWebServiceImpl.class);

    @Override
    public Response isValidLogin(final Login principal) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        try {
            if (principal == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            if (StringUtils.isBlank(principal.getManagedSysId()) ||
                    StringUtils.isBlank(principal.getLogin())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final LoginEntity currentEntity = loginDS.getLoginByManagedSys(principal.getLogin(), principal.getManagedSysId());
            if (currentEntity != null) {
                if (StringUtils.isBlank(principal.getLoginId())) {
                    throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
                } else if (!principal.getLoginId().equals(currentEntity.getLoginId())) {
                    throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
                }
            }

        } catch (BasicDataServiceException e) {
            log.warn(String.format("Error while saving login: %s", e.getMessage()));
            resp.setErrorCode(e.getCode());
            resp.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            log.error("Error while saving login", e);
        }
        return resp;
    }

    @Override
    public Response saveLogin(final Login principal) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        try {
            if (principal == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            if (StringUtils.isBlank(principal.getManagedSysId()) ||
                    StringUtils.isBlank(principal.getLogin())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final LoginEntity currentEntity = loginDS.getLoginByManagedSys(principal.getLogin(), principal.getManagedSysId());
            if (currentEntity != null) {
                if (StringUtils.isBlank(principal.getLoginId())) {
                    throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
                } else if (!principal.getLoginId().equals(currentEntity.getLoginId())) {
                    throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
                }
            }

            final LoginEntity entity = loginDozerConverter.convertToEntity(principal, true);
//            if (currentEntity!=null && currentEntity.getPasswordHistory() != null) {
//                entity.setPasswordHistory(currentEntity.getPasswordHistory());
//            }
            if (StringUtils.isNotBlank(entity.getLoginId())) {
                if (currentEntity != null) {
                    entity.setPasswordHistory(currentEntity.getPasswordHistory());
                }
                loginDS.updateLogin(entity);
            } else {
                loginDS.addLogin(entity);
            }
            resp.setResponseValue(entity.getLoginId());
        } catch (BasicDataServiceException e) {
            log.warn(String.format("Error while saving login: %s", e.getMessage()));
            resp.setErrorCode(e.getCode());
            resp.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            log.error("Error while saving login", e);
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#decryptPassword(java.lang.String)
     */
    @Override
    public Response decryptPassword(String userId, String password) {
        String pswd = null;

        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            pswd = loginDS.decryptPassword(userId, password);
        } catch (Exception e) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        if (pswd == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        resp.setResponseValue(pswd);
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#encryptPassword(java.lang.String)
     */
    @Override
    public Response encryptPassword(String userId, String password) {
        Response resp = new Response(ResponseStatus.SUCCESS);
        String pswd = null;
        try {
            pswd = loginDS.encryptPassword(userId, password);
        } catch (Exception e) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        if (pswd == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }
        resp.setResponseValue(pswd);
        return resp;
    }

	/* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLoginByDomain(java.lang.String)
	 */
//    @Override
//	public LoginListResponse getLoginByDomain(String domainId) {
//		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
//		List<LoginEntity> lgList = loginDS.getLoginByDomain(domainId);
//		if (lgList == null ) {
//			resp.setStatus(ResponseStatus.FAILURE);
//		}else {
//			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
//		}
//		return resp;
//	}

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLoginByManagedSys(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public LoginResponse getLoginByManagedSys(String principal, String sysId) {

        LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        LoginEntity lg = loginDS.getLoginByManagedSys(principal, sysId);
        if (lg == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipal(loginDozerConverter.convertToDTO(lg, false));
        }
        return resp;


    }

    @Override
    public LoginResponse getPrincipalByManagedSys(String principalName,
                                                  String managedSysId) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        final List<LoginEntity> lgList = loginDS.getLoginDetailsByManagedSys(principalName, managedSysId);
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipal(loginDozerConverter.convertToDTO(lgList.get(0), false));
        }
        return resp;
    }

    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLoginByUser(java.lang.String)
      */
    @Override
    public LoginListResponse getLoginByUser(String userId) {

        log.info("getLoginByUser userId=" + userId);

        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
        List<LoginEntity> lgList = loginDS.getLoginByUser(userId);
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getPassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Response getPassword(String principal, String managedSysId) throws Exception {

        Response resp = new Response(ResponseStatus.SUCCESS);
        String pswd = loginDS.getPassword(principal, managedSysId);
        if (pswd == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        resp.setResponseValue(pswd);
        return resp;

    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#lockLogin(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Response lockLogin(String principal,
                              String managedSysId) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        loginDS.lockLogin(principal, managedSysId);
        return resp;

    }

    @Override
    public Response activateLogin(final String loginId) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(loginId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            loginDS.activateDeactivateLogin(loginId, LoginStatusEnum.ACTIVE);
        } catch (BasicDataServiceException e) {
            log.warn(String.format("Error while activating login: %s", e.getMessage()));
            resp.setErrorCode(e.getCode());
            resp.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            log.error("Error while activating login", e);
        }
        return resp;
    }

    @Override
    public Response deActivateLogin(final String loginId) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(loginId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            loginDS.activateDeactivateLogin(loginId, LoginStatusEnum.INACTIVE);
        } catch (BasicDataServiceException e) {
            log.warn(String.format("Error while deactivating login: %s", e.getMessage()));
            resp.setErrorCode(e.getCode());
            resp.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            log.error("Error while deactivating login", e);
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#bulkUnLock(org.openiam.idm.srvc.user.dto.UserStatusEnum)
     */
    @Override
    public Response bulkUnLock(UserStatusEnum status) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        loginDS.bulkUnLock(status);
        return resp;
    }


    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#removeLogin(java.lang.String, java.lang.String)
     */
    @Override
    public Response removeLogin(String principal, String managedSysId) {
        Response resp = new Response(ResponseStatus.SUCCESS);
        loginDS.removeLogin(principal, managedSysId);
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#resetPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Response resetPassword(String principal, String managedSysId, String password) {
        return resetPasswordAndNotifyUser(principal, managedSysId, password, false);
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#resetPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
    public Response resetPasswordAndNotifyUser(String principal, String managedSysId, String password, boolean notifyUserViaEmail) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        boolean result = loginDS.resetPassword(principal, managedSysId, password);
        if (!result) {
            resp.setStatus(ResponseStatus.FAILURE);
        }

        if (notifyUserViaEmail) {
            // send email to user with password
            LoginEntity loginEntity = loginDS.getLoginByManagedSys(principal, managedSysId);
            if (loginEntity != null) {

                Response respPwd = this.decryptPassword(loginEntity.getUserId(), password);

                if (respPwd.getStatus() == ResponseStatus.SUCCESS) {
                    UserEntity user = userService.getUser(loginEntity.getUserId());
                    String pwd = (String) respPwd.getResponseValue();
                    if (user != null) {
                        NotificationRequest request = new NotificationRequest();
                        request.setUserId(user.getId());
                        request.setNotificationType("USER_PASSWORD_EMAIL");


                        List<NotificationParam> paramList = new LinkedList<NotificationParam>();

                        paramList.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
                        paramList.add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), principal));
                        paramList.add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), pwd));
                        paramList.add(new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
                        paramList.add(new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));

                        request.setParamList(paramList);

                        mailService.sendNotification(request);
                    }
                }
            }
        }
        return resp;
    }

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#setPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    /*
	public Response setPassword(String domainId, String principal,
			String managedSysId, String password) {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		boolean result = loginDS.setPassword(domainId, principal, managedSysId, password);
		if (!result) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		return resp;
		
	}
	*/

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#unLockLogin(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Response unLockLogin(String principal, String managedSysId) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        loginDS.unLockLogin(principal, managedSysId);
        return resp;

    }

    @Override
    public Response isPasswordEq(String principal, String managedSysId, String newPassword) throws Exception {

        Response resp = new Response(ResponseStatus.SUCCESS);
        boolean retval = loginDS.isPasswordEq(principal, managedSysId, newPassword);
        resp.setResponseValue(new Boolean(retval));
        if (!retval) {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }

    /**
     * Checks to see if a login exists for a user - domain - managed system combination
     *
     * @param principal
     * @param managedSysId
     * @return
     */
    @Override
    public Response loginExists(String principal, String managedSysId) {
        Response resp = new Response(ResponseStatus.SUCCESS);
        boolean retval = loginDS.loginExists(principal, managedSysId);
        resp.setResponseValue(new Boolean(retval));
        if (!retval) {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;

    }


    public LoginDataService getLoginDS() {
        return loginDS;
    }

    public void setLoginDS(LoginDataService loginDS) {
        this.loginDS = loginDS;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getUserManagedSysIdentityEntity(java.lang.String)
     */
    @Override
    public LoginResponse getPrimaryIdentity(String userId) {
        LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        LoginEntity lg = loginDS.getPrimaryIdentity(userId);
        if (lg == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipal(loginDozerConverter.convertToDTO(lg, false));
        }
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Login findById(final String loginId) {
        final LoginEntity entity = loginDS.getLoginDetails(loginId);
        return (entity != null) ? loginDozerConverter.convertToDTO(entity, true) : null;
    }

    @Override
    public List<Login> findBeans(LoginSearchBean searchBean, Integer from, Integer size) {
        return loginDozerConverter.convertToDTOList(loginDS.findBeans(searchBean, from, size), false);
    }

    @Override
    public Integer count(LoginSearchBean searchBean) {
        return loginDS.count(searchBean);
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#bulkResetPasswordChangeCount()
     */
    @Override
    public Response bulkResetPasswordChangeCount() {
        Response resp = new Response(ResponseStatus.SUCCESS);
        int rowCount = loginDS.bulkResetPasswordChangeCount();
        resp.setResponseValue(new Integer(rowCount));
        return resp;

    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLockedUserSince(java.util.Date)
     */
    @Override
    public LoginListResponse getLockedUserSince(Date lastExecTime) {
        log.info("getLockedUserSince ");

        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
        List<LoginEntity> lgList = loginDS.getLockedUserSince(lastExecTime);
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getInactiveUsers(int, int)
     */
    @Override
    public LoginListResponse getInactiveUsers(int startDays, int endDays) {

        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
        List<LoginEntity> lgList = loginDS.getInactiveUsers(startDays, endDays);
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
        }
        return resp;

    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getUserNearPswdExpiration(int)
     */
    @Override
    public LoginListResponse getUserNearPswdExpiration(int expDays) {
        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
        List<LoginEntity> lgList = loginDS.getUserNearPswdExpiration(expDays);
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
        }
        return resp;
    }

    /**
     * Returns a list of Login objects which are nearing expiry depending on PWD_EXP_WARN password attribute
     * If attribute unset, default is assumed to be 5.
     *
     * @param
     * @return
     */
    @Override
    public LoginListResponse getUsersNearPswdExpiration() {
        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
        List<LoginEntity> lgList = loginDS.getUsersNearPswdExpiration();
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
        }
        return resp;
    }

    @Override
    public Response changeIdentityName(
            String newPrincipalName,
            String newPassword,
            String userId,
            String managedSysId) {

        LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        int retval = this.loginDS.changeIdentityName(newPrincipalName, newPassword, userId, managedSysId);
        if (retval > 0) {
            resp.setResponseValue(new Integer(retval));
        } else {
            resp.setStatus(ResponseStatus.FAILURE);
        }

        return resp;
    }

    @Override
    public LoginListResponse getAllLoginByManagedSys(String managedSysId) {
        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
        List<LoginEntity> lgList = loginDS.getAllLoginByManagedSys(managedSysId);
        if (lgList == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false));
        }
        return resp;
    }

    @Override
    public Response deleteLogin(final String loginId) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(loginId)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            loginDS.deleteLogin(loginId);
        } catch (BasicDataServiceException e) {
            log.warn(String.format("Error while saving login: %s", e.getMessage()));
            resp.setErrorCode(e.getCode());
            resp.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            log.error("Error while saving login", e);
        }
        return resp;
    }

    public Response forgotUsername(String email) {
        final Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(email)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            loginDS.forgotUsername(email);
        } catch (BasicDataServiceException e) {
            log.warn(String.format("Error while sending user name: %s", e.getMessage()));
            resp.setErrorCode(e.getCode());
            resp.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            log.error("Error while sending user name", e);
        }
        return resp;
    }
}
