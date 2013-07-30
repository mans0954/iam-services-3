package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataTemplateTypeFieldEntityDAOImpl extends BaseDaoImpl<MetadataTemplateTypeFieldEntity, String> implements MetadataTemplateTypeFieldEntityDAO {

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
