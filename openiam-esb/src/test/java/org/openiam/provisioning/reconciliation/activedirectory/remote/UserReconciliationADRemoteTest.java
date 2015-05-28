package org.openiam.provisioning.reconciliation.activedirectory.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.runner.RunWith;
import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.ws.KeyManagementWS;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
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
    public static final String TestUserSamAccountName = "sys.user";

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

    @Autowired
    @Qualifier("metadataServiceClient")
    protected MetadataWebService metadataWebService;

    private List<String> deleteConnectorIdsList = new LinkedList<String>();
    private List<String> deleteManagedSysIdsList = new LinkedList<String>();
    private List<String> deleteReconConfigIdsList = new LinkedList<String>();
    private List<String> deleteReconSituationIdsList = new LinkedList<String>();
    private List<String> deleteMngSysObjectMatchIdsList = new LinkedList<String>();

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
            managedSysDto.setPswd("=tdWk2eqV8P");
            managedSysDto.setConnectorId(connectorDto.getConnectorId());
            managedSysDto.setHostUrl("win02.ad.openiamdemo.info");
            managedSysDto.setStatus("ACTIVE");
            managedSysDto.setUserId("AD\\Administrator");
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


         //   managedSysDto.setMngSysObjectMatchs(objectMatches);

            // Save ManagedSystem
            Response saveMngSysResponse = managedSystemWebService.saveManagedSystem(managedSysDto);
            Assert.assertTrue(saveMngSysResponse.isSuccess());
            Assert.assertNotNull(saveMngSysResponse.getResponseValue());

            String mngSysId = (String) saveMngSysResponse.getResponseValue();
            deleteManagedSysIdsList.add(mngSysId);

         // Add Object Match
            ManagedSystemObjectMatch managedSystemObjectMatch = new ManagedSystemObjectMatch();
            managedSystemObjectMatch.setBaseDn("dc=ad,dc=openiamdemo,dc=info");
            managedSystemObjectMatch.setKeyField("samaccountname");
            managedSystemObjectMatch.setObjectType("USER");
            managedSystemObjectMatch.setSearchBaseDn("dc=ad,dc=openiamdemo,dc=info");
            managedSystemObjectMatch.setManagedSys(mngSysId);
            Response saveManagedSysObjMatchResponse = managedSystemWebService.saveManagedSystemObjectMatch(managedSystemObjectMatch);
            Assert.assertNotNull(saveManagedSysObjMatchResponse);
            Assert.assertTrue(saveManagedSysObjMatchResponse.isSuccess());

            //
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
            reconciliationConfig.setTargetSystemSearchFilter("(&(objectClass=user)(samaccountname="+ TestUserSamAccountName +"))");
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


            // Try to find the user in OpenIAM
            LoginResponse loginResponse = loginServiceClient.getLoginByManagedSys(TestUserSamAccountName, mngSysId);
            Assert.assertNotNull(loginResponse);
            if(loginResponse.getPrincipal() != null) {
                Login userPrincipal = loginResponse.getPrincipal();
                // TODO check this functionality
                userServiceClient.removeUser(userPrincipal.getUserId());
                // TODO check this functionality
                loginServiceClient.deleteLogin(userPrincipal.getLoginId());
            }

            // TODO CHECK MetadataTypes PRIMARY_LOCATION
            MetadataType metadataType = metadataWebService.getMetadataTypeById("PRIMARY_LOCATION");
            Assert.assertNotNull(metadataType);

        } catch(Throwable t) {
            reconciliationConfig = null;
            this._destroy();
            t.printStackTrace();
        }
        return reconciliationConfig;
    }

    @AfterClass(alwaysRun = true)
    public void _destroy() {
        try {
            for(String sysId : deleteManagedSysIdsList) {
                managedSystemWebService.removeManagedSystem(sysId);
            }
        } catch(Throwable t) {
            // do nothing
        }
        try {
            for(String recId : deleteReconConfigIdsList) {
                reconciliationWebService.removeConfig(recId, null);
            }
        } catch(Throwable t) {
            // do nothing
        }
        try {
            for (String conId : deleteConnectorIdsList) {
                provisionConnectorWebServiceClient.removeProvisionConnector(conId);
            }
        } catch(Throwable t) {
            // do nothing
        }
    }


    @Test
    public void testUserReconciliation() throws Exception {
        Assert.assertTrue(true);
        Assert.assertNotNull(reconciliationConfig);

        Response testConnectionResponse = provisionService.testConnectionConfig(reconciliationConfig.getManagedSysId(),"3000");
        Assert.assertNotNull(testConnectionResponse);
        Assert.assertTrue(testConnectionResponse.isSuccess());

        //SET Timeout for waiting WS response
        setWSClientTimeout(reconciliationWebService, 600000L);
        reconciliationWebService.startReconciliation(reconciliationConfig);


        int i = 0;

    }


    public static void setWSClientTimeout(Object wsService, long timeout) {
        Client client = ClientProxy.getClient(wsService);
        if (client != null) {
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(timeout);
            policy.setReceiveTimeout(timeout);
            conduit.setClient(policy);
        }

    }
}
