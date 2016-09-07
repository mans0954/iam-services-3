package org.openiam.idm.srvc.org.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Data access object implementation for OrganizationEntity.
 */
@Repository("organizationDAO")
public class OrganizationDAOImpl extends BaseDaoImpl<OrganizationEntity, String> implements OrganizationDAO {

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
            final OrganizationSearchBean sb = (OrganizationSearchBean) searchBean;

            if (sb.hasMultipleKeys()) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeys()));
            } else if (StringUtils.isNotBlank(sb.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
            }
            
            final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
            if(nameCriterion != null) {
            	criteria.add(nameCriterion);
            }

            if (StringUtils.isNotBlank(sb.getInternalOrgId())) {
                criteria.add(Restrictions.eq("internalOrgId",
                		sb.getInternalOrgId()));
            }

            if(CollectionUtils.isNotEmpty(sb.getUserIdSet())){
            	criteria.createAlias("users", "userXrefs")
						.createAlias("userXrefs.memberEntity", "user").add(
								Restrictions.in("user.id", sb.getUserIdSet()));
            }
            
            if(CollectionUtils.isNotEmpty(sb.getChildIdSet())) {
            	criteria.createAlias("childOrganizations", "childXrefs")
						.createAlias("childXrefs.memberEntity", "child").add(
						Restrictions.in("child.id", sb.getChildIdSet()));
			}
            
            if(CollectionUtils.isNotEmpty(sb.getGroupIdSet())) {
            	criteria.createAlias("groups", "groupXrefs")
						.createAlias("groupXrefs.memberEntity", "group").add(
						Restrictions.in("group.id", sb.getGroupIdSet()));
			}
            
            if(CollectionUtils.isNotEmpty(sb.getRoleIdSet())) {
            	criteria.createAlias("roles", "roleXrefs")
						.createAlias("roleXrefs.memberEntity", "role").add(
						Restrictions.in("role.id", sb.getRoleIdSet()));
			}
            
            if(CollectionUtils.isNotEmpty(sb.getResourceIdSet())) {
            	criteria.createAlias("resources", "resourceXrefs")
						.createAlias("resourceXrefs.memberEntity", "resource").add(
						Restrictions.in("resource.id", sb.getResourceIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(sb.getParentIdSet())) {
				criteria.createAlias("parentOrganizations", "parentXrefs")
						.createAlias("parentXrefs.entity", "parent").add(
						Restrictions.in("parent.id", sb.getParentIdSet()));
			}

            if (StringUtils.isNotBlank(sb
                    .getValidParentTypeId())) {
                criteria.createAlias("organizationType.parentTypes",
                        "parentTypes").add(
                        Restrictions.eq("parentTypes.id",
                        		sb.getValidParentTypeId()));
            }

            if (CollectionUtils.isNotEmpty(sb.getOrganizationTypeIdSet())) {
                criteria.add(Restrictions.in("organizationType.id",
                		sb.getOrganizationTypeIdSet()));
            }

            if (CollectionUtils.isNotEmpty(sb.getAttributes())) {
                for (final Tuple<String, String> attribute : sb.getAttributes()) {
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

            if (StringUtils.isNotBlank(sb.getMetadataType())) {
                criteria.add(Restrictions.eq("type.id", sb.getMetadataType()));
            }

            if (sb.getIsSelectable() != null) {
                criteria.add(Restrictions.eq("selectable", sb.getIsSelectable()));
            }
            if (StringUtils.isNotBlank(sb.getAbbreviation())) {
                criteria.add(Restrictions.eq("abbreviation", sb.getAbbreviation()));
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

    // BUG in Hibernate!! count() fails for some queries, while the normal
    // select succeeds. the count query is indeed incorrect:
    // select count(*) as y0_ from COMPANY this_ where
    // parenttype1_.ORG_TYPE_ID=? order by this_.COMPANY_NAME asc
    // using criteria.list.size();
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
	public List<OrgToOrgMembershipXrefEntity> getOrg2OrgXrefs() {
		return getSession().createCriteria(OrgToOrgMembershipXrefEntity.class).list();
	}

/*    @Override
    @LocalizedDatabaseGet
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, final String attrValue) {
        List ret = new ArrayList<OrganizationEntity>();
        if (StringUtils.isNotBlank(attrName)) {
            // Can't use Criteria for @ElementCollection due to Hibernate bug
            // (org.hibernate.MappingException: collection was not an association)
            ret = getHibernateTemplate().find("select oa.organization from OrganizationAttributeEntity oa left join oa.values av where oa.name = ? and ((oa.isMultivalued = false and oa.value = ?) or (oa.isMultivalued = true and av in ?))", attrName, attrValue, attrValue);
        }
        return ret;
    }*/

}
