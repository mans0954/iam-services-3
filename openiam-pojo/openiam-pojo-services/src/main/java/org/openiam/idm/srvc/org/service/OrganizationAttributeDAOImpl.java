package org.openiam.idm.srvc.org.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.springframework.stereotype.Repository;

@Repository("orgAttrDAO")
public class OrganizationAttributeDAOImpl extends BaseDaoImpl<OrganizationAttributeEntity, String> implements OrganizationAttributeDAO {

	private static String DELETE_BY_ORGANIZATION_ID = "DELETE FROM %s oa WHERE oa.organization.id = :organizationId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_ORGANIZATION_ID = String.format(DELETE_BY_ORGANIZATION_ID, domainClass.getSimpleName());
	}
	
	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public void deleteByOrganizationId(String organizationId) {
		final Query query = getSession().createQuery(DELETE_BY_ORGANIZATION_ID);
		query.setParameter("organizationId", organizationId);
		query.executeUpdate();
	}

    @Override
    public List<OrganizationAttributeEntity> findOrgAttributes(String organizationId) {
        Criteria criteria = getCriteria();
        criteria.createAlias("organization", "organization")
                .add(Restrictions.eq("organization.id", organizationId));
        return (List<OrganizationAttributeEntity>) criteria.list();
    }
}
