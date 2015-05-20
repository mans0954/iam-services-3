package org.openiam.provisioning.reconciliation.activedirectory.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.ws.ReconciliationWebService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class UserReconciliationADRemoteTest extends AbstractTestNGSpringContextTests {


    private static final Log log = LogFactory.getLog(UserReconciliationADRemoteTest.class);

    @Autowired
    @Qualifier("provisionServiceClient")
    private ProvisionService provisionService;

    @Autowired
    @Qualifier("loginServiceClient")
    private LoginDataWebService loginServiceClient;

    @Autowired
    @Qualifier("metadataServiceClient")
    protected MetadataWebService metadataServiceClient;

    @Autowired
    @Qualifier("userServiceClient")
    private UserDataWebService userServiceClient;

    @Autowired
    @Qualifier("languageServiceClient")
    protected LanguageWebService languageServiceClient;

    @Autowired
    @Qualifier("auditServiceClient")
    protected IdmAuditLogWebDataService auditLogService;

    @Autowired
    @Qualifier("reconciliationServiceClient")
    protected ReconciliationWebService reconciliationWebService;

    private ReconciliationConfig getReconciliationConfig_1() {
        ReconciliationConfig reconciliationConfig = new ReconciliationConfig();
        //TODO initialization
        reconciliationConfig.setName("");
        return reconciliationConfig;
    }


    @Test
    public void testUserReconciliation() throws Exception {
        Assert.assertTrue(true);
        ReconciliationConfig reconciliationConfig = getReconciliationConfig_1();


        reconciliationWebService.startReconciliation(reconciliationConfig);


    }



}
