package org.openiam.idm.srvc.auth.service;


import org.openiam.base.ws.BooleanResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.LogoutException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
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
     * This method executes a global logout so that the user is logged out all the application they have logged into. <br>
     * For example:
     * <p/>
     * <code>
     * authenticationService.globalLogout(userId);<br>
     * </code>
     *
     * @param userId The id of the user.
     */
    @WebMethod
    void globalLogout(
            @WebParam(name = "userId", targetNamespace = "")
            String userId) throws LogoutException;

    /*
    @WebMethod
    AuthenticationResponse passwordAuth(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "password", targetNamespace = "")
            String password) throws Exception;
	*/

    @WebMethod
    AuthenticationResponse login(
            @WebParam(name = "request", targetNamespace = "")
            AuthenticationRequest request);

    /**
     * For Single Sign On, takes the token and type of token and authenticates the user based on the token.
     * If authentication is successful returns a Subject which has principals,
     * userGroups userId, authenticating authority, credentials, token and
     * expiration time. If not successful, a null is returned.
     * <p/>
     * For example:
     * <p/>
     * <code>
     * SSOSubject sub =  authenticationService.authenticateByToken(token, tokenType);<br>
     * </code>
     *
     * @param token     - An encoded string unique for each login incidence
     * @param tokenType - Constant indicating the type of token that being passed.
     * @return SSOSubject which holds user information.
     */
    /*
    @WebMethod
    Subject authenticateByToken(
            @WebParam(name = "userId", targetNamespace = "")
            String userId,
            @WebParam(name = "token", targetNamespace = "")
            String token,
            @WebParam(name = "tokenType", targetNamespace = "")
            String tokenType) throws Exception;
	*/

    /*
    @WebMethod
    BooleanResponse validateToken(
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "token", targetNamespace = "")
            String token,
            @WebParam(name = "tokenType", targetNamespace = "")
            String tokenType) throws Exception;
	*/

    @WebMethod
    Response renewToken(
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "token", targetNamespace = "")
            String token,
            @WebParam(name = "tokenType", targetNamespace = "")
            String tokenType);

    /*
    @WebMethod
    BooleanResponse validateTokenByUser(
            @WebParam(name = "userId", targetNamespace = "")
            String userId,
            @WebParam(name = "token", targetNamespace = "")
            String token,
            @WebParam(name = "tokenType", targetNamespace = "")
            String tokenType) throws Exception;
	*/
}
