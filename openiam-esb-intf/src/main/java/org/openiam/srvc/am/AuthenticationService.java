package org.openiam.srvc.am;

import org.openiam.base.request.AuthenticationRequest;
import org.openiam.base.request.LogoutRequest;
import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.response.AuthenticationResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Created by alexander on 11/08/16.
 */
@WebService
public interface AuthenticationService {
    /**
     * Use
     * @param userId
     * @throws Throwable
     */
    @WebMethod
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
     * @param request The request to logout.
     */
    @WebMethod
    Response globalLogoutRequest(@WebParam(name = "request", targetNamespace = "") LogoutRequest request);

    /**
     * This method logs in a user.  It updates his Login record to reflect this fact.  Unsuccessful logins attempts are counted.  If the user
     * unsuccessfully logins in N number of times in a row, his account is locked.  'N' is defined in the Password Policy.
     * @param request - authentication request specific to the user logging in.
     * @return - an <code>AuthenticationResponse</code> object containing the user's token, principal, and internal user ID.  If the login attempt was successful,
     * 			 <p>AuthenticationResponse.getStatus()</p> returns ResponseCode.SUCCESS.  Otherwise, it is set to ResponseCode.FAILURE
     */
    @WebMethod
    AuthenticationResponse login(@WebParam(name = "request", targetNamespace = "") AuthenticationRequest request);

    @WebMethod
    Response clearOTPActiveStatus(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);

    @WebMethod
    boolean isOTPActive(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);

    @WebMethod
    Response sendOTPToken(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);

    @WebMethod
    Response confirmOTPToken(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);


    @WebMethod
    Response getOTPSecretKey(@WebParam(name = "request", targetNamespace = "") OTPServiceRequest request);

    /**
     * Attempts to renew the SSO Token for this user.
     * @param principal - the user's login
     * @param token - the current token
     * @param tokenType - the token type
     * @return a <code>Response</code> object.  If successful, <p>Response.getStatus()</p> returns ResponseCode.SUCCESS.  Otherwise, it returns ResponseCode.FAILURE.
     * 		   If renewal is successful Response.getResponseValue() will contains an <code>SSOToken</code> Object.
     */
    @WebMethod
    Response renewToken(final @WebParam(name = "principal", targetNamespace = "") String principal,
                        final @WebParam(name = "token", targetNamespace = "") String token,
                        final @WebParam(name = "tokenType", targetNamespace = "") String tokenType,
                        final @WebParam(name = "patternId", targetNamespace = "") String patternId);

    @WebMethod
    List<AuthStateEntity> findBeans(final @WebParam(name = "request", targetNamespace = "") AuthStateSearchBean searchBean,
                                    final @WebParam(name = "from", targetNamespace = "") int from,
                                    final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    Response save(final @WebParam(name = "entity", targetNamespace = "") AuthStateEntity entity);

    @WebMethod
    List<String> getAllLoginModuleSpringBeans();
}
