package org.openiam.service.integration.sync;

import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.ws.IdentitySynchWebService;
import org.openiam.idm.srvc.synch.ws.SynchConfigResponse;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/*
*
 * Created by alexander on 17/02/16.

*/

public class SyncServiceTest extends AbstractServiceTest {

    @Autowired
    @Qualifier("syncServiceClient")
    private IdentitySynchWebService syncService;

    private static SynchConfig config=null;

    @BeforeClass(alwaysRun = true)
    public void _init() {
        config = createAdSyncConfig();
    }
    @AfterClass(alwaysRun = true)
    public void _destroy() {
        if(config!=null){
            syncService.removeConfig(config.getId());
        }
    }
    @Test
    public void adSync() throws Exception {
        SyncResponse syncResponse =  syncService.startSynchronization(config);

        Assert.assertNotNull(syncResponse);
//        Assert.assertTrue(syncResponse.getStatus() == ResponseStatus.SUCCESS);
    }


    private SynchConfig createAdSyncConfig(){
        SynchConfig config = new SynchConfig();
        config.setName("IntegrationTestADConfig");
        config.setStatus("ACTIVE");
        config.setSynchAdapter("AD");
        //TODO: need to make this configurable
        config.setSrcLoginId("DEV\\Administrator");
        //TODO: need to make this configurable
        config.setSrcPassword("OpenIAM4u!!");
        config.setSrcHost("ldap://104.196.44.120");
        config.setQuery("(&(objectClass=user)(sAMAccountName=*))");
        config.setBaseDn("OU=Test,DC=dev,DC=local");
        config.setSynchType("FULL");
        config.setProcessRule("USER");
        //TODO: need to make this configurable
        config.setValidationRule("private/an/sync/UserSyncValidationScript.groovy");
        //TODO: need to make this configurable
        config.setTransformationRule("private/an/sync/UserSyncMetaTransformationScript.groovy");
        config.setUseTransformationScript(true);
        config.setMatchFieldName("PRINCIPAL");
        config.setCustomMatchAttr("sAMAccountName");
        config.setUsePolicyMap(false);
        config.setPolicyMapBeforeTransformation(true);
        config.setUseSystemPath(false);
        config.setSearchScope(SearchScopeType.SUBTREE_SCOPE);


        SynchConfigResponse response = syncService.addConfig(config);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());

        config =response.getConfig();

        Response tcresp = syncService.testConnection(config);

        Assert.assertNotNull(tcresp);
        Assert.assertTrue(tcresp.isSuccess());

        return config;
    }
}
