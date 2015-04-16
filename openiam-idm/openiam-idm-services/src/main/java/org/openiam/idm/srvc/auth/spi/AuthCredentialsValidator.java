package org.openiam.idm.srvc.auth.spi;


import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.Map;

public interface AuthCredentialsValidator {

    public static final int NEW = 0;
    public static final int RENEW = 1;

    public void execute(UserEntity user, Login login, int operation, Map<String, Object> bindingMap) throws AuthenticationException;

}
