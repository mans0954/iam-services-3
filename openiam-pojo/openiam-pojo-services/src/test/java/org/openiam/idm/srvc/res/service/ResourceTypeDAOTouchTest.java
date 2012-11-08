package org.openiam.idm.srvc.res.service;

import org.openiam.idm.srvc.res.dto.ResourceType;
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
    private void touchAdd() {
        resourceTypeDAO.add(new ResourceType());
    }

    @Test
    private void touchFindAllResourceTypes() {
        resourceTypeDAO.findAllResourceTypes();
    }

    @Test
    private void touchFindByExample() {
        resourceTypeDAO.findByExample(new ResourceType());
    }

    @Test
    private void touchFindById() {
        resourceTypeDAO.findById("");
    }

    @Test
    private void touchRemove() {
        ResourceType resourceType = new ResourceType();
        resourceTypeDAO.add(resourceType);
        resourceTypeDAO.remove(resourceType);
    }

    @Test
    private void touchRemoveAllResourceTypes() {
        resourceTypeDAO.removeAllResourceTypes();
    }

    @Test
    private void touchUpdate() {
        resourceTypeDAO.update(new ResourceType());
    }

}
