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

    @Test(enabled = false)
    public void addMetadataElement() {
        metadataService.addMetadataElement(new MetadataElement());
    }

    @Test(enabled = false)
    public void addMetadataType() {

        metadataService.addMetadataType(this.addMetadataTypeRecord());

    }

    @Test(enabled = false)
    public void addTypeToCategory() {
        MetadataType type = this.addMetadataTypeRecord();
        Category category = new Category();
        metadataService.addMetadataType(type);
        categoryDataService.addCategory(category);
        metadataService.addTypeToCategory(type.getMetadataTypeId(),
                category.getCategoryId());
    }

    @Test(enabled = false)
    public void getAllElementsForCategoryType() {
        metadataService.getAllElementsForCategoryType("");
    }

    @Test(enabled = false)
    public void getMetadataElementById() {
        metadataService.getMetadataElementById("");
    }

    @Test(enabled = false)
    public void getMetadataElementByType() {
        metadataService.getMetadataElementByType("");
    }

    @Test(enabled = false)
    public void getMetadataType() {
        metadataService.getMetadataType("");
    }

    @Test(enabled = false)
    public void getMetadataTypes() {
        metadataService.getMetadataTypes();
    }

    @Test(enabled = false)
    public void getTypesInCategory() {
        metadataService.getTypesInCategory("");
    }

    @Test(enabled = false)
    public void removeMetadataElement() {
        MetadataElement element = new MetadataElement();
        metadataService.addMetadataElement(element);
        metadataService.removeMetadataElement(element.getMetadataElementId());
    }

    @Test(enabled = false)
    public void removeMetadataType() {
        MetadataType mt = this.addMetadataTypeRecord();
        metadataService.addMetadataType(mt);
        metadataService.removeMetadataType("");
    }

    @Test(enabled = false)
    public void removeTypeFromCategory() {
        MetadataType element = new MetadataType();
        metadataService.addMetadataType(element);

        Category category = new Category();
        categoryDataService.addCategory(category);
        metadataService.removeTypeFromCategory(element.getMetadataTypeId(),
                category.getCategoryId());
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
