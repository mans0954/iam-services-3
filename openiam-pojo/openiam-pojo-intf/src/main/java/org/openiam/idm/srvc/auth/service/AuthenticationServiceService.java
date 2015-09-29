package org.openiam.idm.srvc.auth.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;

import java.util.List;

/**
 * Created by Vitaly on 8/6/2015.
 */
public interface AuthenticationServiceService {

    AuthenticationResponse login(AuthenticationRequest request);

    void globalLogout(String userId) throws Throwable;

    Response renewToken(String principal, String token, String tokenType);

    List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean,
                                    int from, int size);

    Response save(final AuthStateEntity entity);

}

