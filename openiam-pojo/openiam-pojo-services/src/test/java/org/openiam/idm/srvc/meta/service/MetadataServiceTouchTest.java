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
    public void addMetadataElement() {
        metadataService.addMetadataElement(new MetadataElement());
    }

    @Test
    public void addMetadataType() {
        metadataService.addMetadataType(this.addMetadataTypeRecord());
    }

    @Test
    public void addTypeToCategory() {
        metadataService
                .addTypeToCategory(
                        metadataService.addMetadataType(
                                this.addMetadataTypeRecord())
                                .getMetadataTypeId(), categoryDataService
                                .addCategory(new Category()).getCategoryId());
    }

    @Test
    public void getAllElementsForCategoryType() {
        metadataService.getAllElementsForCategoryType("");
    }

    @Test
    public void getMetadataElementById() {
        metadataService.getMetadataElementById("");
    }

    @Test
    public void getMetadataElementByType() {
        metadataService.getMetadataElementByType("");
    }

    @Test
    public void getMetadataType() {
        metadataService.getMetadataType("");
    }

    @Test
    public void getMetadataTypes() {
        metadataService.getMetadataTypes();
    }

    @Test
    public void getTypesInCategory() {
        metadataService.getTypesInCategory("");
    }

    @Test
    public void removeMetadataElement() {
        metadataService.removeMetadataElement(metadataService
                .addMetadataElement(new MetadataElement())
                .getId());
    }

    @Test
    public void removeMetadataType() {
        MetadataType mt = metadataService.addMetadataType(this
                .addMetadataTypeRecord());
        metadataService.removeMetadataType(mt.getMetadataTypeId());
    }

    @Test
    public void removeTypeFromCategory() {
        metadataService
                .removeTypeFromCategory(
                        metadataService.addMetadataType(new MetadataType())
                                .getMetadataTypeId(), categoryDataService
                                .addCategory(new Category()).getCategoryId());
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
