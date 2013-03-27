package org.openiam.idm.srvc.meta.service;

import java.util.HashMap;
import java.util.HashSet;
import org.openiam.idm.srvc.cat.dto.Category;
import org.openiam.idm.srvc.cat.service.CategoryDataService;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

/**
 * @author zaporozhec
 */
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class MetadataServiceTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    MetadataService metadataService;

    @Autowired
    CategoryDataService categoryDataService;

    @Test
    public void getAllElementsForCategoryType() {
        metadataService.getAllElementsForCategoryType("");
    }

    @Test
    public void getMetadataElementByType() {
        metadataService.getMetadataElementByType("");
    }

    @Test
    public void getTypesInCategory() {
        metadataService.getTypesInCategory("");
    }

    private MetadataType addMetadataTypeRecord() {
        MetadataType type = new MetadataType();
        type.setElementAttributes(new HashMap<String, MetadataElement>(0));
        type.getElementAttributes().put("A1", new MetadataElement());
        type.getElementAttributes().put("A2", new MetadataElement());

        type.setCategories(new HashSet<Category>(0));
        type.getCategories().add(new Category());
        type.getCategories().add(new Category());
        return type;
    }

}
