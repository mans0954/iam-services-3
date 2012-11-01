package org.openiam.idm.srvc.org.service;


import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;


import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.dto.*;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.stereotype.Repository;

/**
 * Data access object implementation for Organization. 
 */
@Repository("organizationDAO")
public class OrganizationDAOImpl extends BaseDaoImpl<Organization, String> implements OrganizationDAO {
	private static final Log log = LogFactory.getLog(OrganizationDAOImpl.class);

//	private SessionFactory sessionFactory;

	
//	public void setSessionFactory(SessionFactory session) {
//		   this.sessionFactory = session;
//	}
//
//	protected SessionFactory getSessionFactory() {
//		try {
//			return (SessionFactory) new InitialContext()
//					.lookup("SessionFactory");
//		} catch (Exception e) {
//			log.error("Could not locate SessionFactory in JNDI", e);
//			throw new IllegalStateException(
//					"Could not locate SessionFactory in JNDI");
//		}
//	}
//
//	public Organization findById(java.lang.String id) {
//		log.debug("getting Organization instance with id: " + id);
//		try {
//			Organization instance = (Organization) sessionFactory.getCurrentSession()
//					.get("org.openiam.idm.srvc.org.dto.Organization", id);
//			if (instance == null) {
//				log.debug("get successful, no instance found");
//			} else {
//				log.debug("get successful, instance found");
//			}
//			return instance;
//		} catch (RuntimeException re) {
//			log.error("get failed", re);
//			throw re;
//		}
//	}

    @Override
    protected Criteria getExampleCriteria(final Organization organization) {
        final Criteria criteria = getCriteria();
        if(StringUtils.isNotBlank(organization.getOrgId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), organization.getOrgId()));
        } else {
            if(StringUtils.isNotEmpty(organization.getOrganizationName())) {
                String organizationName = organization.getOrganizationName();
                MatchMode matchMode = null;
                if(StringUtils.indexOf(organizationName, "*") == 0) {
                    matchMode = MatchMode.START;
                    organizationName = organizationName.substring(1);
                }
                if(StringUtils.isNotEmpty(organizationName) && StringUtils.indexOf(organizationName, "*") == organizationName.length() - 1) {
                    organizationName = organizationName.substring(0, organizationName.length() - 1);
                    matchMode = (matchMode == MatchMode.START) ? MatchMode.ANYWHERE : MatchMode.END;
                }

                if(StringUtils.isNotEmpty(organizationName)) {
                    if(matchMode != null) {
                        criteria.add(Restrictions.ilike("organizationName", organizationName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("organizationName", organizationName));
                    }
                }
            }
        }
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "orgId";
    }
    @Deprecated
    public List findByExample(Organization instance) {
		log.debug("finding Company instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria(
					"org.openiam.idm.srvc.user.Company").add(
					Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
    @Deprecated
	public Organization add(Organization instance) {
		log.debug("persisting Organization instance");
		try {
			sessionFactory.getCurrentSession().persist(instance);
			log.debug("persist successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public List<Organization> findChildOrganization(String orgId) {
		log.debug("getting Organization instances for childobjects of  " + orgId );

		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
						" where a.parentId = :orgId order by a.organizationName asc  " );
		qry.setString("orgId", orgId);
		qry.setCacheable(true);
		qry.setCacheRegion("query.organization.findChildOrganization");
		List<Organization> result = (List<Organization>)qry.list();
		if (result == null || result.size() == 0)
			return null;
		return result;
	}

	public Organization findParent(String orgId) {
		Organization curOrg = findById(orgId);
		if (curOrg != null && curOrg.getParentId() != null) {
			return findById(curOrg.getParentId());
		}
		return null;
	}
    @Deprecated
	public void remove(Organization instance) {
		log.debug("deleting Address instance");
		try {
			sessionFactory.getCurrentSession().delete(instance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

//	public Organization update(Organization instance) {
//		log.debug("merging Organization instance");
//		try {
//			return (Organization)sessionFactory.getCurrentSession().merge(instance);
//		} catch (RuntimeException re) {
//			log.error("merge failed", re);
//			throw re;
//		}
//	}
	
	/**
	 * Returns a list of Organization objects that are root level entities; ie. they 
	 * don't have a parent. 
	 * @return
	 */
	public List<Organization> findRootOrganizations() {
		log.debug("getting Organization instances at parent level  "  );

		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
						" where a.parentId = null order by a.organizationName asc" );
		List<Organization> result = (List<Organization>)qry.list();
		qry.setCacheable(true);
		if (result == null || result.size() == 0)
			return null;
		return result;
		
	}
	
	public List<Organization> findOrganizationByType(String type, String parentId ) {
		log.debug("getting Organization for a type  "  );
		Query qry = null;
		
		Session session = sessionFactory.getCurrentSession();
		if (parentId == null) {
			qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
							" where a.metadataTypeId = :type order by a.organizationName asc " );

		}else {
			qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
			" where a.metadataTypeId = :type and a.parentId = :parentId order by a.organizationName asc " );
			qry.setString("parentId", parentId);
		}	
		qry.setString("type", type);
		List<Organization> result = (List<Organization>)qry.list();	
		if (result == null || result.size() == 0)
			return null;
		return result;		
	}
	
	public List<Organization> findAllOrganization() {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
						" order by a.organizationName asc " );
		qry.setCacheable(true);
		qry.setCacheRegion("query.organization.findAllOrganization");
		List<Organization> result = (List<Organization>)qry.list();	
		if (result == null || result.size() == 0)
			return null;
		return result;			
	}
	
	public List<Organization> search(String name, String type, String classification, String internalOrgId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria =  session.createCriteria(org.openiam.idm.srvc.org.dto.Organization.class);
		if (name == null && type == null) {
			// show all the orgs
			criteria.add(Restrictions.like("organizationName", "%"));
		}
		if (name != null) {
			criteria.add(Restrictions.like("organizationName", name + "%"));
		}
		if (type != null) {
			criteria.add(Restrictions.eq("metadataTypeId", type));
		}
		if (classification != null) {
			criteria.add(Restrictions.eq("classification", OrgClassificationEnum.valueOf(classification)));
		}
		if (internalOrgId != null) {
			criteria.add(Restrictions.eq("internalOrgId", internalOrgId));
		}
		
		return criteria.list();
		
	}

	public List<Organization> findOrganizationByClassification(String parentId, String classification) {
		log.debug("getting Organization for a classification  "  );
		Query qry = null;
		
		Session session = sessionFactory.getCurrentSession();
		if (parentId == null) {
			qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
							" where a.classification = :classification " +
							" order by a.parentId, a.organizationName asc " );
		}else {
			qry = session.createQuery("from org.openiam.idm.srvc.org.dto.Organization a " +
			" where a.parentId = :parentId and " +
			" 	    a.classification = :classification " +
			" order by a.parentId, a.organizationName asc " );
			qry.setString("parentId", parentId);
			
		}
		
		qry.setString("classification", classification);
		
		qry.setCacheable(true);
		qry.setCacheRegion("query.organization.findOrganizationByClassification");
		
		List<Organization> result = (List<Organization>)qry.list();	
		if (result == null || result.size() == 0)
			return null;
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.org.service.OrganizationDAO#findOrganizationByStatus(java.lang.String, java.lang.String)
	 */
	public List<Organization> findOrganizationByStatus(String parentId,
			String status) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria =  session.createCriteria(org.openiam.idm.srvc.org.dto.Organization.class);
		
		if (parentId != null) {
			criteria.add(Restrictions.eq("parentId", parentId));
		}
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		criteria.add(Restrictions.eq("classification", OrgClassificationEnum.ORGANIZATION));

		
		return criteria.list();
		
	}
		
	
}
