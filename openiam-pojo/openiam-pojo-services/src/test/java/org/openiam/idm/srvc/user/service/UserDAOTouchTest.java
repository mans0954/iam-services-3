package org.openiam.idm.srvc.user.service;

import java.util.Date;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations={"classpath:applicationContext-test.xml","classpath:test-application-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired
  private UserDAO userDAO;

  @BeforeMethod
  public void init() {

  }

  @Test
  public void touchAdd() {
      userDAO.save(new UserEntity());
  }

  @Test
  public void touchFindByDelegationProperties() {
      userDAO.findByDelegationProperties(new DelegationFilterSearch());
  }

  @Test
  public void touchFindById() {
      userDAO.findById("");
  }

  @Test
  public void touchRemove() {
      userDAO.delete(new UserEntity());
  }

  @Test
  public void touchUpdate() {
      UserEntity user = new UserEntity();
      userDAO.save(user);
      userDAO.update(user);
  }

}
