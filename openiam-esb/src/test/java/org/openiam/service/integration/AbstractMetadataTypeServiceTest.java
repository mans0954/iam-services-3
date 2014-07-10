package org.openiam.service.integration;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.AfterClass;
import org.openiam.base.AbstractMetadataTypeDTO;
import org.openiam.base.AdminResourceDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public abstract class AbstractMetadataTypeServiceTest<T extends AbstractMetadataTypeDTO, S extends AbstractKeyNameSearchBean<T, String>> extends AbstractKeyNameServiceTest<T, S> {

	@Autowired
    @Qualifier("metadataServiceClient")
    protected MetadataWebService metadataServiceClient;
	
	@BeforeClass
    protected void setUp() throws Exception {
       
    }
	
	@Test
	public void clusterTest() throws Exception {
		/* create and save */
		T instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());
		
		/* find */
		final S searchBean = newSearchBean();
		searchBean.setDeepCopy(false);
    	searchBean.setName(instance.getName());
    	
    	/* confirm save on both nodes */
    	instance = assertClusteredSave(searchBean);
    	
    	/* change name */
    	instance.setName(getRandomName());
    	response = saveAndAssert(instance);
    	
    	/* confirm update went through on both nodes */
    	searchBean.setName(instance.getName());
    	instance = assertClusteredSave(searchBean);
    	
    	/* add metadata type */
    	instance.setMdTypeId(metadataServiceClient.findTypeBeans(null, 0, 1, null).get(0).getId());
    	response = saveAndAssert(instance);
    	instance = assertClusteredSave(searchBean);
    	
    	instance.setMdTypeId(null);
    	response = saveAndAssert(instance);
    	instance = assertClusteredSave(searchBean);
    	
    	if(instance != null && instance.getId() != null) {
    		delete(instance);
    	}
	}
	
	@AfterClass
    public void tearDown() throws Exception {
    	
    }
}
