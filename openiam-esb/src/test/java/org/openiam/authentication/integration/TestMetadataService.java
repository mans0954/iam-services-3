package org.openiam.authentication.integration;

import groovy.xml.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.WhereClauseConstants;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXrefID;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
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
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	private ContentProviderWebService contentProviderWS;
	
	 @BeforeClass
	 protected void setUp() throws Exception {
		 
	 }
	 

	 @Test
	 public void testLanguages() {
		 Assert.assertTrue(CollectionUtils.isNotEmpty(languageWS.getAll()));
	 }
	
	@Test
	public void testCreateAndDeleteEmptyTemplate() {
		final MetadataTemplateType templateType = getFirstPageTemplateType();
		
		final ContentProviderSearchBean cpSearchBean = new ContentProviderSearchBean();
		cpSearchBean.setDeepCopy(true);
		final List<ContentProvider> cpList = contentProviderWS.findBeans(cpSearchBean, 0, 1);
		final URIPattern pattern = (CollectionUtils.isNotEmpty(cpList) && CollectionUtils.isNotEmpty(cpList.get(0).getPatternSet())) ? 
				cpList.get(0).getPatternSet().iterator().next() : null;
		
		/* create */
		MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(System.currentTimeMillis() + "");
		template.addPattern(pattern);
		template.setMetadataTemplateTypeId(templateType.getId());
		final Response saveResponse = templateWebService.save(template);
		assertSuccess(saveResponse);
		Assert.assertNotNull(saveResponse.getResponseValue());
		
		template = templateWebService.findById((String)saveResponse.getResponseValue());
		Assert.assertNotNull(template);
		if(pattern != null) {
			Assert.assertTrue(CollectionUtils.isNotEmpty(template.getUriPatterns()));
		}
		Assert.assertEquals(templateType.getId(), template.getMetadataTemplateTypeId());
		
		/* delete */
		final Response deleteResponse = templateWebService.delete((String)saveResponse.getResponseValue());
		assertSuccess(deleteResponse);
		
		Assert.assertNull(templateWebService.findById((String)saveResponse.getResponseValue()));
		if(pattern != null) {
			Assert.assertNotNull(contentProviderWS.getURIPattern(pattern.getId()));
		}
	}
	
	@Test
	public void testCreateAndDeleteEmptyElement() {
		
		final MetadataType type = getAllMetatypes().get(0);
		
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
		final MetadataTemplateType templateType = getFirstPageTemplateType();
		
		/* create */
		MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(System.currentTimeMillis() + "");
		template.setMetadataTemplateTypeId(templateType.getId());
		final Response saveResponse = templateWebService.save(template);
		assertSuccess(saveResponse);
		Assert.assertNotNull(saveResponse.getResponseValue());
		template = templateWebService.findById((String)saveResponse.getResponseValue());
		Assert.assertNotNull(template.getResourceId());
		Assert.assertEquals(template.getMetadataTemplateTypeId(), templateType.getId());
		
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
		final MetadataTemplateType templateType = getFirstPageTemplateType();
		
		final MetadataType type = getAllMetatypes().get(0);
		
		/* create */
		MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(System.currentTimeMillis() + "");
		template.setMetadataTemplateTypeId(templateType.getId());
		Response templateSaveResponse = templateWebService.save(template);
		assertSuccess(templateSaveResponse);
		Assert.assertNotNull(templateSaveResponse.getResponseValue());
		
		template = templateWebService.findById((String)templateSaveResponse.getResponseValue());
		Assert.assertNotNull(template.getResourceId());
		
		/* create */
		MetadataElement element = new MetadataElement();
		element.setAttributeName(System.currentTimeMillis() + "");
		element.setMetadataTypeId(type.getMetadataTypeId());
		Response elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		Assert.assertNotNull(elementSaveResponse.getResponseValue());
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		Assert.assertNotNull(element.getResourceId());
		
		/* add xref */
		MetadataElementPageTemplateXref xref = getXref(template, element, 2);
		template.addMetdataElement(xref);
		
		for(final MetadataTemplateTypeField field : templateType.getFields()) {
			final MetadataFieldTemplateXref fieldXref = new MetadataFieldTemplateXref();
			fieldXref.setField(field);
			fieldXref.setRequired(true);
			//fieldXref.setTemplate(template);
			fieldXref.setId(new MetadataFieldTemplateXrefID(field.getId(), template.getId()));
			template.addFieldXref(fieldXref);
		}
		
		templateSaveResponse = templateWebService.save(template);
		assertSuccess(templateSaveResponse);
		
		template = templateWebService.findById(template.getId());
		Assert.assertTrue(CollectionUtils.isNotEmpty(template.getMetadataElements()));
		Assert.assertEquals(templateType.getFields().size(), template.getFieldXrefs().size());
		
		template.removeMetdataElement(xref);
		Assert.assertTrue(CollectionUtils.isEmpty(template.getMetadataElements()));
		template.setFieldXrefs(null);
		
		templateSaveResponse = templateWebService.save(template);
		assertSuccess(templateSaveResponse);
		
		template = templateWebService.findById(template.getId());
		Assert.assertTrue(CollectionUtils.isEmpty(template.getMetadataElements()));
		Assert.assertTrue(CollectionUtils.isEmpty(template.getFieldXrefs()));
		
		/* delete */
		final Response deleteResponse = templateWebService.delete(template.getId());
		assertSuccess(deleteResponse);
		Assert.assertNull(templateWebService.findById(template.getId()));
	}
	
	@Test
	public void testMetdataEntityCollectionsBulk() {
		final List<Language> languageList = languageWS.getAll();
		final MetadataType type = getAllMetatypes().get(0);
		
		MetadataElement element = new MetadataElement();
		element.setAttributeName(System.currentTimeMillis() + "");
		element.setMetadataTypeId(type.getMetadataTypeId());
		
		element.setDefaultValueLanguageMap(getLanguageMap(element.getId(), WhereClauseConstants.META_ELEMENT_DEFAULT_VALUE_REFERENCE_TYPE));
		element.setLanguageMap(getLanguageMap(element.getId(), WhereClauseConstants.META_ELEMENT_REFERENCE_TYPE));
		element.setValidValues(getValidValues(element));
		Response elementSaveResponse  = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		Assert.assertNotNull(element.getResourceId());
		Assert.assertEquals(element.getLanguageMap().size(), languageList.size());
		Assert.assertEquals(element.getDefaultValueLanguageMap().size(), languageList.size());
		Assert.assertEquals(element.getValidValues().size(), 6);
		
		/* remove one mapping from each collection, confirm that it was delted from DB */
		final Iterator<Entry<String, LanguageMapping>> defaultIterator = element.getDefaultValueLanguageMap().entrySet().iterator();
		defaultIterator.next();
		defaultIterator.remove();
		
		final Iterator<Entry<String, LanguageMapping>> languageIterator = element.getLanguageMap().entrySet().iterator();
		languageIterator.next();
		languageIterator.remove();
		
		final Iterator<MetadataValidValue> validValueIterator = element.getValidValues().iterator();
		validValueIterator.next();
		validValueIterator.remove();
		
		elementSaveResponse  = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		Assert.assertEquals(element.getLanguageMap().size(), languageList.size() - 1);
		Assert.assertEquals(element.getDefaultValueLanguageMap().size(), languageList.size() - 1);
		Assert.assertEquals(element.getValidValues().size(), 5);
		
		final Response deleteResponse = metadataWebService.deleteMetadataElement(element.getId());
		assertSuccess(deleteResponse);
		Assert.assertNull(metadataWebService.findElementById(element.getId()));
	}
	
	@Test
	public void testMetdataEntityCollectionsIncremental() {
		final List<Language> languageList = languageWS.getAll();
		final MetadataType type = getAllMetatypes().get(0);
		
		MetadataElement element = new MetadataElement();
		element.setAttributeName(System.currentTimeMillis() + "");
		element.setMetadataTypeId(type.getMetadataTypeId());
		
		Response elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		Assert.assertNotNull(elementSaveResponse.getResponseValue());
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		
		Assert.assertNotNull(element.getResourceId());
		element.setDefaultValueLanguageMap(getLanguageMap(element.getId(), WhereClauseConstants.META_ELEMENT_DEFAULT_VALUE_REFERENCE_TYPE));
		element.setLanguageMap(getLanguageMap(element.getId(), WhereClauseConstants.META_ELEMENT_REFERENCE_TYPE));
		element.setValidValues(getValidValues(element));
		elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		Assert.assertEquals(element.getLanguageMap().size(), languageList.size());
		Assert.assertEquals(element.getDefaultValueLanguageMap().size(), languageList.size());
		Assert.assertEquals(element.getValidValues().size(), 6);
		
		/* remove all collections - confirm removed */
		element.setDefaultValueLanguageMap(new HashMap<String, LanguageMapping>());
		element.setLanguageMap(new HashMap<String, LanguageMapping>());
		elementSaveResponse = metadataWebService.saveMetadataEntity(element);
		assertSuccess(elementSaveResponse);
		element = metadataWebService.findElementById((String)elementSaveResponse.getResponseValue());
		Assert.assertTrue(MapUtils.isEmpty(element.getLanguageMap()));
		Assert.assertTrue(MapUtils.isEmpty(element.getDefaultValueLanguageMap()));
		
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
			value.setDisplayOrder(i);
			retVal.add(value);
		}
		return retVal;
	}
	
	private void assertSuccess(final Response response) {
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getStatus());
		Assert.assertEquals(response.getStatus(), ResponseStatus.SUCCESS);
	}
	
	private List<MetadataType> getAllMetatypes() {
		final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
		searchBean.setActive(true);
		searchBean.setSyncManagedSys(true);
		return metadataWebService.findTypeBeans(searchBean, 0, Integer.MAX_VALUE);
	}
	
	private MetadataTemplateType getFirstPageTemplateType() {
		final MetadataTemplateTypeSearchBean searchBean = new MetadataTemplateTypeSearchBean();
		searchBean.setDeepCopy(true);
		return templateWebService.findTemplateTypes(searchBean, 0, Integer.MAX_VALUE).get(0);
	}
}
