package org.openiam.authentication.integration;

import groovy.xml.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.domain.WhereClauseConstants;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.dto.MetadataValidValue;
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
	
	@Test
	public void testMetdataEntityCollections() {
		final List<Language> languageList = languageWS.getAll();
		final MetadataType type = metadataWebService.findTypeBeans(new MetadataTypeSearchBean(), 0, Integer.MAX_VALUE).get(0);
		
		MetadataElement element = new MetadataElement();
		element.setAttributeName(System.currentTimeMillis() + "");
		element.setMetadataTypeId(type.getMetadataTypeId());
		
		Response elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		Assert.assertNotNull(elementSaveResponse.getResponseValue());
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		
		element.setDefaultValueLanguageMap(getLanguageMap(element.getId(), WhereClauseConstants.META_ELEMENT_DEFAULT_VALUE_REFERENCE_TYPE));
		element.setLanguageMap(getLanguageMap(element.getId(), WhereClauseConstants.META_ELEMENT_REFERENCE_TYPE));
		element.setValidValues(getValidValues(element));
		elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		Assert.assertEquals(element.getLanguageMap().size(), languageList.size());
		Assert.assertEquals(element.getDefaultValueLanguageMap().size(), languageList.size());
		Assert.assertTrue(CollectionUtils.isNotEmpty(element.getValidValues()));
		
		final Response deleteResponse = metadataWebService.deleteMetadataElement(element.getId());
		assertSuccess(deleteResponse);
		Assert.assertNull(metadataWebService.findElementById(element.getId()));
	}
	
	private MetadataElementPageTemplateXref getXref(final MetadataElementPageTemplate template, final MetadataElement element, final Integer order) {
		final MetadataElementPageTemplateXref xref = new MetadataElementPageTemplateXref(template.getId(), element.getId(), order);
		return xref;
	}
	
	private Map<String, LanguageMapping> getLanguageMap(final String referenceId, final String referenceType) {
		final List<Language> languageList = languageWS.getAll();
		final Map<String, LanguageMapping> languageMap = new HashMap<String, LanguageMapping>();
		for(final Language language : languageList) {
			final LanguageMapping mapping = new LanguageMapping();
			mapping.setLanguageId(language.getLanguageId());
			mapping.setReferenceId(referenceId);
			mapping.setReferenceType(referenceType);
			mapping.setValue(RandomStringUtils.randomAlphanumeric(3));
			languageMap.put(language.getLanguageId(), mapping);
		}
		return languageMap;
	}
	
	private Set<MetadataValidValue> getValidValues(final MetadataElement element) {
		final Set<MetadataValidValue> retVal = new HashSet<MetadataValidValue>();
		for(int i = 0; i < 6; i++) {
			final MetadataValidValue value = new MetadataValidValue();
			value.setLanguageMap(getLanguageMap(null, WhereClauseConstants.VALID_VALUES_REFERENCE_TYPE));
			value.setMetadataEntityId(element.getId());
			value.setUiValue(RandomStringUtils.randomAlphanumeric(3));
			retVal.add(value);
		}
		return retVal;
	}
	
	private void assertSuccess(final Response response) {
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getStatus());
		Assert.assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
	}
}
