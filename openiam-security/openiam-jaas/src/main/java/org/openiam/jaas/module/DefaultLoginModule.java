package org.openiam.jaas.module;

import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.jaas.callback.TokenCallback;
import org.openiam.jaas.util.ServiceLookupHelper;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DefaultLoginModule extends AbstractLoginModule {
    private  String token;
    private String userId;

    @Override
    protected void gatheringUserInfo() throws LoginException{
        Callback[] callbacks = new Callback[]{new NameCallback("user name:"),
                                              new PasswordCallback("password:", false),
                                              new TokenCallback("token:", false)};
        log.debug("Callback array has been set.");
        log.debug("Callback hanlder =" + callbackHandler);

        try {
            callbackHandler.handle(callbacks);
            /*get user principal*/
            username = ((NameCallback)callbacks[0]).getName();
            /*get user password*/
            password = new String(((PasswordCallback)callbacks[1]).getPassword());
            ((PasswordCallback)callbacks[1]).clearPassword();
            /*get user token and Id*/
            token = new String(((TokenCallback)callbacks[2]).getSecurityToken());
            userId =  ((TokenCallback)callbacks[2]).getUserId();

        } catch (IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            System.out.println("UnsupportedCallbackException - ");
            uce.printStackTrace();

            throw new LoginException("Error: " + uce.getCallback().toString() +
                                     " not available to garner authentication information " +
                                     "from the user");
        }
    }

    protected boolean processLogin() throws LoginException{
        try {

            int resultCode = AuthenticationConstants.RESULT_SUCCESS;

            if (this.token == null || token.isEmpty()) {
                // password authentication
                log.debug("Executing password authentication");
                final AuthenticationRequest authenticatedRequest = new AuthenticationRequest();
    			//authenticatedRequest.setClientIP(request.getRemoteAddr());
    			authenticatedRequest.setPassword(password);
    			authenticatedRequest.setPrincipal(username);
    			try {
    				authenticatedRequest.setNodeIP(InetAddress.getLocalHost().getHostAddress());
    			} catch (UnknownHostException e) {
    				
    			}
    			AuthenticationResponse resp = ServiceLookupHelper.getAuthenticationService().login(authenticatedRequest);
                
                /*
                AuthenticationResponse resp = ServiceLookupHelper.getAuthenticationService().passwordAuth(jaasConfiguration.getSecurityDomain(),
                                                                                                          username,
                                                                                                          password);
				*/
                resultCode = resp.getAuthErrorCode();
                iamSubject = resp.getSubject();
                if(iamSubject!=null && iamSubject.getSsoToken()!=null)
                    log.debug("Token from password auth=" + iamSubject.getSsoToken().getToken());
            }else {
                // token is present. Carry out token authenticaiton
                log.debug("Executing token authentication");
                log.debug("LOGIN MOD -> userName=" + username);
                log.debug("LOGIN MOD -> token=" + token);
                iamSubject = (org.openiam.idm.srvc.auth.dto.Subject)ServiceLookupHelper.getAuthenticationService().renewToken(username, token, AuthenticationConstants.OPENIAM_TOKEN).getResponseValue();
                /*
                iamSubject = ServiceLookupHelper.getAuthenticationService().authenticateByToken(userId, token,
                                                                                                AuthenticationConstants.OPENIAM_TOKEN);
				*/
                resultCode = iamSubject.getResultCode();
            }

            switch (resultCode) {
                case AuthenticationConstants.RESULT_INVALID_DOMAIN:
                    throw new FailedLoginException("RESULT_INVALID_DOMAIN");
                case AuthenticationConstants.RESULT_INVALID_LOGIN:
                    throw new FailedLoginException("RESULT_INVALID_LOGIN");
                case AuthenticationConstants.RESULT_INVALID_PASSWORD:
                    throw new FailedLoginException("RESULT_INVALID_PASSWORD");
                case AuthenticationConstants.RESULT_INVALID_USER_STATUS:
                    throw new FailedLoginException("RESULT_INVALID_USER_STATUS");
                case AuthenticationConstants.RESULT_LOGIN_DISABLED:
                    throw new FailedLoginException("RESULT_LOGIN_DISABLED");
                case AuthenticationConstants.RESULT_LOGIN_LOCKED:
                    throw new FailedLoginException("RESULT_LOGIN_LOCKED");
                case AuthenticationConstants.RESULT_PASSWORD_EXPIRED:
                    throw new FailedLoginException("RESULT_PASSWORD_EXPIRED");
                case AuthenticationConstants.RESULT_INVALID_TOKEN:
                    throw new FailedLoginException("RESULT_INVALID_TOKEN");
                case AuthenticationConstants.RESULT_SERVICE_NOT_FOUND:
                    throw new FailedLoginException("INVALID");
            }

            this.success = true;
            return true;
        }catch(FailedLoginException ae) {
            log.error(ae.getMessage(), ae);
            throw  ae;
        } catch (Exception ex){
            log.error(ex.getMessage(), ex);
            throw new FailedLoginException("INVALID");
        }
    }
}
