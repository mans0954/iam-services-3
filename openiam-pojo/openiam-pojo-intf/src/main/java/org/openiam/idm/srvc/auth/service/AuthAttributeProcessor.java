package org.openiam.idm.srvc.auth.service;

import java.util.EnumMap;

import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;

public interface AuthAttributeProcessor {

    boolean process(String reflectionKey, Object object, Object setValue) throws Exception;

    public String process(final String reflectionKey, final EnumMap<AmAttributes, Object> objectMap) throws Exception;

    public String process(final String reflectionKey, final String userId, final String managedSysId) throws Exception;

}
