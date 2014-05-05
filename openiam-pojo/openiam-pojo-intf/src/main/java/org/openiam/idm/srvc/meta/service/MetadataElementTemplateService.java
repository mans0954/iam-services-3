package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
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

	public List<MetadataElementPageTemplateEntity> findBeans(final MetadataElementPageTemplateSearchBean searchBean, final int from, final int size);
	public int count(final MetadataElementPageTemplateSearchBean searchBean);
	public void save(final MetadataElementPageTemplateEntity template);
	public void delete(final String id);
	public PageTemplateAttributeToken getAttributesFromTemplate(final UserProfileRequestModel request);
	public PageTempate getTemplate(final TemplateRequest request);
	public void saveTemplate(final UserProfileRequestModel request) throws PageTemplateException;
	public void validate(final UserProfileRequestModel request) throws PageTemplateException;
	public MetadataTemplateTypeEntity getTemplateType(final String id);
	public List<MetadataTemplateTypeEntity> findTemplateTypes(final MetadataTemplateTypeEntity entity, final int from, final int size);
	public List<MetadataTemplateTypeFieldEntity> findUIFields(final MetadataTemplateTypeFieldSearchBean searchBean, final int from, final int size);
    public Integer countUIFields(final MetadataTemplateTypeFieldSearchBean searchBean);
}
