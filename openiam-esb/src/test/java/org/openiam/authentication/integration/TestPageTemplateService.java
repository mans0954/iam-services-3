package org.openiam.authentication.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.RandomStringUtils;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.authmanager.service.AuthorizationManagerMenuWebService;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.dto.MetadataValidValue;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValidValue;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.meta.ws.MetadataElementTemplateWebService;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class TestPageTemplateService extends AbstractTestNGSpringContextTests {


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
	
	@Autowired
	@Qualifier("authorizationManagerServiceClient")
	private AuthorizationManagerWebService authManagerWS;
	
	@Resource(name="resourceServiceClient")
	private ResourceDataService resourceDataService;
	
    @Resource(name = "userServiceClient")
    protected UserDataWebService userDataWebService;
	
	private ContentProvider cp;
	private URIPattern pattern;
	private MetadataElementPageTemplate template;
	private List<MetadataElement> elementList = new LinkedList<MetadataElement>();
	private Map<String, List<UserAttribute>> attributeMap = new HashMap<String, List<UserAttribute>>();
	
	private static final String userId = "3000";
	private static final String locale = "en_EN";
	
	 @BeforeClass
	 protected void setUp() throws Exception {
		 final List<Language> languageList = languageWS.getAll();
		 
		 final AuthLevel firstAuthLevel = contentProviderWS.getAuthLevelList().get(0);
		 
		 final ContentProvider contentProvider = new ContentProvider();
		 contentProvider.setDomainPattern(RandomStringUtils.randomAlphanumeric(4));
		 contentProvider.setAuthLevel(firstAuthLevel);
		 contentProvider.setIsPublic(true);
		 contentProvider.setIsSSL(false);
		 contentProvider.setName(RandomStringUtils.randomAlphanumeric(4));
		 Response saveResponse = contentProviderWS.saveContentProvider(contentProvider);
		 assertSuccess(saveResponse);
		 cp = contentProviderWS.getContentProvider(((ContentProvider)saveResponse.getResponseValue()).getId());
		 Assert.assertNotNull(cp);
		 
		 final URIPattern uriPattern = new URIPattern();
		 uriPattern.setAuthLevel(firstAuthLevel);
		 uriPattern.setContentProviderId(cp.getId());
		 uriPattern.setIsPublic(true);
		 uriPattern.setPattern("/*");
		 saveResponse = contentProviderWS.saveURIPattern(uriPattern);
		 assertSuccess(saveResponse);
		 pattern = contentProviderWS.getURIPattern(((URIPattern)saveResponse.getResponseValue()).getId());
		 Assert.assertNotNull(pattern);
		 
		 final MetadataElementPageTemplate pageTemplate = new MetadataElementPageTemplate();
		 pageTemplate.setName(RandomStringUtils.randomAlphabetic(4));
		 pageTemplate.addPattern(pattern);
		 saveResponse = templateWebService.save(pageTemplate);
		 assertSuccess(saveResponse);
		 template = templateWebService.findById((String)saveResponse.getResponseValue());
		 assertSuccess(saveResponse);
		 Assert.assertNotNull(template);
		 
		 //resourceDataService.addUserToResource(template.getResourceId(), userId);
		 
		 final List<MetadataType> typeList = getAllMetatypes();
		 if(CollectionUtils.isNotEmpty(typeList)) {
			 int idx = 0;
			 for(final MetadataType type : typeList) {
				 final MetadataElement element = new MetadataElement();
				 element.setAttributeName(RandomStringUtils.randomAlphanumeric(4));
				 element.setMetadataTypeId(type.getMetadataTypeId());
				 element.setSelfEditable(true);
				 element.setStaticDefaultValue(RandomStringUtils.randomAlphabetic(4));
				 element.setDefaultValueLanguageMap(getLanguageMap(languageList));
				 element.setLanguageMap(getLanguageMap(languageList));
				 
				 //final Set<MetadataElementPageTemplateXref> templateSet = new HashSet<MetadataElementPageTemplateXref>();
				 //templateSet.add(new MetadataElementPageTemplateXref(template.getId(), null, idx));
				 //element.setTemplateSet(templateSet);
				 for(int i = 0; i < 6; i++) {
					 final MetadataValidValue value = new MetadataValidValue();
					 value.setLanguageMap(getLanguageMap(languageList));
					 value.setUiValue(RandomStringUtils.randomAlphanumeric(4));
					 element.addValidValue(value);
				 }
				 saveResponse = metadataWebService.saveMetadataEntity(element);
				 assertSuccess(saveResponse);
				 
				 /* add as xref */
				 final String elementId = (String)saveResponse.getResponseValue();
				 template.addMetdataElement(new MetadataElementPageTemplateXref(template.getId(), elementId, idx));
				 templateWebService.save(template);
				 assertSuccess(saveResponse);
				 
				 final MetadataElement savedElement = metadataWebService.findElementById((String)saveResponse.getResponseValue());
				 elementList.add(savedElement);
				 
				 for(int i = 0; i < 4; i++) {
					 final UserAttribute attribute = new UserAttribute();
					 attribute.setMetadataElementId(savedElement.getId());
					 attribute.setName(RandomStringUtils.randomAlphanumeric(3));
					 attribute.setUserId(userId);
					 attribute.setValue(RandomStringUtils.randomAlphanumeric(3));
					 saveResponse = userDataWebService.addAttribute(attribute);
					 assertSuccess(saveResponse);
					 final String attributeId = (String)saveResponse.getResponseValue();
					 
					 final UserAttribute savedAttribute = userDataWebService.getAttribute(attributeId);
					 if(!attributeMap.containsKey(savedElement.getId())) {
						 attributeMap.put(savedElement.getId(), new LinkedList<UserAttribute>());
					 }
					 attributeMap.get(savedElement.getId()).add(savedAttribute);
				 }
				 
				 //resourceDataService.addUserToResource(savedElement.getResourceId(), userId);
				 idx++;
			 }
		 }
		 //authManagerWS.refreshCache();
	 }
	 
	 @AfterClass
	 protected void tearDown() throws Exception {
		 Response deleteResponse = null;
		 deleteResponse = templateWebService.delete(template.getId());
		 assertSuccess(deleteResponse);
		 deleteResponse = contentProviderWS.deleteProviderPattern(pattern.getId());
		 assertSuccess(deleteResponse);
		 deleteResponse = contentProviderWS.deleteContentProvider(cp.getId());
		 assertSuccess(deleteResponse);
		 Assert.assertNull(contentProviderWS.getContentProvider(cp.getId()));
		 Assert.assertNull(contentProviderWS.getURIPattern(pattern.getId()));
		 Assert.assertNull(templateWebService.findById(template.getId()));
		 for(final MetadataElement element : elementList) {
			 assertSuccess(metadataWebService.deleteMetadataElement(element.getId()));
			 Assert.assertNull(metadataWebService.findElementById(element.getId()));
			 resourceDataService.deleteResource(element.getResourceId());
		 }
		 
		 resourceDataService.deleteResource(template.getResourceId());
	 }
	 
	@Test
	public void testEquality() {
		final TemplateRequest request = new TemplateRequest();
		request.setUserId(userId);
		request.setLocaleName(locale);
		request.setSelfserviceRequest(true);
		request.setPatternId(pattern.getId());
		final PageTempate tempalteByPattern = templateWebService.getTemplate(request);
		Assert.assertNotNull(tempalteByPattern);
		
		request.setPatternId(null);
		request.setTemplateId(template.getId());
		final PageTempate templateByTemplateId = templateWebService.getTemplate(request);
		Assert.assertNotNull(templateByTemplateId);
		
		Assert.assertEquals(tempalteByPattern.getTemplateId(), templateByTemplateId.getTemplateId());
	}
	
	@Test
	public void testTextCorrectness() {
		final String languageId = getLanguageByLocale(locale).getLanguageId();
		final TemplateRequest request = new TemplateRequest();
		request.setUserId(userId);
		request.setLocaleName(locale);
		request.setSelfserviceRequest(true);
		request.setPatternId(pattern.getId());
		final PageTempate template = templateWebService.getTemplate(request);
		Assert.assertNotNull(template);
		if(CollectionUtils.isNotEmpty(template.getElements())) {
			for(final PageElement element : template.getElements()) {
				final MetadataElement metaElement = getElement(element.getElementId());
				
				final String displayName = element.getDisplayName();
				final String defaultValue = element.getDefaultValue();
				
				Assert.assertEquals(displayName, metaElement.getLanguageMap().get(languageId).getValue());
				Assert.assertEquals(defaultValue, metaElement.getDefaultValueLanguageMap().get(languageId).getValue());
				
				Assert.assertTrue(CollectionUtils.isNotEmpty(element.getValidValues()));
				for(final PageElementValidValue validValue : element.getValidValues()) {
					final MetadataValidValue validValueObj = getValidValue(metaElement, validValue.getId());
					
					final String validValueDisplayName = validValue.getDisplayName();
					final String value = validValue.getValue();
					
					Assert.assertEquals(validValueDisplayName, validValueObj.getLanguageMap().get(languageId).getValue());
					Assert.assertEquals(value, validValueObj.getUiValue());
				}
				
				Assert.assertTrue(CollectionUtils.isNotEmpty(element.getUserValues()));
				for(final PageElementValue value : element.getUserValues()) {
					final UserAttribute attribute = getUserAttribute(metaElement.getId(), value.getUserAttributeId());
					Assert.assertEquals(attribute.getValue(), value.getValue());
				}
			}
		}
	}
	
	private UserAttribute getUserAttribute(final String elementId, final String attributeId) {
		UserAttribute retVal = null;
		if(attributeMap.containsKey(elementId)) {
			for(final UserAttribute attribute : attributeMap.get(elementId)) {
				if(attribute.getId().equals(attributeId)) {
					retVal = attribute;
					break;
				}
			}
		}
		return retVal;
	}
	
	private MetadataValidValue getValidValue(final MetadataElement element, final String validValueId) {
		MetadataValidValue retVal = null;
		if(element != null && element.getValidValues() != null) {
			for(final MetadataValidValue vv : element.getValidValues()) {
				if(vv.getId().equals(validValueId)) {
					retVal = vv;
					break;
				}
			}
		}
		return retVal;
	}
	
	private Language getLanguageByLocale(final String locale) {
		final List<Language> languageList = languageWS.getAll();
		Language language = null;
		for(final Language l : languageList) {
			if(l.getLocale().equals(locale)) {
				language = l;
				break;
			}
		}
		return language;
	}
	
	private MetadataElement getElement(final String elementId) {
		MetadataElement element = null;
		for(final MetadataElement e : elementList) {
			if(e.getId().equals(elementId)) {
				element = e;
				break;
			}
		}
		return element;
	}
	
	private Map<String, LanguageMapping> getLanguageMap(final List<Language> languageList) {
		final Map<String, LanguageMapping> map = new HashMap<String, LanguageMapping>();
		for(final Language language : languageList) {
			 final LanguageMapping mapping = new LanguageMapping();
			 mapping.setLanguageId(language.getLanguageId());
			 mapping.setValue(RandomStringUtils.randomAlphabetic(5));
			 map.put(language.getLanguageId(), mapping);
		 }
		return map;
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
		searchBean.setSyncManagedSys(false);
		searchBean.setGrouping("UI_WIDGET");
		return metadataWebService.findTypeBeans(searchBean, 0, Integer.MAX_VALUE);
	}
}
