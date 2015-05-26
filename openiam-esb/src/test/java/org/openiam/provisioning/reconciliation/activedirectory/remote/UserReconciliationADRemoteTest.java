package org.openiam.provisioning.reconciliation.activedirectory.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.ws.ReconciliationConfigResponse;
import org.openiam.idm.srvc.recon.ws.ReconciliationWebService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    @Autowired
    @Qualifier("managedSysServiceClient")
    protected ManagedSystemWebService managedSystemWebService;

    @Autowired
    @Qualifier("provisionConnectorWebServiceClient")
    protected ProvisionConnectorWebService provisionConnectorWebServiceClient;

    private List<String> deleteConnectorIdsList = new LinkedList<String>();
    private List<String> deleteManagedSysIdsList = new LinkedList<String>();
    private List<String> deleteReconConfigIdsList = new LinkedList<String>();
    private ReconciliationConfig reconciliationConfig;

    // Powershell AD Remote Connector
    @BeforeClass(alwaysRun = true)
    private ReconciliationConfig initReconciliationConfig() {
        ProvisionConnectorDto connectorDto = new ProvisionConnectorDto();
        reconciliationConfig = new ReconciliationConfig();
        ManagedSysDto managedSysDto = new ManagedSysDto();

        try {
            connectorDto.setName("TEST-POWERSHELL-AD-CONNECTOR");
            connectorDto.setMetadataTypeId("RemoteConnector");
            connectorDto.setClientCommProtocol("CLEAR");
            connectorDto.setServiceUrl("win02.openiamdemo.com/PowershellConnectorAD/PowershellConnector.svc");
            connectorDto.setServiceNameSpace("urn:idm.openiam.org/spml2/service");
            connectorDto.setServicePort("443");
            connectorDto.setConnectorInterface("REMOTE");

            // Save Connector
            provisionConnectorWebServiceClient.addProvisionConnector(connectorDto);
            ProvisionConnectorSearchBean provisionConnectorSearchBean = new ProvisionConnectorSearchBean();
            provisionConnectorSearchBean.setConnectorName("TEST-POWERSHELL-AD-CONNECTOR");
            List<ProvisionConnectorDto> provisionConnectorDtoList = provisionConnectorWebServiceClient.getProvisionConnectors(provisionConnectorSearchBean, 0, 10);
            Assert.assertNotNull(provisionConnectorDtoList);
            Assert.assertEquals(provisionConnectorDtoList.size(), 1);
            connectorDto = provisionConnectorDtoList.get(0);
            deleteConnectorIdsList.add(connectorDto.getConnectorId());

            managedSysDto.setName("TEST-POWERSHELL-AD");
            managedSysDto.setDecryptPassword("=tdWk2eqV8P");
            managedSysDto.setConnectorId(connectorDto.getConnectorId());
            managedSysDto.setHostUrl("win02.ad.openiamdemo.info");
            managedSysDto.setStatus("ACTIVE");
            managedSysDto.setUserId("Administrator");
            managedSysDto.setAddHandler("ADPowershell.ps1");
            managedSysDto.setModifyHandler("ADPowershell.ps1");
            managedSysDto.setDeleteHandler("ADPowershell.ps1");
            managedSysDto.setPasswordHandler("ADPowershell.ps1");
            managedSysDto.setSuspendHandler("ADPowershell.ps1");
            managedSysDto.setSearchHandler("ADPowershell.ps1");
            managedSysDto.setLookupHandler("ADPowershell.ps1");
            managedSysDto.setTestConnectionHandler("ADPowershell.ps1");
            managedSysDto.setReconcileResourceHandler("ADPowershell.ps1");
            managedSysDto.setHandler5("ADPowershell.ps1");
            managedSysDto.setAttributeNamesHandler("ADPowershell.ps1");
            managedSysDto.setSearchScope(SearchScopeType.SUBTREE_SCOPE);
            managedSysDto.setResumeHandler("ADPowershell.ps1");
            managedSysDto.setSkipGroupProvision(Boolean.TRUE);
            managedSysDto.setChangedByEndUser(false);
            managedSysDto.setDescription("UserReconciliationADRemoveTest_Connector");

            // Save ManagedSystem
            Response response = managedSystemWebService.saveManagedSystem(managedSysDto);
            Assert.assertTrue(response.isSuccess());
            Assert.assertNotNull(response.getResponseValue());

            String mngSysId = (String) response.getResponseValue();
            deleteManagedSysIdsList.add(mngSysId);

            ManagedSysDto managedSysDto_1 = managedSystemWebService.getManagedSys(mngSysId);
            Assert.assertNotNull(managedSysDto_1);
            Assert.assertEquals(managedSysDto_1.getName(), "TEST-POWERSHELL-AD");

            reconciliationConfig.setName("Test Powershell AD Reconciliation");
            reconciliationConfig.setManagedSysId(managedSysDto_1.getId());
            reconciliationConfig.setResourceId(managedSysDto_1.getResourceId());
            reconciliationConfig.setTargetSystemMatchScript("recon/PowershellADSearchQuery.groovy");
            reconciliationConfig.setMatchFieldName("MANAGED_SYS_PRINCIPAL");
            reconciliationConfig.setCustomMatchAttr("SamAccountName");
            reconciliationConfig.setSearchFilter("{\"lastName\" : \"NONE\"}");
            reconciliationConfig.setTargetSystemSearchFilter("(&(objectClass=user)(samaccountname=*))");
            reconciliationConfig.setMatchScript("recon/UserSearchScript.groovy");
            reconciliationConfig.setReconType("USER");

            Set<ReconciliationSituation> reconciliationSituationSet = new HashSet<ReconciliationSituation>();
            ReconciliationSituation reconciliationSituation1 = new ReconciliationSituation();
            reconciliationSituation1.setSituation("IDM[not exists] and Resource[exists]");
            reconciliationSituation1.setSituationResp("ADD_TO_IDM");
            reconciliationSituation1.setScript("recon/PowershellADPopulationScript.groovy");
            reconciliationSituationSet.add(reconciliationSituation1);

            ReconciliationSituation reconciliationSituation2 = new ReconciliationSituation();
            reconciliationSituation2.setSituation("IDM[exists] and Resource[exists]");
            reconciliationSituation2.setSituationResp("UPDATE_IDM_FROM_RES");
            reconciliationSituation2.setScript("recon/PowershellADPopulationScript.groovy");
            reconciliationSituationSet.add(reconciliationSituation2);

            ReconciliationSituation reconciliationSituation3 = new ReconciliationSituation();
            reconciliationSituation3.setSituation("IDM[exists] and Resource[not exists]");
            reconciliationSituation3.setSituationResp("NOTHING");
            reconciliationSituation3.setScript("recon/PowershellADPopulationScript.groovy");
            reconciliationSituationSet.add(reconciliationSituation3);

            ReconciliationSituation reconciliationSituation4 = new ReconciliationSituation();
            reconciliationSituation4.setSituation("IDM[deleted] and Resource[exists]");
            reconciliationSituation4.setSituationResp("NOTHING");
            reconciliationSituation4.setScript("recon/PowershellADPopulationScript.groovy");
            reconciliationSituationSet.add(reconciliationSituation4);

            reconciliationConfig.setSituationSet(reconciliationSituationSet);

            // Save ReconciliationConfig
            ReconciliationConfigResponse reconciliationConfigResponse = reconciliationWebService.addConfig(reconciliationConfig);
            Assert.assertTrue(reconciliationConfigResponse.isSuccess());
            Assert.assertNotNull(reconciliationConfigResponse.getConfig());
            reconciliationConfig = reconciliationConfigResponse.getConfig();
            deleteReconConfigIdsList.add(reconciliationConfig.getReconConfigId());

            reconciliationConfigResponse = reconciliationWebService.getConfigById(reconciliationConfig.getReconConfigId());
            Assert.assertTrue(reconciliationConfigResponse.isSuccess());
            ReconciliationConfig reconciliationConfig1 = reconciliationConfigResponse.getConfig();
            Assert.assertNotNull(reconciliationConfig1);
            Assert.assertEquals(reconciliationConfig1.getName(), "Test Powershell AD Reconciliation");
            Assert.assertNotNull(reconciliationConfig1.getSituationSet());
            Assert.assertEquals(reconciliationConfig1.getSituationSet().size(), 4);


            



        } catch(Throwable t) {
            reconciliationConfig = null;
            this._destroy();
            t.printStackTrace();
        }
        return reconciliationConfig;
    }

    @AfterClass(alwaysRun = true)
    public void _destroy() {
        for(String sysId : deleteManagedSysIdsList) {
            managedSystemWebService.removeManagedSystem(sysId);
        }
        for(String recId : deleteReconConfigIdsList) {
            reconciliationWebService.removeConfig(recId, null);
        }
        for(String conId : deleteConnectorIdsList) {
            provisionConnectorWebServiceClient.removeProvisionConnector(conId);
        }
    }


    @Test
    public void testUserReconciliation() throws Exception {
        Assert.assertTrue(true);
        Assert.assertNotNull(reconciliationConfig);

       // ReconciliationConfig reconciliationConfig = getReconciliationConfig_1();


        //reconciliationWebService.startReconciliation(reconciliationConfig);


    }



}
