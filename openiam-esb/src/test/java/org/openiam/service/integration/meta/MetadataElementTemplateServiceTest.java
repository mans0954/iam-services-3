package org.openiam.service.integration.meta;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.dto.pk.MetadataElementPageTemplateXrefId;
import org.openiam.idm.srvc.meta.ws.MetadataElementTemplateWebService;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MetadataElementTemplateServiceTest extends AbstractKeyNameServiceTest<MetadataElementPageTemplate, MetadataElementPageTemplateSearchBean> {
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	private ContentProviderWebService contentProviderServiceClient;
	
	@Autowired
	@Qualifier("metadataTemplateServiceClient")
	private MetadataElementTemplateWebService metadataTemplateServiceClient;

	private MetadataTemplateType getFirstType() {
		return metadataTemplateServiceClient.findTemplateTypes(new MetadataTemplateTypeSearchBean(), 0, 1).get(0);
	}
	
	private List<MetadataTemplateTypeField> getAllUIFields(final int from, final int size) {
		return metadataTemplateServiceClient.findUIFIelds(new MetadataTemplateTypeFieldSearchBean(), from, size);
	}
	
	private List<MetadataElement> getAllMetadataElement(final int from, final int size) {
		return metadataServiceClient.findElementBeans(null, from, size, getDefaultLanguage());
	}
	
	private List<URIPattern> getAllURIPatterns(final int from, final int size) {
		return contentProviderServiceClient.findUriPatterns(new URIPatternSearchBean(), from, size);
	}
	
	@Override
	protected MetadataElementPageTemplate newInstance() {
		final MetadataElementPageTemplate template = new MetadataElementPageTemplate();
		template.setName(getRandomName());
		template.setMetadataTemplateTypeId(getFirstType().getId());
		return template;
	}

	@Override
	protected MetadataElementPageTemplateSearchBean newSearchBean() {
		final MetadataElementPageTemplateSearchBean searchBean = new MetadataElementPageTemplateSearchBean();
		
		return searchBean;
	}

	@Override
	protected Response save(MetadataElementPageTemplate t) {
		return metadataTemplateServiceClient.save(t);
	}

	@Override
	protected Response delete(MetadataElementPageTemplate t) {
		return metadataTemplateServiceClient.delete(t.getId());
	}

	@Override
	protected MetadataElementPageTemplate get(String key) {
		final MetadataElementPageTemplateSearchBean searchBean = newSearchBean();
		searchBean.setDeepCopy(true);
		searchBean.setKey(key);
		return find(searchBean, 0, 1).get(0);
	}

	@Override
	public List<MetadataElementPageTemplate> find(final MetadataElementPageTemplateSearchBean searchBean, int from, int size) {
		return metadataTemplateServiceClient.findBeans(searchBean, from, size);
	}

/*	@Override
	protected String getId(MetadataElementPageTemplate bean) {
		return bean.getId();
	}

	@Override
	protected void setId(MetadataElementPageTemplate bean, String id) {
		bean.setId(id);
	}

	@Override
	protected void setName(MetadataElementPageTemplate bean, String name) {
		bean.setName(name);
	}

	@Override
	protected String getName(MetadataElementPageTemplate bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(MetadataElementPageTemplateSearchBean searchBean, String name) {
		searchBean.setName(name);
	}*/
	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<MetadataElementPageTemplate, MetadataElementPageTemplateSearchBean> key = doClusterTest();
		final MetadataElementPageTemplate instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}
	
	@Test
	public void fullSaveUpdateTest() throws Exception {
		MetadataElementPageTemplate test = newInstance();
		Response wsResponse = null;
		try {
			test.setIsPublic(true);
			wsResponse = saveAndAssert(test);
			Thread.sleep(2000L);
			test = get((String)wsResponse.getResponseValue());
			
/*
 second simple save
*/

			wsResponse = saveAndAssert(test);
			Thread.sleep(2000L);
			test = get((String)wsResponse.getResponseValue());
			
			//addURIPatterns(test, 0, 3);
			addUIFields(test, 0, 3);
			addMetadataElements(test, 0, 3);
			test = assertFullClusteredSave(test);
			
			//addURIPatterns(test, 3, 3);
			addUIFields(test, 3, 3);
			addMetadataElements(test, 3, 3);
			test = assertFullClusteredSave(test);
			
/*
 remove 2
*/

			test = assertFullClusteredSave(test);
			
			//test.setUriPatterns(null);
			test.setFieldXrefs(null);
			test.setMetadataElements(null);
			//addURIPatterns(test, 6, 3);
			addUIFields(test, 6, 3);
			addMetadataElements(test, 6, 3);
			test = assertFullClusteredSave(test);
						
			//addURIPatterns(test, 0, 3);
			//addUIFields(test, 0, 3);
			//addMetadataElements(test, 0, 3);
			//test = assertFullClusteredSave(test);
			
			//test.setUriPatterns(null);
			test.setFieldXrefs(null);
			test.setMetadataElements(null);
			test = assertFullClusteredSave(test);
			
		} finally {
			if(test.getId() != null) {
				deleteAndAssert(test);
			}
		}
	}
	
	private MetadataElementPageTemplate assertFullClusteredSave(final MetadataElementPageTemplate test) {
		saveAndAssert(test);
		final MetadataElementPageTemplate testCluster1 = get(test.getId());
		final MetadataElementPageTemplate testCluster2 = get(test.getId());
		Assert.assertEquals(testCluster1, test);
		Assert.assertEquals(testCluster2, test);
		if(CollectionUtils.isEmpty(test.getFieldXrefs())) {
			Assert.assertTrue(CollectionUtils.isEmpty(testCluster1.getFieldXrefs()));
			Assert.assertTrue(CollectionUtils.isEmpty(testCluster2.getFieldXrefs()));
		} else {
			Assert.assertEquals(CollectionUtils.size(testCluster1.getFieldXrefs()), CollectionUtils.size(test.getFieldXrefs()));
			Assert.assertEquals(CollectionUtils.size(testCluster2.getFieldXrefs()), CollectionUtils.size(test.getFieldXrefs()));
		}
		if(CollectionUtils.isEmpty(test.getMetadataElements())) {
			Assert.assertTrue(CollectionUtils.isEmpty(testCluster1.getMetadataElements()));
			Assert.assertTrue(CollectionUtils.isEmpty(testCluster2.getMetadataElements()));
		} else {
			Assert.assertEquals(CollectionUtils.size(testCluster1.getMetadataElements()), CollectionUtils.size(test.getMetadataElements()));
			Assert.assertEquals(CollectionUtils.size(testCluster2.getMetadataElements()), CollectionUtils.size(test.getMetadataElements()));
		}
		if(CollectionUtils.isEmpty(test.getUriPatterns())) {
			Assert.assertTrue(CollectionUtils.isEmpty(testCluster1.getUriPatterns()));
			Assert.assertTrue(CollectionUtils.isEmpty(testCluster2.getUriPatterns()));
		} else {
			Assert.assertEquals(CollectionUtils.size(testCluster1.getUriPatterns()), CollectionUtils.size(test.getUriPatterns()));
			Assert.assertEquals(CollectionUtils.size(testCluster2.getUriPatterns()), CollectionUtils.size(test.getUriPatterns()));
		}
		return testCluster1;
	}
	
	private void addURIPatterns(final MetadataElementPageTemplate test, final int from, final int size) {
		
	}
	
	private void addMetadataElements(final MetadataElementPageTemplate test, final int from, final int size) {
		final List<MetadataElement> elements = getAllMetadataElement(from, size);
		for(int i = 0; i < elements.size(); i++) {
			final MetadataElementPageTemplateXref xref = new MetadataElementPageTemplateXref();
			xref.setDisplayOrder(i);
			xref.setId(new MetadataElementPageTemplateXrefId(test.getId(), elements.get(i).getId()));
			test.addMetdataElement(xref);
		}
	}
	
	private void addUIFields(final MetadataElementPageTemplate test, final int from, final int size) {
		List<MetadataTemplateTypeField> fieldList = getAllUIFields(from, size);
		for(int i = 0; i < fieldList.size(); i++) {
			final MetadataFieldTemplateXref xref = new MetadataFieldTemplateXref();
			xref.setDisplayOrder(i);
			xref.setEditable(true);
			xref.setField(fieldList.get(i));
			xref.setLanguageMap(generateRandomLanguageMapping());
			final MetadataElementPageTemplate copy  = new MetadataElementPageTemplate();
			copy.setId(test.getId());
			xref.setTemplate(copy);
			xref.setRequired(true);
			test.addFieldXref(xref);
		}
	}
}
