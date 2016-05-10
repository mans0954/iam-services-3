package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.Org2OrgXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Data access object implementation for OrganizationEntity.
 */
@Repository("organizationDAO")
public class OrganizationDAOImpl extends
        BaseDaoImpl<OrganizationEntity, String> implements OrganizationDAO {

    @Autowired
    private OrganizationSearchBeanConverter organizationSearchBeanConverter;

    @Override
    @LocalizedDatabaseGet
    public int getNumOfOrganizationsForUser(final String userId,
                                            final Set<String> filter) {
        final Criteria criteria = getOrganizationsForUserCriteria(userId,
                filter).setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> getOrganizationsForUser(
            final String userId, final Set<String> filter, final int from,
            final int size) {
        final Criteria criteria = getOrganizationsForUserCriteria(userId,
                filter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> getUserAffiliationsByType(
            final String userId, final String typeId, final Set<String> filter, final int from,
            final int size) {
        final Criteria criteria = getUserAffiliationsByTypeCriteria(userId, typeId,
                filter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    private Criteria getOrganizationsForUserCriteria(final String userId,
                                                     final Set<String> filter) {

        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(userId)) {
            criteria.createAlias("organizationUser", "ou").
                    add(Restrictions.eq("ou.primaryKey.user.id", userId));
        }

        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private Criteria getUserAffiliationsByTypeCriteria(final String userId, final String typeId,
                                                       final Set<String> filter) {

        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(typeId)) {
            criteria.createAlias("organizationUser", "ou", Criteria.LEFT_JOIN).
                    add(Restrictions.and(Restrictions.eq("ou.primaryKey.user.id", userId), Restrictions.eq("ou.metadataTypeEntity.id", typeId)));
        }

        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }
    private Criteria getPrimaryUserAffiliationCriteria(final String userId, final String mdTypeId) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(userId)) {
            criteria.createAlias("organizationUser", "ou").
                    add(Restrictions.and(Restrictions.eq("ou.primaryKey.user.id", userId), Restrictions.eq("ou.metadataTypeEntity.id", mdTypeId)));
        }
        return criteria;
    }
    private Criteria getLocationsForOrganizationsCriteria(final String userId,
                                                          final Set<String> filter) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(userId)) {
            criteria.createAlias("organizationUser", "ou").
                    add(Restrictions.eq("ou.primaryKey.user.id", userId));
        }

        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if (searchBean != null && searchBean instanceof OrganizationSearchBean) {
            final OrganizationSearchBean organizationSearchBean = (OrganizationSearchBean) searchBean;

            final OrganizationEntity exampleEntity = organizationSearchBeanConverter
                    .convert(organizationSearchBean);
            exampleEntity.setId(null);
            criteria = this.getExampleCriteria(exampleEntity);

            if (organizationSearchBean.hasMultipleKeys()) {
                criteria.add(Restrictions.in(getPKfieldName(),
                        organizationSearchBean.getKeys()));
            } else if (StringUtils.isNotBlank(organizationSearchBean.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(),
                        organizationSearchBean.getKey()));
            }

            if (StringUtils.isNotBlank(organizationSearchBean.getInternalOrgId())) {
                criteria.add(Restrictions.eq("internalOrgId",
                        organizationSearchBean.getInternalOrgId()));
            }

            if (CollectionUtils.isNotEmpty(organizationSearchBean.getUserIdSet())) {
                criteria.createAlias("organizationUser", "ou");
                criteria.add(Restrictions.in("ou.primaryKey.user.id", organizationSearchBean.getUserIdSet()));
            }

            if (CollectionUtils.isNotEmpty(organizationSearchBean.getParentIdSet())) {
                criteria.createAlias("parentOrganizations", "pr");
                criteria.add(Restrictions.in("pr.id", organizationSearchBean.getParentIdSet()));
            }
            if (CollectionUtils.isNotEmpty(organizationSearchBean.getChildIdSet())) {
                criteria.createAlias("childOrganizations", "ch");
                criteria.add(Restrictions.in("ch.id", organizationSearchBean.getChildIdSet()));
            }

            if (StringUtils.isNotBlank(organizationSearchBean
                    .getValidParentTypeId())) {
                criteria.createAlias("organizationType.parentTypes",
                        "parentTypes").add(
                        Restrictions.eq("parentTypes.id",
                                organizationSearchBean.getValidParentTypeId()));
            }

            if (CollectionUtils.isNotEmpty(organizationSearchBean.getOrganizationTypeIdSet())) {
                criteria.add(Restrictions.in("organizationType.id",
                        organizationSearchBean.getOrganizationTypeIdSet()));
            }

            if (CollectionUtils.isNotEmpty(organizationSearchBean.getAttributes())) {
                for (final Tuple<String, String> attribute : organizationSearchBean.getAttributes()) {
                    DetachedCriteria crit = DetachedCriteria.forClass(OrganizationAttributeEntity.class);
                    if (StringUtils.isNotBlank(attribute.getKey()) && StringUtils.isNotBlank(attribute.getValue())) {
                        crit.add(Restrictions.and(Restrictions.eq("name", attribute.getKey()),
                                Restrictions.eq("value", attribute.getValue())));
                    } else if (StringUtils.isNotBlank(attribute.getKey())) {
                        crit.add(Restrictions.eq("name", attribute.getKey()));
                    } else if (StringUtils.isNotBlank(attribute.getValue())) {
                        crit.add(Restrictions.eq("value", attribute.getValue()));
                    }
                    crit.setProjection(Projections.property("organization.id"));
                    criteria.add(Subqueries.propertyIn("id", crit));
                }
            }

            if (StringUtils.isNotBlank(organizationSearchBean.getMetadataType())) {
                criteria.add(Restrictions.eq("type.id", organizationSearchBean.getMetadataType()));
            }

            if (organizationSearchBean.getIsSelectable() != null) {
                criteria.add(Restrictions.eq("selectable", organizationSearchBean.getIsSelectable()));
            }
            if (StringUtils.isNotBlank(organizationSearchBean.getAdminResourceId())) {
                criteria.add(Restrictions.eq("adminResource.id", organizationSearchBean.getAdminResourceId()));
            }
            if (StringUtils.isNotBlank(organizationSearchBean.getAbbreviation())) {
                criteria.add(Restrictions.eq("abbreviation", organizationSearchBean.getAbbreviation()));
            }
            if (StringUtils.isNotBlank(organizationSearchBean.getDomainName())) {
                criteria.add(Restrictions.eq("domainName", organizationSearchBean.getDomainName()));
            }
        }
        return criteria;
    }

    protected void setOderByCriteria(Criteria criteria, AbstractSearchBean sb) {
        List<SortParam> sortParamList = sb.getSortBy();
        for (SortParam sort : sortParamList) {
            if ("type".equals(sort.getSortBy())) {
                criteria.createAlias("organizationType", "orgTp", Criteria.LEFT_JOIN);
                criteria.addOrder(createOrder("orgTp.name", sort.getOrderBy()));
            } else {
                criteria.addOrder(createOrder(sort.getSortBy(), sort.getOrderBy()));
            }
        }
    }

    @Override
    protected Criteria getExampleCriteria(final OrganizationEntity organization) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(organization.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), organization.getId()));
        } else {
            if (StringUtils.isNotEmpty(organization.getName())) {
                String name = organization.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.START;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                    name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.START) ? MatchMode.ANYWHERE : MatchMode.END;
                }

                if (StringUtils.isNotEmpty(name)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", name, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", name));
                    }
                }
            }

//			if (organization.getOrganizationType() != null && StringUtils.isNotBlank(organization.getOrganizationType().getId())) {
//				criteria.add(Restrictions.eq("organizationType.id", organization.getOrganizationType().getId()));
//			}

            if (StringUtils.isNotBlank(organization.getInternalOrgId())) {
                criteria.add(Restrictions.eq("internalOrgId", organization.getInternalOrgId()));
            }

            if (organization.getAdminResource() != null && StringUtils.isNotBlank(organization.getAdminResource().getId())) {
                criteria.add(Restrictions.eq("adminResource.id", organization.getAdminResource().getId()));
            }
        }
//		criteria.addOrder(Order.asc("name"));
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> getChildOrganizations(String orgId,
                                                          Set<String> filter, final int from, final int size) {
        return getList(getChildOrganizationsCriteria(orgId, filter), from, size);
    }

    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> getParentOrganizations(String orgId,
                                                           Set<String> filter, final int from, final int size) {
        return getList(getParentOrganizationsCriteria(orgId, filter), from,
                size);
    }

    @Override
    public int getNumOfParentOrganizations(String orgId, Set<String> filter) {
        final Criteria criteria = getParentOrganizationsCriteria(orgId, filter)
                .setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public int getNumOfChildOrganizations(String orgId, Set<String> filter) {
        final Criteria criteria = getChildOrganizationsCriteria(orgId, filter)
                .setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public int count(final SearchBean searchBean) {
        final Criteria criteria = getExampleCriteria(searchBean);
        // criteria.setProjection(Projections.property("id"));
        return criteria.list().size();
    }

    private List<OrganizationEntity> getList(Criteria criteria, final int from,
                                             final int size) {
        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        criteria.addOrder(Order.asc("name"));
        return criteria.list();
    }

    private Criteria getParentOrganizationsCriteria(String orgId,
                                                    Set<String> filter) {
        Criteria criteria = getCriteria().createAlias("childOrganizations",
                "organization").add(Restrictions.eq("organization.id", orgId));
        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    private Criteria getChildOrganizationsCriteria(String orgId,
                                                   Set<String> filter) {
        Criteria criteria = getCriteria().createAlias("parentOrganizations",
                "organization").add(Restrictions.eq("organization.id", orgId));
        if (filter != null && !filter.isEmpty()) {
            criteria.add(Restrictions.in(getPKfieldName(), filter));
        }
        return criteria;
    }

    @Override
    public List<Org2OrgXrefEntity> getOrgToOrgXrefList() {
        List<Org2OrgXrefEntity> orgTypeXrefEntities = this.getSession().createCriteria(Org2OrgXrefEntity.class).list();
        return orgTypeXrefEntities;
    }

    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData) {
        Criteria criteria = getCriteria();

        if (allowedOrgTypes != null && !allowedOrgTypes.isEmpty()) {
            criteria.add(Restrictions.in("organizationType.id", allowedOrgTypes));
        }

        if (filterData != null && !filterData.isEmpty()) {
            criteria.add(Restrictions.in("id", filterData));
        }
        return criteria.list();
    }

    @Override
    public void deleteOrganizationUserDependency(final String orgId) {
        if (StringUtils.isNotBlank(orgId)) {
            this.getSession().createSQLQuery("DELETE FROM USER_AFFILIATION WHERE COMPANY_ID='" + orgId + "'").executeUpdate();
        }
    }

    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, final String attrValue) {
        List ret = new ArrayList<OrganizationEntity>();
        if (StringUtils.isNotBlank(attrName)) {
            // Can't use Criteria for @ElementCollection due to Hibernate bug
            // (org.hibernate.MappingException: collection was not an association)
            ret = getHibernateTemplate().find("select oa.organization from OrganizationAttributeEntity oa left join oa.values av where oa.name = ? and ((oa.isMultivalued = false and oa.value = ?) or (oa.isMultivalued = true and av in ?))", attrName, attrValue, attrValue);
        }
        return ret;
    }

    @Override
    public String getOrganizationAliases(String userId) {
        List<String> reval = null;
        StringBuilder sb = new StringBuilder("SELECT ALIAS FROM ");
        sb.append("COMPANY ");
        sb.append(" org ");
        sb.append("LEFT JOIN ");
        sb.append(" USER_AFFILIATION ");
        sb.append(" ua ON org.COMPANY_ID = ua.COMPANY_ID ");
        sb.append("WHERE ");
        if (StringUtils.isNotBlank(userId)) {
            sb.append(" ua.USER_ID='");
            sb.append(userId);
            sb.append("' AND ");
        }
        sb.append("ALIAS IS NOT NULL ");
        sb.append("AND org.ORG_TYPE_ID='ORGANIZATION' GROUP BY org.ALIAS");
        reval = this.getSession().createSQLQuery(sb.toString()).list();
        if (CollectionUtils.isNotEmpty(reval)) {
            return StringUtils.join(reval, ',');
        } else return null;
    }

    @Override
    public OrganizationEntity getPrimaryAffiliationForUser(
            final String userId, final String mdType) {
        final Criteria criteria = getPrimaryUserAffiliationCriteria(userId, mdType);
        List<OrganizationEntity> res = criteria.list();
        return CollectionUtils.isNotEmpty(res) ? res.get(0) : null;
    }
}
