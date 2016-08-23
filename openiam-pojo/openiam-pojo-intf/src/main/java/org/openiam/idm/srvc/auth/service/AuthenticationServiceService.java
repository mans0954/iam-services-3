package org.openiam.idm.srvc.auth.service;

import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.base.request.AuthenticationRequest;
import org.openiam.base.request.LogoutRequest;
import org.openiam.base.response.AuthenticationResponse;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;

import java.util.List;

/**
 * Created by Vitaly on 8/6/2015.
 */

public interface AuthenticationServiceService {

    Subject login(AuthenticationRequest request)  throws BasicDataServiceException;

    void globalLogoutRequest(final LogoutRequest request) throws BasicDataServiceException;

    SSOToken renewToken(final String principal, final String token, final String tokenType, final String patternId) throws BasicDataServiceException;

    List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean, int from, int size);

    void save(final AuthStateEntity entity);

    String getOTPSecretKey(OTPServiceRequest request)  throws BasicDataServiceException;

    void sendOTPToken(final OTPServiceRequest request) throws BasicDataServiceException;

    void confirmOTPToken(final OTPServiceRequest request) throws BasicDataServiceException;

    void clearOTPActiveStatus(final OTPServiceRequest request)  throws BasicDataServiceException;

    boolean isOTPActive(final OTPServiceRequest request) throws BasicDataServiceException;
}

