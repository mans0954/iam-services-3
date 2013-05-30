package org.openiam.idm.srvc.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml","classpath:test-application-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserAffiliationDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private UserAffiliationDAO userAffiliationDAO;

    @Test
    public void touchFindById() {
        userAffiliationDAO.findById("");
    }

    @Test
    public void touchFindOrgAffiliationsByUser() {
        userAffiliationDAO.findOrgAffiliationsByUser("", null);
    }
}
