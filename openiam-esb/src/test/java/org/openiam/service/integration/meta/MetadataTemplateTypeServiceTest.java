package org.openiam.service.integration.meta;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.srvc.common.MetadataElementTemplateWebService;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class MetadataTemplateTypeServiceTest extends AbstractServiceTest {
	
	@Autowired
	@Qualifier("metadataTemplateServiceClient")
	private MetadataElementTemplateWebService metadataTemplateServiceClient;

	@Test
	public void testFindBeans() {
		final List<MetadataTemplateType> templateTypes = metadataTemplateServiceClient.findTemplateTypes(new MetadataTemplateTypeSearchBean(), 0, Integer.MAX_VALUE);
		Assert.assertTrue(String.format("No template types found"), CollectionUtils.isNotEmpty(templateTypes));
		for(final MetadataTemplateType type : templateTypes) {
			Assert.assertNotNull(String.format("Template type with ID %s not found", type.getId()), metadataTemplateServiceClient.getTemplateType(type.getId()));
		}
	}
	
	

}
