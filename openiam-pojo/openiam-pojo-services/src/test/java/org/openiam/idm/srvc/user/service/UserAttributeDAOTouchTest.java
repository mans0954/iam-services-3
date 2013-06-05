package org.openiam.idm.srvc.user.service;

import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserAttributeDAOTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private UserAttributeDAO userAttributeDAO;

    @Test
    public void touchAdd() {
        userAttributeDAO.save(new UserAttributeEntity());
    }

    @Test
    public void touchAttachClean() {
        UserAttributeEntity userAttribute = new UserAttributeEntity();
        userAttributeDAO.save(userAttribute);
        userAttributeDAO.attachClean(userAttribute);
    }

    @Test
    public void touchAttachDirty() {
        userAttributeDAO.attachDirty(new UserAttributeEntity());
    }

    @Test
    public void touchDeleteUserAttributes() {
        userAttributeDAO.deleteUserAttributes("");
    }

    @Test
    public void touchFindById() {
        userAttributeDAO.findById("");
    }

    @Test
    public void touchFindUserAttributes() {
        userAttributeDAO.findUserAttributes("");
    }

    @Test
    public void touchRemove() {
        userAttributeDAO.delete(new UserAttributeEntity());
    }

    @Test
    public void touchUpdate() {
        UserAttributeEntity userAttribute = new UserAttributeEntity();
        userAttributeDAO.save(userAttribute);
        userAttributeDAO.update(userAttribute);
    }
}
