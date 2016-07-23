package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.base.BaseRequestModel;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public interface MetadataElementTemplateService {

	MetadataElementPageTemplateEntity findById(String id);
	List<MetadataElementPageTemplateEntity> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size);
	int count(final MetadataElementPageTemplateSearchBean searchBean);
	void save(final MetadataElementPageTemplateEntity template);
	void delete(final String id);
	PageTemplateAttributeToken getAttributesFromTemplate(final BaseRequestModel request);
	PageTempate getTemplate(final TemplateRequest request);
	void saveTemplate(final UserProfileRequestModel request) throws PageTemplateException;
	void validate(final UserProfileRequestModel request) throws PageTemplateException;
	public void validate(final BaseRequestModel request) throws Exception;
	MetadataTemplateTypeEntity getTemplateType(final String id);
	List<MetadataTemplateTypeEntity> findTemplateTypes(final MetadataTemplateTypeSearchBean searchBean, final int from, final int size);
	List<MetadataTemplateTypeFieldEntity> findUIFields(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size);
    Integer countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean);
}
