package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.base.BaseTemplateRequestModel;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.dto.*;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public interface MetadataElementTemplateService {

	MetadataElementPageTemplate findById(String id);
	List<MetadataElementPageTemplate> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size);
	int count(final MetadataElementPageTemplateSearchBean searchBean);
	String save(final MetadataElementPageTemplate template) throws BasicDataServiceException;
	void delete(final String id);
	PageTemplateAttributeToken getAttributesFromTemplate(final BaseTemplateRequestModel request);
	PageTempate getTemplate(final TemplateRequest request);
	void saveTemplate(final UserProfileRequestModel request) throws PageTemplateException;
	void validate(final UserProfileRequestModel request) throws PageTemplateException;
	public void validate(final BaseTemplateRequestModel request) throws Exception;
	MetadataTemplateType getTemplateType(final String id);
	List<MetadataTemplateType> findTemplateTypes(final MetadataTemplateTypeSearchBean searchBean, final int from, final int size);
	List<MetadataTemplateTypeField> findUIFields(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size);
    Integer countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean);
}
