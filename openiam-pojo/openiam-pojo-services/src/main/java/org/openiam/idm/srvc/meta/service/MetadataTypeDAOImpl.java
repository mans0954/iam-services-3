package org.openiam.idm.srvc.meta.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.OrderDaoImpl;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.searchbean.converter.MetadataTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends OrderDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {

    @Autowired
    private MetadataTypeSearchBeanConverter metadataTypeSearchBeanConverter;

    protected boolean cachable() {
        return true;
    }

    @Override
    public MetadataTypeEntity findByNameGrouping(String name, MetadataTypeGrouping grouping) {
        return (MetadataTypeEntity) getCriteria().setCacheable(cachable()).add(Restrictions.eq("description", name)).add(Restrictions.eq("grouping", grouping)).uniqueResult();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if (searchBean != null && searchBean instanceof MetadataTypeSearchBean) {
            final MetadataTypeSearchBean metadataTypeSearchBean = (MetadataTypeSearchBean) searchBean;
            final MetadataTypeEntity entity = metadataTypeSearchBeanConverter.convert(metadataTypeSearchBean);
            criteria = this.getExampleCriteria(entity);
        }
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final MetadataTypeEntity entity) {
        final Criteria criteria = getCriteria();

        if (StringUtils.isNotBlank(entity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
        } else {
            /*
            if (StringUtils.isNotBlank(entity.getDescription())) {
		    	criteria.add(Restrictions.eq("description", entity.getDescription()));
		    }
		    */
            if (entity.getGrouping() != null) {
                criteria.add(Restrictions.eq("grouping", entity.getGrouping()));
            }
            if (entity.getActive() != null) {
                criteria.add(Restrictions.eq("active", entity.getActive()));
            }
            if (StringUtils.isNotEmpty(entity.getDescription())) {
                String name = entity.getDescription();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.END;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                    name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(name)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("description", name, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("description", name));
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(entity.getCategories())) {
                final Set<String> categoryIds = new HashSet<>();
                for (final CategoryEntity category : entity.getCategories()) {
                    categoryIds.add(category.getId());
                }
                criteria.createAlias("categories", "category").add(Restrictions.in("category.id", categoryIds));
            }
        }
        criteria.setCacheable(cachable());
        return criteria;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MetadataTypeEntity> findTypesInCategory(String categoryId) {
        final Criteria criteria = getCriteria().setCacheable(cachable()).createAlias("categories", "category").add(
                Restrictions.eq("category.id", categoryId));
        return criteria.list();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    protected String getReferenceType() {
        return "MetadataTypeEntity.displayNameMap";
    }
}
