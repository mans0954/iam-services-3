package org.openiam.idm.srvc.continfo.service;

import java.util.Collections;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml","classpath:test-application-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class PhoneDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private PhoneDAO phoneDAO;

    @Test
    public void touchAdd() {
        phoneDAO.save(new PhoneEntity());
    }

    @Test
    public void touchFindById() {
        phoneDAO.findById("");
    }

    @Test
    public void touchRemoveByUserId() {
    	phoneDAO.removeByUserId("");
    }

    @Test
    public void touchRemove() {
        phoneDAO.delete(new PhoneEntity());
    }

    @Test
    public void touch() {
        phoneDAO.update(new PhoneEntity());
    }

}
