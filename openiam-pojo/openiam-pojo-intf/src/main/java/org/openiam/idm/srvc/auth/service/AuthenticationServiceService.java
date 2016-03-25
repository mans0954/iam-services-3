package org.openiam.idm.srvc.auth.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.OTPServiceRequest;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;

import java.util.List;

/**
 * Created by Vitaly on 8/6/2015.
 */

public interface AuthenticationServiceService {

    AuthenticationResponse login(AuthenticationRequest request);

    void globalLogout(String userId) throws Throwable;

    public Response renewToken(final String principal, final String token, final String tokenType, final String patternId);
    List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean,
                                    int from, int size);

    Response save(final AuthStateEntity entity);

    public Response getOTPSecretKey(OTPServiceRequest request);

    public Response sendOTPToken(final OTPServiceRequest request);

    public Response confirmOTPToken(final OTPServiceRequest request);

    public Response clearOTPActiveStatus(final OTPServiceRequest request);

    public boolean isOTPActive(final OTPServiceRequest request);

    public Response globalLogoutRequest(final LogoutRequest request);
    }

