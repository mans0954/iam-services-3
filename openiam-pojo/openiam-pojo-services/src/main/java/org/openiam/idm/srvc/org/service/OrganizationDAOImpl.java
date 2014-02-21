package org.openiam.idm.srvc.org.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Org2OrgXref;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	public int getNumOfOrganizationsForUser(final String userId,
			final Set<String> filter) {
		final Criteria criteria = getOrganizationsForUserCriteria(userId,
				filter).setProjection(rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}
    @Override
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

	private Criteria getOrganizationsForUserCriteria(final String userId,
			final Set<String> filter) {
		final Criteria criteria = getCriteria();
		if (StringUtils.isNotBlank(userId)) {
			criteria.createAlias("users", "u").add(
					Restrictions.eq("u.id", userId));
		}

		if (filter != null && !filter.isEmpty()) {
			criteria.add(Restrictions.in(getPKfieldName(), filter));
		}
		return criteria;
	}
    @Override
	public List<OrganizationEntity> findRootOrganizations() {
		final Criteria criteria = getCriteria().add(
				Restrictions.isNull("parentId")).addOrder(
				Order.asc("name"));
		// .setFetchMode("attributes", FetchMode.JOIN);
		return criteria.list();
	}
    @Override
	public List<OrganizationEntity> findAllOrganization() {
		Criteria criteria = getCriteria().addOrder(
				Order.asc("name"));
		// .setFetchMode("attributes", FetchMode.JOIN);
		return criteria.list();
	}

	@Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if (searchBean != null && searchBean instanceof OrganizationSearchBean) {
			final OrganizationSearchBean organizationSearchBean = (OrganizationSearchBean) searchBean;

			final OrganizationEntity exampleEnity = organizationSearchBeanConverter
					.convert(organizationSearchBean);
			exampleEnity.setId(null);
			criteria = this.getExampleCriteria(exampleEnity);

			if (organizationSearchBean.hasMultipleKeys()) {
				criteria.add(Restrictions.in(getPKfieldName(),
						organizationSearchBean.getKeys()));
			} else if (StringUtils.isNotBlank(organizationSearchBean.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(),
						organizationSearchBean.getKey()));
			}

			if (StringUtils.isNotBlank(organizationSearchBean.getUserId())) {
				criteria.createAlias("users", "u").add(
						Restrictions.eq("u.id",
								organizationSearchBean.getUserId()));
			}

			if (StringUtils.isNotBlank(organizationSearchBean.getChildId())) {
				criteria.createAlias("childOrganizations", "child").add(
						Restrictions.eq("child.id",
								organizationSearchBean.getChildId()));
			}

			if (StringUtils.isNotBlank(organizationSearchBean.getParentId())) {
				criteria.createAlias("parentOrganizations", "parent").add(
						Restrictions.eq("parent.id",
								organizationSearchBean.getParentId()));
			}

			if (StringUtils.isNotBlank(organizationSearchBean
					.getValidParentTypeId())) {
				criteria.createAlias("organizationType.parentTypes",
						"parentTypes").add(
						Restrictions.eq("parentTypes.id",
								organizationSearchBean.getValidParentTypeId()));
			}
		}
		return criteria;
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

			if (organization.getOrganizationType() != null && StringUtils.isNotBlank(organization.getOrganizationType().getId())) {
				criteria.add(Restrictions.eq("organizationType.id", organization.getOrganizationType().getId()));
			}

			if (StringUtils.isNotBlank(organization.getInternalOrgId())) {
				criteria.add(Restrictions.eq("internalOrgId", organization.getInternalOrgId()));
			}
			
			if(organization.getAdminResource() != null && StringUtils.isNotBlank(organization.getAdminResource().getId())) {
				criteria.add(Restrictions.eq("adminResource.id", organization.getAdminResource().getId()));
			}
		}
		criteria.addOrder(Order.asc("name"));
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public List<OrganizationEntity> getChildOrganizations(String orgId,
			Set<String> filter, final int from, final int size) {
		return getList(getChildOrganizationsCriteria(orgId, filter), from, size);
	}

	@Override
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
    public List<Org2OrgXref> getOrgToOrgXrefList(){
        return this.getSession().createSQLQuery("SELECT COMPANY_ID as organizationId, MEMBER_COMPANY_ID as memberOrganizationId FROM COMPANY_TO_COMPANY_MEMBERSHIP")
                .addScalar("organizationId").addScalar("memberOrganizationId")
                .setResultTransformer(Transformers.aliasToBean(Org2OrgXref.class)).list();
    }

    @Override
    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData){
        Criteria criteria = getCriteria();

        if(allowedOrgTypes!=null && !allowedOrgTypes.isEmpty()){
            criteria.add(Restrictions.in("organizationType.id", allowedOrgTypes));
        }

        if(filterData!=null && !filterData.isEmpty()){
            criteria.add(Restrictions.in("id", filterData));
        }
        return criteria.list();
    }
}
