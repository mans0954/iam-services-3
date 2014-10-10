package org.openiam.idm.srvc.pswd.service;

import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.user.domain.UserEntity;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/3/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PasswordPolicyProvider {
	
	public Policy getPasswordPolicyByUser(final String userId, final String contentProviderId);
	
	public Policy getPasswordPolicyByUser(final UserEntity user, final String contentProviderId);

    public Policy getGlobalPasswordPolicy();
}
