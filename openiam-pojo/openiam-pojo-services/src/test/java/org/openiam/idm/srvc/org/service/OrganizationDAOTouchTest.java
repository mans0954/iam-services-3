package org.openiam.idm.srvc.org.service;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class OrganizationDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private OrganizationDAO orgDAO;

    @Test
    private void touchAdd() {
        orgDAO.add(new OrganizationEntity());
    }

    @Test
    private void touchFindAllOrganization() {
        orgDAO.findAllOrganization();
    }

    @Test
    private void touchFindById() {
        orgDAO.findById("");
    }

    @Test
    private void touchFindChildOrganization() {
        orgDAO.findChildOrganization("");
    }

    @Test
    private void touchFindOrganizationByClassification() {
        orgDAO.findOrganizationByClassification("", null);
    }

    @Test
    private void touchFindOrganizationByStatus() {
        orgDAO.findOrganizationByStatus("", "");
    }

    @Test
    private void touchFindOrganizationByType() {
        orgDAO.findOrganizationByType("", "");
    }

    @Test
    private void touchFindParent() {
        orgDAO.findParent("");
    }

    @Test
    private void touchFindRootOrganizations() {
        orgDAO.findRootOrganizations();
    }

    @Test
    private void touchRemove() {
        OrganizationEntity organization = new OrganizationEntity();
        orgDAO.add(organization);
        orgDAO.remove(organization);
    }

    @Test
    private void touchSearch() {
        orgDAO.search("", "", null, "");
    }

    @Test
    private void touchUpdate() {
        OrganizationEntity organization = new OrganizationEntity();
        orgDAO.add(organization);
        orgDAO.update(organization);
    }
}
