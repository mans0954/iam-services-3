package org.openiam.idm.srvc.res.service;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserPrivilegeDAOTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {

    // TODO UserPrivilegeDAO is not correct autowired. But UserPrivilegeDAO is
    // not used in project
    // @Autowired
    // private UserPrivilegeDAO userPrivilegeDAO;
    //
    // @Test(enabled = false)
    // public void touchAdd() {
    // userPrivilegeDAO.add(new UserPrivilegeEntity());
    // }
    //
    // @Test(enabled = false)
    // public void touchFindAllUserPrivileges() {
    // userPrivilegeDAO.findAllUserPrivileges();
    // }
    //
    // @Test(enabled = false)
    // public void touchFindByExample() {
    // userPrivilegeDAO.findByExample(new UserPrivilegeEntity());
    // }
    //
    // @Test(enabled = false)
    // public void touchFindById() {
    // userPrivilegeDAO.findById("");
    // }
    //
    // @Test(enabled = false)
    // public void touchRemove() {
    // UserPrivilegeEntity privilegeEntity = new UserPrivilegeEntity();
    // userPrivilegeDAO.add(privilegeEntity);
    // userPrivilegeDAO.remove(privilegeEntity);
    // }
    //
    // @Test(enabled = false)
    // public void touchRemoveAllUserPrivileges() {
    // userPrivilegeDAO.removeAllUserPrivileges();
    // }
    //
    // @Test(enabled = false)
    // public void touchUpdate() {
    // UserPrivilegeEntity privilegeEntity = new UserPrivilegeEntity();
    // userPrivilegeDAO.add(privilegeEntity);
    // userPrivilegeDAO.update(privilegeEntity);
    // }

}
