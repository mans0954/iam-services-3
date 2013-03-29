package org.openiam.authentication.integration;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataElementTemplateWebService;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class TestMetadataService extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("metadataServiceClient")
	private MetadataWebService metadataWebService;
	
	@Autowired
	@Qualifier("metadataTemplateServiceClient")
	private MetadataElementTemplateWebService templateWebService;
	
	 @BeforeClass
	 protected void setUp() throws Exception {
		 
	 }
	
	@Test
	public void testSearchTest() {
		 final MetadataTypeSearchBean typeSearchBean = new MetadataTypeSearchBean();
		 typeSearchBean.setGrouping("UI_WIDGET");
		 typeSearchBean.setActive(true);
		 List<MetadataType> typeList = metadataWebService.findTypeBeans(typeSearchBean, 0, Integer.MAX_VALUE);
		 Assert.assertTrue(CollectionUtils.isNotEmpty(typeList));
	}
	
	@Test
	public void testCreateAndDeleteEmptyTemplate() {
		/* create */
		final MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(System.currentTimeMillis() + "");
		final Response saveResponse = templateWebService.save(template);
		assertSuccess(saveResponse);
		Assert.assertNotNull(saveResponse.getResponseValue());
		
		/* delete */
		final Response deleteResponse = templateWebService.delete((String)saveResponse.getResponseValue());
		assertSuccess(deleteResponse);
		
		Assert.assertNull(templateWebService.findById((String)saveResponse.getResponseValue()));
	}
	
	@Test
	public void testCreateAndDeleteEmptyElement() {
		final MetadataType type = metadataWebService.findTypeBeans(new MetadataTypeSearchBean(), 0, Integer.MAX_VALUE).get(0);
		
		/* create */
		final MetadataElement element = new MetadataElement();
		element.setAttributeName(System.currentTimeMillis() + "");
		element.setMetadataTypeId(type.getMetadataTypeId());
		final Response saveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(saveResponse);
		Assert.assertNotNull(saveResponse.getResponseValue());
		
		/* delete */
		final Response deleteResponse = metadataWebService.deleteMetadataElement((String)saveResponse.getResponseValue());
		assertSuccess(deleteResponse);
		
		Assert.assertNull(metadataWebService.findElementById((String)saveResponse.getResponseValue()));
	}
	
	@Test
	public void testCreateUpdateAndDelete() {
		/* create */
		final MetadataType type = new MetadataType();
		type.setGrouping("" + System.currentTimeMillis());
		final Response saveResponse = metadataWebService.saveMetadataType(type);
		assertSuccess(saveResponse);
		Assert.assertNotNull(saveResponse.getResponseValue());
		
		/* delete */
		final Response deleteResponse = metadataWebService.deleteMetadataType((String)saveResponse.getResponseValue());
		assertSuccess(deleteResponse);
		
		Assert.assertNull(metadataWebService.findTypeById((String)saveResponse.getResponseValue()));
	}
	
	private void assertSuccess(final Response response) {
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getStatus());
		Assert.assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
	}
}
