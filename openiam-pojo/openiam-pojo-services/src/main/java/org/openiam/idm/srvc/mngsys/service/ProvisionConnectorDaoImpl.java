package org.openiam.idm.srvc.mngsys.service;

import org.elasticsearch.common.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProvisionConnectorDaoImpl extends BaseDaoImpl<ProvisionConnectorEntity, String> implements ProvisionConnectorDao {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<MetadataTypeEntity> getMetadataTypes() {
        DetachedCriteria ownerCriteria = DetachedCriteria.forClass(ProvisionConnectorEntity.class);
        ownerCriteria.setProjection(Property.forName("metadataTypeId"));
        Criteria criteria = getSession().createCriteria(MetadataTypeEntity.class);
        criteria.add(Property.forName("id").in(ownerCriteria));
        return criteria.list();
    }

	@Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof ProvisionConnectorSearchBean) {
			final ProvisionConnectorSearchBean sb = (ProvisionConnectorSearchBean)searchBean;
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			} else {
				final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
				if(StringUtils.isNotBlank(sb.getTypeId())) {
					criteria.add(Restrictions.eq("metadataTypeId", sb.getTypeId()));
				}
			}
		}
		return criteria;
	}
    
    
}
