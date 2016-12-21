package org.openiam.srvc.user;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.*;
import org.openiam.base.response.data.BooleanResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.*;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.base.response.LoginListResponse;
import org.openiam.base.response.LoginResponse;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.mq.constants.api.user.LoginAPI;
import org.openiam.mq.constants.queue.user.LoginQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;

import java.util.Date;
import java.util.List;

@WebService(endpointInterface = "org.openiam.srvc.user.LoginDataWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/auth/service", 
		serviceName = "LoginDataWebService",
		portName = "LoginDataWebServicePort")
@Service("loginWS")
@Transactional
public class LoginDataWebServiceImpl extends AbstractApiService implements LoginDataWebService {

	
	private static final Log log = LogFactory.getLog(LoginDataWebServiceImpl.class);

	@Autowired
	public LoginDataWebServiceImpl(LoginQueue queue) {
		super(queue);
	}

	@Override
	public Response isValidLogin(final Login principal) {
		return this.getResponse(LoginAPI.Validate, new BaseCrudServiceRequest<>(principal), Response.class);
	}
	
	@Override
	public Response saveLogin(final Login principal) {
		return this.manageCrudApiRequest(LoginAPI.Save, principal);
	}

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#decryptPassword(java.lang.String)
	 */
    @Override
	public Response decryptPassword(String userId, String password) {
		DataEncryptionRequest request = new DataEncryptionRequest();
		request.setData(password);
		request.setUserId(userId);

		StringResponse response = this.getResponse(LoginAPI.DecryptPassword, request, StringResponse.class);
		return response.convertToBase();
	}

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#encryptPassword(java.lang.String)
	 */
    @Override
	public Response encryptPassword(String userId, String password) {
		DataEncryptionRequest request = new DataEncryptionRequest();
		request.setData(password);
		request.setUserId(userId);

		StringResponse response = this.getResponse(LoginAPI.EncryptPassword, request, StringResponse.class);
    	return response.convertToBase();
	}


	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#getLoginByManagedSys(java.lang.String, java.lang.String, java.lang.String)
	 *
	 * use findBeans instead of
	 */
    @Override
	@Deprecated
	public LoginResponse getLoginByManagedSys(String principal, String sysId) {
    	LoginSearchBean searchBean = new LoginSearchBean();
    	searchBean.setLoginMatchToken(new SearchParam(principal, MatchType.EXACT));
		searchBean.setManagedSysId(sysId);
		List<Login> loginList = this.findBeans(searchBean, 0, 1);


		LoginResponse resp = new LoginResponse(ResponseStatus.SUCCESS);
		if (CollectionUtils.isEmpty(loginList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipal(loginList.get(0));
		}
		return resp;
	}


    /* (non-Javadoc)
      * @see org.openiam.srvc.user.LoginDataWebService#getLoginByUser(java.lang.String)
      *
	  * use findBeans instead of
      */
    @Override
	@Deprecated
	public LoginListResponse getLoginByUser(String userId) {
		LoginSearchBean searchBean = new LoginSearchBean();
		searchBean.setUserId(userId);
		List<Login> loginList = this.findBeans(searchBean, -1, -1);

		log.info("getLoginByUser userId=" + userId);
		
		LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		if (CollectionUtils.isEmpty(loginList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(loginList);
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#lockLogin(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public Response lockLogin(String principal, String managedSysId) {
    	Login dto = new Login();
    	dto.setManagedSysId(managedSysId);
    	dto.setLogin(principal);
		return this.manageCrudApiRequest(LoginAPI.LockLogin, dto);
	}
    @Override
    public Response activateLogin(final String loginId){
		Login dto = new Login();
		dto.setId(loginId);
		return this.manageCrudApiRequest(LoginAPI.ActivateLogin, dto);

    }
    @Override
    public Response deActivateLogin(final String loginId){
		Login dto = new Login();
		dto.setId(loginId);
		return this.manageCrudApiRequest(LoginAPI.DeActivateLogin, dto);
    }

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#bulkUnLock(org.openiam.idm.srvc.user.dto.UserStatusEnum)
	 */
    @Override
	public Response bulkUnLock(UserStatusEnum status) {
		UnlockRequest request = new UnlockRequest();
		request.setStatus(status);
		return this.getResponse(LoginAPI.BulkUnLock, request, Response.class);
	}

    @Override
	public Response removeLogin(String principal, String managedSysId) {
    	Login dto = new Login();
    	dto.setManagedSysId(managedSysId);
    	dto.setLogin(principal);
    	return this.manageCrudApiRequest(LoginAPI.RemoveLogin, dto);
	}

    @Override
    @Deprecated
	public Response resetPassword(String principal, String managedSysId, String password) {
		return resetPasswordAndNotifyUser(principal, managedSysId, null, password, false);
	}
    

	@Override
	public Response resetPasswordWithContentProvider(final String principal, final String managedSysId, final String password, final String contentProviderId) {
		return resetPasswordAndNotifyUser(principal, managedSysId, contentProviderId, password, false);
	}
    
    @Override
    public Response resetPasswordAndNotifyUser(final String principal, final String managedSysId, final String contentProviderId, final String password, final boolean notifyUserViaEmail) {
		ResetPasswordRequest request = new ResetPasswordRequest();
		request.setManagedSysId(managedSysId);
		request.setPrincipal(principal);
		request.setPassword(password);
		request.setContentProviderId(contentProviderId);
		request.setNotifyUserViaEmail(notifyUserViaEmail);

		return this.getResponse(LoginAPI.ResetPassword, request, Response.class);
    }

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#unLockLogin(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public Response unLockLogin(String principal, String managedSysId) {
		UnlockRequest request = new UnlockRequest();
		request.setPrincipal(principal);
		request.setManagedSysId(managedSysId);

		return this.getResponse(LoginAPI.UnLockLogin, request, Response.class);
	}

    @Override
	public Response isPasswordEq(String principal, String managedSysId ,  String newPassword) {
		Login dto = new Login();
		dto.setManagedSysId(managedSysId);
		dto.setLogin(principal);
		dto.setPassword(newPassword);

		return this.manageCrudApiRequest(LoginAPI.IsPasswordEq, dto);
	}
	
	/**
	 * Checks to see if a login exists for a user - domain - managed system combination
	 * @param principal
	 * @param managedSysId
	 * @return
	 */
    @Override
	public Response loginExists(String principal, String managedSysId ) {
		LoginSearchBean searchBean = new LoginSearchBean();
		searchBean.setLoginMatchToken(new SearchParam(principal, MatchType.EXACT));
		searchBean.setManagedSysId(managedSysId);

		BooleanResponse loginExistsResponse = this.getResponse(LoginAPI.LoginExists, new BaseSearchServiceRequest<>(searchBean), BooleanResponse.class);
		if (!loginExistsResponse.getValue()) {
			loginExistsResponse.setStatus(ResponseStatus.FAILURE);
		}
		return loginExistsResponse.convertToBase();
	}
	


	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#getUserManagedSysIdentityEntity(java.lang.String)
	 */
    @Override
	public LoginResponse getPrimaryIdentity(String userId) {
		LoginSearchBean searchBean = new LoginSearchBean();
		searchBean.setUserId(userId);

		return this.getResponse(LoginAPI.GetPrimaryIdentity, new BaseSearchServiceRequest<>(searchBean), LoginResponse.class);
	}
	
	@Override
	public Login findById(final String loginId) {
		LoginResponse response = this.getResponse(LoginAPI.FindById, new IdServiceRequest(loginId), LoginResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getPrincipal();
	}

	@Override
    public List<Login> findBeans(LoginSearchBean searchBean, int from, int size){
		LoginListResponse response = this.getResponse(LoginAPI.FindBeans, new BaseSearchServiceRequest<>(searchBean,from, size), LoginListResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getPrincipalList();
    }

    @Override
    public Integer count(LoginSearchBean searchBean){
    	return this.getIntValue(LoginAPI.Count, new BaseSearchServiceRequest<>(searchBean));
    }

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#bulkResetPasswordChangeCount()
	 */
    @Override
	public Response bulkResetPasswordChangeCount() {
		IntResponse response = this.getResponse(LoginAPI.BulkResetPasswordChangeCount, new EmptyServiceRequest(), IntResponse.class);
		return response.convertToBase();
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#getLockedUserSince(java.util.Date)
	 */
    @Override
	public LoginListResponse getLockedUserSince(Date lastExecTime) {
		log.info("getLockedUserSince " );
		PrincipalRequest request = new PrincipalRequest();
		request.setLastExecTime(lastExecTime);
		return this.getResponse(LoginAPI.GetLockedUserSince, request, LoginListResponse.class);
	}

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#getInactiveUsers(int, int)
	 */
    @Override
	public LoginListResponse getInactiveUsers(int startDays, int endDays) {
		PrincipalRequest request = new PrincipalRequest();
		request.setStartDays(startDays);
		request.setEndDays(endDays);
		return this.getResponse(LoginAPI.GetInactiveUsers, request, LoginListResponse.class);
	}

	/* (non-Javadoc)
	 * @see org.openiam.srvc.user.LoginDataWebService#getUserNearPswdExpiration(int)
	 */
    @Override
	public LoginListResponse getUserNearPswdExpiration(int expDays) {
		PrincipalRequest request = new PrincipalRequest();
		request.setStartDays(expDays);
		return this.getResponse(LoginAPI.GetUserNearPswdExpiration, request, LoginListResponse.class);
	}

	 /**
     *Returns a list of Login objects which are nearing expiry depending on PWD_EXP_WARN password attribute
     *If attribute unset, default is assumed to be 5. 
     *
     * @param 
     * @return
     */
    @Override
    public LoginListResponse getUsersNearPswdExpiration(){
		PrincipalRequest request = new PrincipalRequest();
		return this.getResponse(LoginAPI.GetUserNearPswdExpiration, request, LoginListResponse.class);
	}


    @Override
    @Deprecated
    public LoginListResponse getAllLoginByManagedSys(String managedSysId) {
    	LoginSearchBean searchBean = new LoginSearchBean();
    	searchBean.setManagedSysId(managedSysId);
		List<Login> lgList = findBeans(searchBean, -1,-1);

        LoginListResponse resp = new LoginListResponse(ResponseStatus.SUCCESS);
		if (CollectionUtils.isEmpty(lgList)) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setPrincipalList(lgList);
		}
		return resp;
    }

	@Override
	public Response deleteLogin(final String loginId) {
    	Login dto = new Login();
    	dto.setId(loginId);
    	return this.manageCrudApiRequest(LoginAPI.DeleteLogin, dto);
	}

    public Response forgotUsername(String email){
		StringDataRequest request = new StringDataRequest();
		request.setData(email);
		return this.getResponse(LoginAPI.ForgotUsername, request, Response.class);
    }
}
