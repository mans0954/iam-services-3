package org.openiam.service.integration.password;

import org.junit.Assert;
import org.openiam.base.request.PasswordResetTokenRequest;
import org.openiam.base.response.PasswordResetTokenResponse;
import org.openiam.base.response.PasswordValidationResponse;
import org.openiam.base.response.ValidatePasswordResetTokenResponse;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.service.integration.AbstractServiceTest;
import org.openiam.srvc.user.PasswordWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PasswordServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("passwordServiceClient")
	protected PasswordWebService passwordService;
	
	private Policy policy;
	private Login login;
	
	@BeforeClass
	public void beforeClass() {
		policy = getPasswordPolicy();
		user = super.createUser();
		login = loginServiceClient.getPrimaryIdentity(user.getId()).getPrincipal();
	}
	
	@AfterClass
	public void afterClass() {
		if(user != null) {
			userServiceClient.deleteUser(user.getId());
		}
	}
	
	@Test
	public void testIsPasswordValid() {
		PasswordValidationResponse response =  null;
		String password = PasswordGenerator.generatePassword(policy);
		
		final Password pswd = new Password();
		pswd.setPassword(password);
		pswd.setPrincipal(login.getLogin());
		pswd.setManagedSysId(login.getManagedSysId());
		response = passwordService.isPasswordValid(pswd);
		assertSuccess(response);
		
		pswd.setPassword("a");
		response = passwordService.isPasswordValid(pswd);
		assertFailure(response);
	}
	
	@Test
	public void testGeneratePasswordResetToken() {
		final Login login = loginServiceClient.getPrimaryIdentity(user.getId()).getPrincipal();
		final PasswordResetTokenRequest request = new PasswordResetTokenRequest(login.getLogin(), login.getManagedSysId(), null);
		final PasswordResetTokenResponse response = passwordService.generatePasswordResetToken(request);
		assertSuccess(response);
		Assert.assertNotNull(response.getPasswordResetToken());
		
		final ValidatePasswordResetTokenResponse validateResponse = passwordService.validatePasswordResetToken(response.getPasswordResetToken());
		assertSuccess(validateResponse);
		Assert.assertNotNull(validateResponse.getPrincipal());
	}
	
	@Test
	public void testGetPasswordPolicy() {
		final PasswordPolicyAssocSearchBean sb = new PasswordPolicyAssocSearchBean();
		Assert.assertNotNull(passwordService.getPasswordPolicy(sb));
		
		sb.setUserId(user.getId());
		Assert.assertNotNull(passwordService.getPasswordPolicy(sb));
		
		sb.setPrincipal(login.getLogin());
		sb.setManagedSysId(login.getManagedSysId());
		Assert.assertNotNull(passwordService.getPasswordPolicy(sb));
		
		//TODO:  need a more comprehensive test
	}
}
