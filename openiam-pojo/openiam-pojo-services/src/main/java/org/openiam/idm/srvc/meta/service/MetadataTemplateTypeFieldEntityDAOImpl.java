package org.openiam.idm.srvc.meta.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataTemplateTypeFieldEntityDAOImpl extends BaseDaoImpl<MetadataTemplateTypeFieldEntity, String> implements MetadataTemplateTypeFieldEntityDAO {

	
	
	 @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof MetadataTemplateTypeFieldSearchBean) {
			final MetadataTemplateTypeFieldSearchBean bean = (MetadataTemplateTypeFieldSearchBean)searchBean;
			if(StringUtils.isNotBlank(bean.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), bean.getKey()));
			} else {
				if(StringUtils.isNotBlank(bean.getName())) {
					criteria.add(Restrictions.eq("name", bean.getName()));
				}
				
				if(StringUtils.isNotBlank(bean.getTemplateTypeId())) {
					criteria.add(Restrictions.eq("templateType.id", bean.getTemplateTypeId()));
				}
				
				if(StringUtils.isNotBlank(bean.getTemplateId())) {
					criteria.createAlias("fieldXrefs", "xref").add(Restrictions.eq("xref.id.templateId", bean.getTemplateId()));
				}
			}
		}
		return criteria;
	}

	@Override
	 protected Criteria getExampleCriteria(final MetadataTemplateTypeFieldEntity entity) {
		 final Criteria criteria = getCriteria();
		 if(entity != null) {
			 if(StringUtils.isNotBlank(entity.getId())) {
				 criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
			 } else {
				 if(entity.getTemplateType() != null && StringUtils.isNotBlank(entity.getTemplateType().getId())) {
					 criteria.add(Restrictions.eq("templateType.id", entity.getTemplateType().getId()));
				 }
				 
				 if(StringUtils.isNotBlank(entity.getName())) {
					 criteria.add(Restrictions.eq("name", entity.getName()));
				 }
			 }
		 }
		 return criteria;
	 }
	
	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
