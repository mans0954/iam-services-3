package org.openiam.idm.srvc.res.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.res.dto.ResourceGroup;
import org.springframework.stereotype.Repository;

@Repository("resourceGroupDAO")
public class ResourceGroupDAOImpl extends BaseDaoImpl<ResourceGroupEntity, String> implements ResourceGroupDAO {

	@Override
	protected Criteria getExampleCriteria(ResourceGroupEntity entity) {
		final Criteria criteria = super.getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getResGroupId())) {
				criteria.add(Restrictions.eq("resGroupId", entity.getResGroupId()));
			} else {
				if(StringUtils.isNotBlank(entity.getGroupId())) {
					criteria.add(Restrictions.eq("groupId", entity.getGroupId()));
				}
				
				if(StringUtils.isNotBlank(entity.getResourceId())) {
					criteria.add(Restrictions.eq("resourceId", entity.getResourceId()));
				}
			}
		}
		
		return criteria;
	}
	
	@Override
	public ResourceGroupEntity getRecord(String resourceId, String groupId) {
		final ResourceGroupEntity entity = new ResourceGroupEntity();
		entity.setGroupId(groupId);
		entity.setResourceId(resourceId);
		final List<ResourceGroupEntity> results = getByExample(entity, 0, 1);;
		return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
	}

	@Override
	protected String getPKfieldName() {
		return "resGroupId";
	}
}
