package org.openiam.idm.srvc.res.service;

import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ResourceTypeDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;

    @Test
    public void touchFindById() {
        resourceTypeDAO.findById("");
    }

    @Test
    public void touchUpdate() {
        resourceTypeDAO.update(new ResourceTypeEntity());
    }

}
