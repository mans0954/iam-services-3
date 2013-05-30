package org.openiam.idm.srvc.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml","classpath:test-application-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class OrganizationDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private OrganizationDAO orgDAO;

    @Test
    private void touchFindAllOrganization() {
        orgDAO.findAllOrganization();
    }

    @Test
    private void touchFindById() {
        orgDAO.findById("");
    }

    @Test
    private void touchFindRootOrganizations() {
        orgDAO.findRootOrganizations();
    }
    
    @Test
    private void touchGetChildOrganizations() {
    	orgDAO.getChildOrganizations("",null, 0, Integer.MAX_VALUE);
    }
    
    @Test
    private void touchGetParentOrganizations() {
    	orgDAO.getParentOrganizations("",null, 0, Integer.MAX_VALUE);
    }
}
