package org.openiam.idm.srvc.res.service;

import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ResourceDAOTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private ResourceDAO resourceDAO;

    @Test
    public void touchFindByName() {
        resourceDAO.findByName("");
    }

    @Test
    public void touchGetResourcesByType() {
        resourceDAO.getResourcesByType("");
    }

    @Test
    public void touchCount() {
        resourceDAO.count(new ResourceEntity());
    }

    @Test
    public void touchCountAll() {
        resourceDAO.countAll();
    }

    @Test
    public void touchSave() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceDAO.save(resourceEntity);
    }

    @Test
    public void touchDelete() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceDAO.save(resourceEntity);
        resourceDAO.delete(resourceEntity);
    }

    @Test
    public void touchMerge() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceDAO.save(resourceEntity);
        resourceDAO.merge(resourceEntity);
    }

    @Test
    public void touchUpdate() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceDAO.save(resourceEntity);
        resourceDAO.update(resourceEntity);
    }

    @Test
    public void touchFindAll() {
        resourceDAO.findAll();
    }

    @Test
    public void touchFindById() {
        resourceDAO.findById("");
    }

    // TODO fix resourceDAO.getByExample
    @Test(enabled = false)
    public void touchGetByExample() {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceDAO.save(resourceEntity);
        resourceDAO.getByExample(resourceEntity);
    }
}
