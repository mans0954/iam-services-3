package org.openiam.authentication.integration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;
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
	
	@Autowired
	@Qualifier("languageServiceClient")
	private LanguageWebService languageWS;
	
	 @BeforeClass
	 protected void setUp() throws Exception {
		 
	 }
	 

	 @Test
	 public void testLanguages() {
		 Assert.assertTrue(CollectionUtils.isNotEmpty(languageWS.getAll()));
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
	public void testCreateAndDeleteType() {
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
	
	@Test
	public void testCreateUpdateAndDeleteSimpleTemplate() {
		/* create */
		MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(System.currentTimeMillis() + "");
		final Response saveResponse = templateWebService.save(template);
		assertSuccess(saveResponse);
		Assert.assertNotNull(saveResponse.getResponseValue());
		template = templateWebService.findById((String)saveResponse.getResponseValue());
		
		final String newName = "" + System.currentTimeMillis();
		template.setName(newName);
		final Response updateResponse = templateWebService.save(template);
		assertSuccess(updateResponse);
		Assert.assertEquals(templateWebService.findById(template.getId()).getName(), newName);
		
		/* delete */
		final Response deleteResponse = templateWebService.delete(template.getId());
		assertSuccess(deleteResponse);
		
		Assert.assertNull(templateWebService.findById(template.getId()));
	}
	
	@Test
	public void testMetadataTemplateXrefs() {
		final MetadataType type = metadataWebService.findTypeBeans(new MetadataTypeSearchBean(), 0, Integer.MAX_VALUE).get(0);
		
		/* create */
		MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(System.currentTimeMillis() + "");
		Response templateSaveResponse = templateWebService.save(template);
		assertSuccess(templateSaveResponse);
		Assert.assertNotNull(templateSaveResponse.getResponseValue());
		
		template = templateWebService.findById((String)templateSaveResponse.getResponseValue());
		
		/* create */
		MetadataElement element = new MetadataElement();
		element.setAttributeName(System.currentTimeMillis() + "");
		element.setMetadataTypeId(type.getMetadataTypeId());
		Response elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		Assert.assertNotNull(elementSaveResponse.getResponseValue());
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		
		/* add xref */
		MetadataElementPageTemplateXref xref = getXref(template, element, 2);
		template.addMetdataElement(xref);
		templateWebService.save(template);
		
		templateSaveResponse = templateWebService.save(template);
		assertSuccess(templateSaveResponse);
		
		template = templateWebService.findById(template.getId());
		Assert.assertTrue(CollectionUtils.isNotEmpty(template.getMetadataElements()));
		template.removeMetdataElement(xref);
		Assert.assertTrue(CollectionUtils.isEmpty(template.getMetadataElements()));
		
		templateSaveResponse = templateWebService.save(template);
		assertSuccess(templateSaveResponse);
		
		template = templateWebService.findById(template.getId());
		Assert.assertTrue(CollectionUtils.isEmpty(template.getMetadataElements()));
		
		
		/* delete */
		final Response deleteResponse = templateWebService.delete(template.getId());
		assertSuccess(deleteResponse);
		Assert.assertNull(templateWebService.findById(template.getId()));
	}
	
	private MetadataElementPageTemplateXref getXref(final MetadataElementPageTemplate template, final MetadataElement element, final Integer order) {
		final MetadataElementPageTemplateXref xref = new MetadataElementPageTemplateXref(template.getId(), element.getId(), order);
		return xref;
	}
	
	private void assertSuccess(final Response response) {
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getStatus());
		Assert.assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
	}
}
