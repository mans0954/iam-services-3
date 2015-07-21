package org.openiam.service.integration.provisioning;


import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.service.integration.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class ManagedSystemServiceTest extends AbstractServiceTest {
    private static final String connectorId = "100";
    private static final String manSysName = "Managed System Name";
    private static final String manSysNewName = "Managed System New Name";
    private ManagedSysDto managedSysDto;
    private String msId;



    @Resource(name = "manSysServiceClient")
    private ManagedSystemWebService managedSystemWebService;

    private List<String> manSysIds;

    @BeforeClass(alwaysRun = true)
    public void first() {

        managedSysDto = new ManagedSysDto();

    }

    @AfterClass(alwaysRun = true)
    public void last() {
        deleteManSys();
    }

    @Test(priority = 1)
    public void saveManSystemTest() throws Exception {

        managedSysDto.setName(manSysName);
        managedSysDto.setConnectorId(connectorId);
        Response res = managedSystemWebService.saveManagedSystem(managedSysDto);
        Assert.assertNotNull(res);
        msId = (String) res.getResponseValue();
        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(msId);
        managedSysDto.setId(msId);

    }

    @Test(priority = 2)
    public void updateManSystemTest() throws Exception {


        managedSysDto.setName(manSysNewName);
        Response res = managedSystemWebService.saveManagedSystem(managedSysDto);
        Assert.assertNotNull(res);
        String manSysId = (String) res.getResponseValue();
        Assert.assertTrue(res.isSuccess());
        Assert.assertNotNull(manSysId);

    }

    @Test(priority = 3)
    public void deleteManSystemTest() throws Exception {

        Response res = managedSystemWebService.removeManagedSystem(msId);
        Assert.assertNotNull(res);
        Assert.assertTrue(res.isSuccess());
        msId=null;
    }


    private void deleteManSys() {
        if (msId!= null) {
            Response resFirst = managedSystemWebService.removeManagedSystem(msId);

        }

    }
}
