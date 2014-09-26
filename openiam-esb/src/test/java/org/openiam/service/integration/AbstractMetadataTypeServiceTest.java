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
	
	@Override
	public ClusterKey<T, S> doClusterTest() throws Exception {
		ClusterKey<T, S> key = super.doClusterTest();
		T instance = key.getDto();
		S searchBean = key.getSearchBean();
		
		instance.setMdTypeId(metadataServiceClient.findTypeBeans(null, 0, 1, null).get(0).getId());
    	Response response = saveAndAssert(instance);
    	instance = assertClusteredSave(searchBean);
    	
    	instance.setMdTypeId(null);
    	response = saveAndAssert(instance);
    	instance = assertClusteredSave(searchBean);
    	return key;
    	
	}
	
	@AfterClass
    public void tearDown() throws Exception {
    	
    }
}
