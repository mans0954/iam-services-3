package org.openiam.authentication.integration;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

//@Transactional
//@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class RenewTokenTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("authServiceClient")
    private AuthenticationService authServiceClient;

    private static String token=null;
    private static AtomicInteger counter=new AtomicInteger(0);
    private static String tokenType="SSOToken";
    private static final String login="sysadmin";
    private static final String password="passwd00";

    @BeforeClass
    protected void setUp() throws Exception {
        final AuthenticationRequest authenticatedRequest = new AuthenticationRequest();
        authenticatedRequest.setClientIP("127.0.0.1");
        authenticatedRequest.setPassword(password);
        authenticatedRequest.setPrincipal(login);
        authenticatedRequest.setLanguageId("1");

        final AuthenticationResponse authenticationResponse = authServiceClient.login(authenticatedRequest);

        Assert.assertNotNull(authenticationResponse);
        Assert.assertNotNull(authenticationResponse.getStatus());

        //int errCode = authenticationResponse.getAuthErrorCode();

        Assert.assertEquals(ResponseStatus.SUCCESS.ordinal() == authenticationResponse.getStatus().ordinal(), true,
                            "Authentication Failed");
        SSOToken ssoToken = authenticationResponse.getSubject().getSsoToken();
        Assert.assertNotNull(ssoToken);

        token = ssoToken.getToken();
        tokenType = ssoToken.getTokenType();
        Assert.assertNotNull(token);
    }

    @Test(threadPoolSize = 20, invocationCount = 1000)
    //@Test
    public void renewTokenTest(){
        int threadId = counter.incrementAndGet();
        System.out.println("TRYING "+threadId);
        final Response authResponse = authServiceClient.renewToken(login, token, tokenType, null);

        Assert.assertNotNull(authResponse);
        Assert.assertNotNull(authResponse.getStatus());
        Assert.assertEquals(ResponseStatus.SUCCESS == authResponse.getStatus(), true,
                            "thread: "+threadId+"; Renew token is failed");
        if(authResponse.getResponseValue() instanceof SSOToken) {
            final SSOToken ssoToken = (SSOToken)authResponse.getResponseValue();
            if(ssoToken != null) {
                System.out.println("thread: "+threadId+"; token = " + ssoToken.getToken());
                System.out.println("thread: "+threadId+"; tokenType = " + ssoToken.getTokenType());
                System.out.println("thread: "+threadId+"; userId = " + ssoToken.getUserId());
                System.out.println("thread: "+threadId+"; principal = " + ssoToken.getPrincipal());
            }
        }
    }
}
