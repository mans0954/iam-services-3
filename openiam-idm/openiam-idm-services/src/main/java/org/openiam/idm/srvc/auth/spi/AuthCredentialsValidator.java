package org.openiam.idm.srvc.auth.spi;


import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.Map;

public interface AuthCredentialsValidator {

    public void execute(UserEntity user, Login login, Map<String, Object> bindingMap) throws AuthenticationException;

}
