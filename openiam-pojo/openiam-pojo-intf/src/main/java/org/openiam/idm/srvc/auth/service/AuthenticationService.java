package org.openiam.idm.srvc.auth.service;


import java.util.List;

import org.openiam.base.ws.BooleanResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.LogoutException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.OTPServiceRequest;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.dto.Group;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * <p>
 * <code>AuthenticateService</code> <font face="arial"> allows users to authenticate using
 * various methods </font>
 * </p>
 */
@WebService
@XmlSeeAlso({
        Group.class
})
public interface AuthenticationService {

	/**
	 * Use 
	 * @param userId
	 * @throws Throwable
	 */
    @WebMethod
    @Deprecated
    void globalLogout(
            @WebParam(name = "userId", targetNamespace = "")
            String userId) throws Throwable;

    /**
     * This method executes a global logout so that the user is logged out all the application they have logged into. <br>
     * For example:
     * <p/>
     * <code>
     * authenticationService.globalLogoutWithContentProvider(userId, contentProviderId);<br>
     * </code>
     *
     * @param userId The id of the user.
     * @param contentProviderId the id of the content provider
     */
    Response globalLogoutRequest(final LogoutRequest request);

    /**
     * This method logs in a user.  It updates his Login record to reflect this fact.  Unsuccessful logins attempts are counted.  If the user
     * unsuccessfully logins in N number of times in a row, his account is locked.  'N' is defined in the Password Policy.
     * @param request - authentication request specific to the user logging in.
     * @return - an <code>AuthenticationResponse</code> object containing the user's token, principal, and internal user ID.  If the login attempt was successful, 
     * 			 <p>AuthenticationResponse.getStatus()</p> returns ResponseCode.SUCCESS.  Otherwise, it is set to ResponseCode.FAILURE
     */
    @WebMethod
    AuthenticationResponse login(
            @WebParam(name = "request", targetNamespace = "")
            AuthenticationRequest request);
    
    @WebMethod
    public Response clearOTPActiveStatus(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);
    
    @WebMethod
    public boolean isOTPActive(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);
    
    @WebMethod
    public Response sendOTPToken(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);
    
    @WebMethod
    public Response confirmOTPToken(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);


    @WebMethod
    public Response getOTPSecretKey(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);
    
    /**
     * Attempts to renew the SSO Token for this user.   
     * @param principal - the user's login
     * @param token - the current token
     * @param tokenType - the token type
     * @return a <code>Response</code> object.  If successful, <p>Response.getStatus()</p> returns ResponseCode.SUCCESS.  Otherwise, it returns ResponseCode.FAILURE.
     * 		   If renewal is successful Response.getResponseValue() will contains an <code>SSOToken</code> Object.
     */
    @WebMethod
    Response renewToken(
    		final @WebParam(name = "principal", targetNamespace = "")
            String principal,
            final @WebParam(name = "token", targetNamespace = "")
            String token,
            final @WebParam(name = "tokenType", targetNamespace = "")
            String tokenType,
            final @WebParam(name = "patternId", targetNamespace = "")
            String patternId);
    
    @WebMethod
    List<AuthStateEntity> findBeans(final @WebParam(name = "request", targetNamespace = "") AuthStateSearchBean searchBean,
    								final @WebParam(name = "from", targetNamespace = "") int from,
    								final @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public Response save(final @WebParam(name = "entity", targetNamespace = "") AuthStateEntity entity);

}
