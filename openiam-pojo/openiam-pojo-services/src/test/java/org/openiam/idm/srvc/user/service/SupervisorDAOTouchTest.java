package org.openiam.idm.srvc.user.service;

import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SupervisorDAOTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SupervisorDAO supervisorDao;

    @Test
    public void touchAdd() {
        supervisorDao.save(new SupervisorEntity());
    }

    @Test
    public void touchFindByExample() {
        supervisorDao.getByExample(new SupervisorEntity());
    }

    @Test
    public void touchFindById() {
        supervisorDao.findById("");
    }

    @Test
    public void touchFindEmployees() {
        supervisorDao.findEmployees("");
    }

    @Test
    public void touchFindPrimarySupervisor() {
        supervisorDao.findPrimarySupervisor("");
    }

    @Test
    public void touchFindSupervisors() {
        supervisorDao.findSupervisors("");
    }

    @Test
    public void touchRemove() {
        supervisorDao.delete(new SupervisorEntity());
    }

    @Test
    public void touchUpdate() {
        SupervisorEntity entity = new SupervisorEntity();
        supervisorDao.save(entity);
        supervisorDao.update(entity);
    }
}
