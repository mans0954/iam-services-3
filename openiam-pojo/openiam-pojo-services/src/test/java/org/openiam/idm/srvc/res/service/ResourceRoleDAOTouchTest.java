package org.openiam.idm.srvc.res.service;

import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml","classpath:test-application-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ResourceRoleDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private ResourceRoleDAO resourceRoleDAO;

    @Test
    public void touchAdd() {
       ResourceRoleEntity entity = new ResourceRoleEntity();
       entity.setId(new ResourceRoleEmbeddableId("",""));
       resourceRoleDAO.save(entity);
    }

    @Test
    public void touchFindById() {
       resourceRoleDAO.findById(new ResourceRoleEmbeddableId("",""));
    }
}
