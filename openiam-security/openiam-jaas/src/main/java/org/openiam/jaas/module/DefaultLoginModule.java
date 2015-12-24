package org.openiam.jaas.module;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Arrays;

public class DefaultLoginModule extends AbstractLoginModule {
    private  String token;
    private String userId;
    
	private static boolean initialized = false;
	private static byte[] key = null;
	private static SecureRandom secureRandom;
    
    private static final String AUTH_TOKEN_HEADER = "x-openiam-auth-token";
    
	private void init() {
		if(!initialized) {
			try {
				key = ServiceLookupHelper.getKeyManagementService().getCookieKey();
				secureRandom = new SecureRandom();
				initialized = true;
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Can't get Cookie Key, will retry..", e);
			}
		}
	}
    
	public String decode(final String base64CookieValue) throws Exception {
		init();
		final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
		byte[] iv = new byte[cipher.getBlockSize()];
		final byte[] input =  DatatypeConverter.parseBase64Binary(base64CookieValue);
		System.arraycopy(input, 0, iv, 0, iv.length);
		cipher.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		final byte[] output = new byte[cipher.getOutputSize(input.length - iv.length)];
		final int len = cipher.processBytes(input, iv.length, input.length - iv.length, output, 0);
		cipher.doFinal(output, len);        	  
		return new String(output);
	}

    @Override
    protected void gatheringUserInfo() throws LoginException{
    	/*
        Callback[] callbacks = new Callback[]{new NameCallback("user name:"),
                                              new PasswordCallback("password:", false),
                                              new TokenCallback("token:", false)};
		*/
        System.out.println("Callback array has been set.");
        System.out.println("Callback hanlder =" + callbackHandler);

        try {
        	final HttpServletRequest request = getHttpServletRequest();
        	String authToken = request.getHeader(AUTH_TOKEN_HEADER);
        	if(authToken == null || authToken.trim().isEmpty()) {
        		throw new RuntimeException(String.format("%s header did not have a value", AUTH_TOKEN_HEADER));
        	}
        	
        	authToken = decode(authToken);
        	final String[] tokenSplit = authToken.split("\\|");
        	//userId = tokenSplit[0];
        	username = tokenSplit[1];
        	token = tokenSplit[2];
        	
        	/*
        	if(token == null || token.trim().isEmpty()) {
        		if(request.getCookies() != null) {
        			for(final Cookie cookie : request.getCookies()) {
        				if(cookie.getName() != null && cookie.getName().equalsIgnoreCase("OPENIAM_AUTH_TOKEN")) {
        					token = cookie.getValue();
        				}
        			}
        		}
        	}
        	*/
        	System.out.println("Split token" + Arrays.toString(tokenSplit));
        	System.out.println(String.format("authToken: %s", authToken));
        	System.out.println(String.format("Token: %s", token));
        	System.out.println(String.format("Username: %s", username));
        	final Response wsResponse = ServiceLookupHelper.getAuthenticationService().renewToken(username, token, AuthenticationConstants.OPENIAM_TOKEN);
        	if(wsResponse.isFailure()) {
        		throw new LoginException(String.format("Cannot renew token: %s", wsResponse));
        	}
        	ssoToken = ((SSOToken)wsResponse.getResponseValue());
        	userId = ssoToken.getUserId();
        	
        	/*
            callbackHandler.handle(callbacks);
            //get user principal
            username = ((NameCallback)callbacks[0]).getName();
            //get user password
            password = new String(((PasswordCallback)callbacks[1]).getPassword());
            ((PasswordCallback)callbacks[1]).clearPassword();
            //get user token and Id
            token = new String(((TokenCallback)callbacks[2]).getSecurityToken());
            userId =  ((TokenCallback)callbacks[2]).getUserId();

            */
        } catch(Throwable e) {
        	log.warn("Error while getting authentication information", e);
        	throw new LoginException("Exception while getting authentiction information");
        	/*
        	throw new LoginException("Error: " + uce.getCallback().toString() +
                    " not available to garner authentication information " +
                    "from the user");
			*/
        /*
        } catch (IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            System.out.println("UnsupportedCallbackException - ");
            log.error("Error in gatheringUserInfo", uce);

            throw new LoginException("Error: " + uce.getCallback().toString() +
                                     " not available to garner authentication information " +
                                     "from the user");
		*/
        }
    }

    /*
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
    */
}
