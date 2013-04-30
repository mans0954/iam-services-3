package org.openiam.idm.srvc.auth.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleException;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
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
	public Response saveLogin(final Login principal) {
		final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
		try {
			if(principal == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			if(StringUtils.isBlank(principal.getManagedSysId()) ||
			   StringUtils.isBlank(principal.getDomainId()) || 
			   StringUtils.isBlank(principal.getLogin())) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final LoginEntity currentEntity = loginDS.getLoginByManagedSys(principal.getDomainId(), principal.getLogin(), principal.getManagedSysId());
			if(currentEntity != null) {
				if(StringUtils.isBlank(principal.getLoginId())) {
					throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
				} else if(!principal.getLoginId().equals(currentEntity.getLoginId())) {
					throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
				}
			}
			
			final LoginEntity entity = loginDozerConverter.convertToEntity(principal, true);
			if(StringUtils.isNotBlank(entity.getLoginId())) {
				loginDS.mergeLogin(entity);
			} else {
				loginDS.addLogin(entity);
			}
			resp.setResponseValue(entity.getLoginId());
		} catch(BasicDataServiceException e) {
			log.warn(String.format("Error while saving login: %s", e.getMessage()));
			resp.setErrorCode(e.getCode());
			resp.setStatus(ResponseStatus.FAILURE);
		} catch(Throwable e) {
			resp.setStatus(ResponseStatus.FAILURE);
			resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
			log.error("Error while saving login", e);
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#decryptPassword(java.lang.String)
	 */
	public Response decryptPassword(String userId, String password) {
		String pswd = null;
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		try {
			pswd = loginDS.decryptPassword(userId, password);
		}catch(Exception e) {
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
	public Response encryptPassword(String userId, String password) {
		Response resp = new Response(ResponseStatus.SUCCESS);
		String pswd = null;
		try {
			pswd = loginDS.encryptPassword(userId, password);
		}catch(Exception e) {
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
	public LoginListResponse getLoginByDomain(String domainId) {
		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		List<LoginEntity> lgList = loginDS.getLoginByDomain(domainId);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false)); 
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLoginByManagedSys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public LoginResponse getLoginByManagedSys(String domainId, String principal,
			String sysId) {
		
		LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
		LoginEntity lg = loginDS.getLoginByManagedSys(domainId, principal, sysId);
		if (lg == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipal(loginDozerConverter.convertToDTO(lg, false)); 
		}
		return resp;
		

	}

    public LoginResponse getPrincipalByManagedSys(String principalName,
                                                  String managedSysId) {
        final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
        final List<LoginEntity> lgList = loginDS.getLoginDetailsByManagedSys(principalName, managedSysId);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipal(loginDozerConverter.convertToDTO(lgList.get(0), false));
		}
		return resp;
    }

    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLoginByUser(java.lang.String)
      */
	public LoginListResponse getLoginByUser(String userId) {
		
		log.info("getLoginByUser userId=" + userId);
		
		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		List<LoginEntity> lgList = loginDS.getLoginByUser(userId);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false)); 
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getPassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response getPassword(String domainId, String principal,
			String managedSysId) throws Exception {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		String pswd = loginDS.getPassword(domainId, principal, managedSysId);
		if (pswd == null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		resp.setResponseValue(pswd);
		return resp;
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#lockLogin(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response lockLogin(String domainId, String principal,
			String managedSysId) {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		loginDS.lockLogin(domainId, principal, managedSysId);
		return resp;
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#bulkUnLock(org.openiam.idm.srvc.user.dto.UserStatusEnum)
	 */
	public Response bulkUnLock(UserStatusEnum status) {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		loginDS.bulkUnLock(status);
		return resp;
	}
	
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#removeLogin(java.lang.String, java.lang.String)
	 */
	public Response removeLogin(String domainId, String principal, String managedSysId) {
		Response resp = new Response(ResponseStatus.SUCCESS);
		loginDS.removeLogin(domainId, principal, managedSysId);
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#resetPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response resetPassword(String domainId, String principal,String managedSysId, String password) {
		return resetPasswordAndNotifyUser(domainId, principal, managedSysId, password, false);
	}

    /* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#resetPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    public Response resetPasswordAndNotifyUser(String domainId, String principal,String managedSysId, String password, boolean notifyUserViaEmail) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        boolean result = loginDS.resetPassword(domainId, principal, managedSysId, password);
        if (!result) {
            resp.setStatus(ResponseStatus.FAILURE);
        }

        if(notifyUserViaEmail){
            // send email to user with password
            LoginEntity loginEntity = loginDS.getLoginByManagedSys(domainId, principal, managedSysId);
            if(loginEntity!=null){

                Response respPwd = this.decryptPassword(loginEntity.getUserId(), password);

                if(respPwd.getStatus()==ResponseStatus.SUCCESS){
                    UserEntity user =  userService.getUser(loginEntity.getUserId());
                    String pwd = (String)respPwd.getResponseValue();
                    if(user!=null){
                        NotificationRequest request = new NotificationRequest();
                        request.setUserId(user.getUserId());
                        request.setNotificationType("USER_PASSWORD_EMAIL");


                        List<NotificationParam> paramList = new LinkedList<NotificationParam>();

                        paramList.add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getUserId()));
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
	public Response setPassword(String domainId, String principal,
			String managedSysId, String password) {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		boolean result = loginDS.setPassword(domainId, principal, managedSysId, password);
		if (!result) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		return resp;
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#unLockLogin(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response unLockLogin(String domainId, String principal,
			String managedSysId) {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		loginDS.unLockLogin(domainId, principal, managedSysId);
		return resp;
		
	}
	
	public Response isPasswordEq( 
			String domainId, String principal, 
			String managedSysId ,  String newPassword) throws Exception {
		
		Response resp = new Response(ResponseStatus.SUCCESS);
		boolean retval = loginDS.isPasswordEq(domainId, principal, managedSysId, newPassword);
		resp.setResponseValue(new Boolean(retval));
		if (!retval) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		return resp;
	}
	
	/**
	 * Checks to see if a login exists for a user - domain - managed system combination
	 * @param domainId
	 * @param principal
	 * @param managedSysId
	 * @return
	 */
	public Response loginExists( String domainId, String principal, String managedSysId ) {
		Response resp = new Response(ResponseStatus.SUCCESS);
		boolean retval = loginDS.loginExists(domainId, principal, managedSysId);
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
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getPrimaryIdentity(java.lang.String)
	 */
	public LoginResponse getPrimaryIdentity(String userId) {
		LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
		LoginEntity lg = loginDS.getPrimaryIdentity(userId);
		if (lg == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipal(loginDozerConverter.convertToDTO(lg, false)); 
		}
		return resp;
	}
	
	@Override
	public Login findById(final String loginId) {
		final LoginEntity entity = loginDS.getLoginDetails(loginId);
		return (entity != null) ? loginDozerConverter.convertToDTO(entity, true) : null;
	}

	@Override
    public List<Login> findBeans(LoginSearchBean searchBean, Integer from, Integer size){
        return loginDozerConverter.convertToDTOList(loginDS.findBeans(searchBean, from, size), false);
    }

    public Integer count(LoginSearchBean searchBean){
        return loginDS.count(searchBean);
    }

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#bulkResetPasswordChangeCount()
	 */
	public Response bulkResetPasswordChangeCount() {
		Response resp = new Response(ResponseStatus.SUCCESS);
		int rowCount =  loginDS.bulkResetPasswordChangeCount();
		resp.setResponseValue( new Integer(rowCount));
		return resp;
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getLockedUserSince(java.util.Date)
	 */
	public LoginListResponse getLockedUserSince(Date lastExecTime) {
		log.info("getLockedUserSince " );
		
		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		List<LoginEntity> lgList = loginDS.getLockedUserSince(lastExecTime);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false)); 
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getInactiveUsers(int, int)
	 */
	public LoginListResponse getInactiveUsers(int startDays, int endDays) {
		
		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		List<LoginEntity> lgList = loginDS.getInactiveUsers(startDays, endDays);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false)); 
		}
		return resp;
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.ws.LoginDataWebService#getUserNearPswdExpiration(int)
	 */
	public LoginListResponse getUserNearPswdExpiration(int expDays) {
		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		List<LoginEntity> lgList = loginDS.getUserNearPswdExpiration(expDays);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false)); 
		}
		return resp;
	}


	public Response changeIdentityName(
			String newPrincipalName, 
			String newPassword, 
			String userId, 
			String managedSysId,
			String domainId) {
		
		LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
		int retval = this.loginDS.changeIdentityName(newPrincipalName, newPassword, userId, managedSysId, domainId);
		if (retval > 0) {
			resp.setResponseValue(new Integer(retval));
		}else {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		
		return resp;
	}


    public LoginListResponse getAllLoginByManagedSys(String managedSysId) {
        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		List<LoginEntity> lgList = loginDS.getAllLoginByManagedSys(managedSysId);
		if (lgList == null ) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginDozerConverter.convertToDTOList(lgList, false)); 
		}
		return resp;
    }

	@Override
	public Response deleteLogin(final String loginId) {
		final LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
		try {
			if(StringUtils.isBlank(loginId)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			loginDS.deleteLogin(loginId);
		} catch(BasicDataServiceException e) {
			log.warn(String.format("Error while saving login: %s", e.getMessage()));
			resp.setErrorCode(e.getCode());
			resp.setStatus(ResponseStatus.FAILURE);
		} catch(Throwable e) {
			resp.setStatus(ResponseStatus.FAILURE);
			resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
			log.error("Error while saving login", e);
		}
		return resp;
	}
}
