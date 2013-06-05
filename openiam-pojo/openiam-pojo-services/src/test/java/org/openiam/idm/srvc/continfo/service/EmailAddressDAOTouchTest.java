package org.openiam.idm.srvc.continfo.service;

import java.util.Collections;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml","classpath:test-application-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class EmailAddressDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private EmailAddressDAO emailAddressDAO;

    @Test
    public void touchAdd() {
        emailAddressDAO.save(new EmailAddressEntity());
    }

    @Test
    public void touchFindById() {
        emailAddressDAO.findById("");
    }

    @Test
    public void touchRemove() {
        emailAddressDAO.delete(new EmailAddressEntity());
    }

    @Test
    public void touchRemoveByUserId() {
        emailAddressDAO.removeByUserId("");
    }

    @Test
    public void touchUpdate() {
        emailAddressDAO.update(new EmailAddressEntity());
    }
}
