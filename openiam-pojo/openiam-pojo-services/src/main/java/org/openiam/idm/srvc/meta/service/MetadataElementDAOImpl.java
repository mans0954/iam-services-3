package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.OrderDaoImpl;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO Implementation for MetadataElement
 */
@Repository("metadataElementDAO")
public class MetadataElementDAOImpl extends OrderDaoImpl<MetadataElementEntity, String> implements MetadataElementDAO {


    @Override
    protected boolean cachable() {
        return true;
    }

    @Override
    public MetadataElementEntity findByAttrNameTypeId(String attrName, String typeId) {
        return (MetadataElementEntity) getCriteria().setCacheable(cachable()).add(Restrictions.eq("attributeName", attrName)).add(Restrictions.eq("metadataType.id", typeId)).uniqueResult();
    }

    @Override
    public String findIdByAttrNameTypeId(String attrName, String typeId) {
        return (String) getCriteria().setCacheable(cachable()).add(Restrictions.eq("attributeName", attrName)).add(Restrictions.eq("metadataType.id", typeId)).setProjection(Projections.id()).uniqueResult();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean, boolean isCount) {
        final Criteria criteria = getCriteria();
        if (searchBean != null && searchBean instanceof MetadataElementSearchBean) {
            final MetadataElementSearchBean metaSearchBean = (MetadataElementSearchBean) searchBean;
            if (CollectionUtils.isNotEmpty(metaSearchBean.getKeys())) {
                criteria.add(Restrictions.in("id", metaSearchBean.getKeys()));
            } else {
                setAttributeNameCriteria(criteria, metaSearchBean.getAttributeName());
                if (CollectionUtils.isNotEmpty(metaSearchBean.getTypeIdSet())) {
                    criteria.add(Restrictions.in("metadataType.id", metaSearchBean.getTypeIdSet()));
                }

                //TODO:  Bug in Hibernate - metadataType.grouping throws org.hibernate.QueryException: could not resolve property
                if (CollectionUtils.isNotEmpty(metaSearchBean.getExcludedGroupings())) {
                    //criteria.createAlias("metadataType", "mt").add(Restrictions.not(Restrictions.in("mt.grouping", metaSearchBean.getExcludedGroupings())));
                    //criteria.add(Restrictions.not(Restrictions.in("metadataType.grouping", metaSearchBean.getExcludedGroupings())));
                }

                if (CollectionUtils.isNotEmpty(metaSearchBean.getGroupings())) {
                    criteria.createAlias("metadataType", "mt")
                            .add(Restrictions.in("mt.grouping", metaSearchBean.getGroupings()));
                }

                if (StringUtils.isNotBlank(metaSearchBean.getTemplateId())) {
                    final Set<String> templateIdSet = new HashSet<String>();
                    templateIdSet.add(metaSearchBean.getTemplateId());
                    setTemplateCriteria(criteria, templateIdSet);
                }
            }
        }
        criteria.setCacheable(cachable());
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final MetadataElementEntity entity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(entity.getId())) {
            criteria.add(Restrictions.eq("id", entity.getId()));
        } else {
            setAttributeNameCriteria(criteria, entity.getAttributeName());
            if (StringUtils.isNotBlank(entity.getDataType())) {
                criteria.add(Restrictions.eq("dataType", entity.getDataType()));
            }

            if (entity.getMetadataType() != null && StringUtils.isNotBlank(entity.getMetadataType().getId())) {
                final String metadataTypeId = entity.getMetadataType().getId();
                criteria.add(Restrictions.eq("metadataType.id", metadataTypeId));
            }

            if (CollectionUtils.isNotEmpty(entity.getTemplateSet())) {
                final Set<String> templateIdSet = new HashSet<String>();
                for (final MetadataElementPageTemplateXrefEntity xref : entity.getTemplateSet()) {
                    if (xref.getTemplate() != null && StringUtils.isNotBlank(xref.getTemplate().getId())) {
                        templateIdSet.add(xref.getTemplate().getId());
                    }
                }

                setTemplateCriteria(criteria, templateIdSet);
            }

            if (entity.getResource() != null && StringUtils.isNotEmpty(entity.getResource().getId())) {
                criteria.add(Restrictions.eq("resource.id", entity.getResource().getId()));
            }
        }
        criteria.setCacheable(this.cachable());
        return criteria;
    }

    private void setTemplateCriteria(final Criteria criteria, final Set<String> templateIdSet) {
        if (CollectionUtils.isNotEmpty(templateIdSet)) {
            criteria.setCacheable(this.cachable()).createAlias("templateSet", "xref")
                    .createAlias("xref.template", "template")
                    .add(Restrictions.in("template.id", templateIdSet));
        }
    }

    private void setAttributeNameCriteria(final Criteria criteria, final String attributeName) {
        criteria.setCacheable(cachable());
        if (StringUtils.isNotBlank(attributeName)) {
            String name = attributeName;
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
                    criteria.add(Restrictions.ilike("attributeName", name, matchMode));
                } else {
                    criteria.add(Restrictions.eq("attributeName", name));
                }
            }
        }
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<MetadataElementEntity> getByResourceId(String resourceId) {
        final MetadataElementEntity entity = new MetadataElementEntity();
        final ResourceEntity resource = new ResourceEntity();
        resource.setId(resourceId);
        entity.setResource(resource);
        return getByExample(entity);
    }

    protected String getReferenceType() {
        return "MetadataElementEntity.languageMap";
    }

}
