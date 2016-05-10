package org.openiam.service.integration.contentprovider;

import org.openiam.am.srvc.ws.URIFederationWebService;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class URIFederationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("authServiceClient")
    private AuthenticationService authServiceClient;
    
    @Test
    public void testKerberosAuthentication() {
    	final AuthenticationRequest request = new AuthenticationRequest();
    	request.setPrincipal("snelson");
    	request.setKerberosAuth(true);
    	request.setAuthPolicyId(null);
    	AuthenticationResponse response = authServiceClient.login(request);
    	Assert.assertNotNull(response);
    	Assert.assertTrue(response.getStatus().equals(ResponseStatus.SUCCESS));
    	Assert.assertNotNull(response.getSubject());
    	Assert.assertNotNull(response.getSubject().getSsoToken());
    	
    	request.setKerberosAuth(false);
    	response = authServiceClient.login(request);
    	Assert.assertNotNull(response);
    	Assert.assertTrue(response.getStatus().equals(ResponseStatus.FAILURE));
    	Assert.assertEquals(response.getAuthErrorCode(), AuthenticationConstants.RESULT_INVALID_PASSWORD);
    }
}
