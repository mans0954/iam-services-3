package org.openiam.idm.srvc.cat.service;

import org.openiam.idm.srvc.cat.dto.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class CategoryTestTouch extends
        AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    CategoryDataService categoryDataService;

    @Test
    public void addCategory() {
        categoryDataService.addCategory(new Category());
    }

    @Test
    public void getAllCategoriesTRUE() {
        categoryDataService.getAllCategories(true);
    }

    @Test
    public void getAllCategoriesFALSE() {
        categoryDataService.getAllCategories(false);
    }

    @Test
    public void getCategory() {
        Category cat = new Category();
        cat = categoryDataService.addCategory(cat);
        categoryDataService.getCategory(cat.getCategoryId());
    }

    @Test
    public void getChildCategories() {
        categoryDataService.getChildCategories("", true);
    }

    @Test
    public void removeCategory() {
        categoryDataService
                .removeCategory(categoryDataService.addCategory(new Category())
                        .getCategoryId(), true);
    }

    @Test
    public void updateCategory() {
        Category cat = new Category();
        cat = categoryDataService.addCategory(cat);
        cat.setDisplayOrder(14);
        categoryDataService.updateCategory(cat);
    }
}
