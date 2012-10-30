package org.openiam.idm.srvc.org.service;

import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class OrganizationAttributeDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private OrganizationAttributeDAO orgAttrDAO;

    @Test
    public void touchFindById() {
        orgAttrDAO.findById("");
    }

    @Test
    public void touchFindAttributesByParent() {
        orgAttrDAO.findAttributesByParent("");
    }

    @Test
    public void touchAdd() {
        orgAttrDAO.add(new OrganizationAttribute());
    }

    @Test
    public void touchRemove() {
        OrganizationAttribute organizationAttribute = new OrganizationAttribute();
        orgAttrDAO.add(organizationAttribute);
        orgAttrDAO.remove(organizationAttribute);
    }

    @Test
    public void touchRemoveAttributesByParent() {
        orgAttrDAO.removeAttributesByParent("");
    }

    @Test
    public void touchUpdate() {
        OrganizationAttribute organizationAttribute = new OrganizationAttribute();
        orgAttrDAO.add(organizationAttribute);
        orgAttrDAO.update(organizationAttribute);
    }
}
