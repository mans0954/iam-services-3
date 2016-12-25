package org.openiam.service.integration;

import org.junit.AfterClass;
import org.openiam.base.AbstractMetadataTypeDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.testng.annotations.BeforeClass;

public abstract class AbstractMetadataTypeServiceTest<T extends AbstractMetadataTypeDTO, S extends AbstractKeyNameSearchBean<T, String>> extends AbstractKeyNameServiceTest<T, S> {

	@BeforeClass
    protected void setUp() throws Exception {
       
    }
	
	@Override
	public ClusterKey<T, S> doClusterTest() throws Exception {
		ClusterKey<T, S> key = super.doClusterTest();
		T instance = key.getDto();
		S searchBean = key.getSearchBean();
		
		instance.setMdTypeId(metadataServiceClient.findTypeBeans(null, 0, 1).get(0).getId());
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
