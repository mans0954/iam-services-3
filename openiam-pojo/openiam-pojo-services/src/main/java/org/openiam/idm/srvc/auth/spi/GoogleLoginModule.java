package org.openiam.idm.srvc.auth.spi;

import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10.12.14.
 */
@Component("googleLoginModule")
public class GoogleLoginModule extends AbstractLoginModule {

    @Override
    protected void validate(AuthenticationContext context) throws Exception {

    }

    @Override
    protected LoginEntity getLogin(AuthenticationContext context) throws Exception {
        return null;
    }

    @Override
    protected UserEntity getUser(AuthenticationContext context, LoginEntity login) throws Exception {
        return null;
    }

    @Override
    protected Subject doLogin(AuthenticationContext context, UserEntity user, LoginEntity login) throws Exception {
        return null;
    }
}
