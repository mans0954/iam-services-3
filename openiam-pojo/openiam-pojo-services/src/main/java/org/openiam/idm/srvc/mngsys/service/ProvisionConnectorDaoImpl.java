package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
				final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
				if(sb.getMetadataType() != null && sb.getMetadataType().getId() != null && !sb.getMetadataType().getId().isEmpty()) {
					criteria.add(Restrictions.eq("metadataType.id", sb.getMetadataType().getId()));
				}
			}
		}
		return criteria;
	}
    
    
}
