package org.openiam.idm.srvc.org.service;


import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import org.hibernate.criterion.*;

import org.openiam.idm.srvc.org.dto.*;
import org.springframework.stereotype.Repository;

/**
 * Data access object implementation for OrganizationEntity.
 */
@Repository("organizationDAO")
public class OrganizationDAOImpl extends BaseDaoImpl<OrganizationEntity, String> implements OrganizationDAO {

    private static final Log log = LogFactory.getLog(OrganizationDAOImpl.class);

    protected SessionFactory getSessionFactory() {
        try {
            return (SessionFactory) new InitialContext()
                    .lookup("SessionFactory");
        } catch (Exception e) {
            log.error("Could not locate SessionFactory in JNDI", e);
            throw new IllegalStateException(
                    "Could not locate SessionFactory in JNDI");
        }
    }


    public OrganizationEntity findById(java.lang.String id) {
        log.debug("getting Organization instance with id: " + id);
        try {
            OrganizationEntity instance = (OrganizationEntity) sessionFactory.getCurrentSession()
                    .createCriteria(OrganizationEntity.class)
                    .setFetchMode("attributes", FetchMode.JOIN)
                    .add(Restrictions.eq("orgId",id)).uniqueResult();

            if (instance == null) {
                log.debug("get successful, no instance found");
            } else {
                log.debug("get successful, instance found");
            }
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<OrganizationEntity> findByExample(OrganizationEntity instance) {
        log.debug("finding Company instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria(OrganizationEntity.class)
                    .add(Example.create(instance))
                    .setFetchMode("attributes", FetchMode.JOIN)
                    .list();
            log.debug("find by example successful, result size: "
                    + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public OrganizationEntity add(OrganizationEntity instance) {
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

    public List<OrganizationEntity> findChildOrganization(String orgId) {
        log.debug("getting Organization instances for childobjects of  " + orgId);

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class)
                .add(Restrictions.eq("parentId", orgId))
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        criteria.setCacheable(true);
        criteria.setCacheRegion("query.organization.findChildOrganization");
        List<OrganizationEntity> result = criteria.list();
        if (result == null || result.size() == 0)
            return null;
        return result;
    }

    public OrganizationEntity findParent(String orgId) {
        OrganizationEntity curOrg = findById(orgId);
        if (curOrg != null && curOrg.getParentId() != null) {
            return findById(curOrg.getParentId());
        }

        return null;
    }

    public void remove(OrganizationEntity instance) {
        log.debug("deleting Address instance");
        try {
            sessionFactory.getCurrentSession().delete(instance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    /**
     * Returns a list of Organization objects that are root level entities; ie. they
     * don't have a parent.
     *
     * @return
     */
    public List<OrganizationEntity> findRootOrganizations() {
        log.debug("getting Organization instances at parent level  ");

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class)
                .add(Restrictions.isNull("parentId"))
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);

        criteria.setCacheable(true);
        List<OrganizationEntity> result = (List<OrganizationEntity>) criteria.list();

        if (result == null || result.size() == 0)
            return null;
        return result;

    }

    public List<OrganizationEntity> findOrganizationByType(String type, String parentId) {
        log.debug("getting Organization for a type  ");
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class).add(Restrictions.eq("metadataTypeId",type));
        if (parentId != null) {
            criteria.add(Restrictions.eq("parentId",parentId));
        }
        criteria.addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        List<OrganizationEntity> result = (List<OrganizationEntity>) criteria.list();
        if (result == null || result.size() == 0)
            return null;
        return result;
    }

    public List<OrganizationEntity> findAllOrganization() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class)
                .addOrder(Order.asc("organizationName"))
                .setFetchMode("attributes", FetchMode.JOIN);
        criteria.setCacheable(true);
        criteria.setCacheRegion("query.organization.findAllOrganization");
        List<OrganizationEntity> result = (List<OrganizationEntity>) criteria.list();
        if (result == null || result.size() == 0)
            return null;
        return result;
    }

    public List<OrganizationEntity> search(String name, String type, OrgClassificationEnum classification, String internalOrgId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class);
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
            criteria.add(Restrictions.eq("classification", classification));
        }
        if (internalOrgId != null) {
            criteria.add(Restrictions.eq("internalOrgId", internalOrgId));
        }
        criteria.setFetchMode("attributes", FetchMode.JOIN);
        return criteria.list();

    }

    public List<OrganizationEntity> findOrganizationByClassification(String parentId, OrgClassificationEnum classification) {
        log.debug("getting Organization for a classification  ");

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class);
        if (parentId == null) {
            criteria.add(Restrictions.eq("classification",classification));
        } else {
            criteria.add(Restrictions.eq("parentId",parentId));
        }
        criteria.addOrder(Order.asc("parentId")).addOrder(Order.asc("organizationName"));

        criteria.setCacheable(true);
        criteria.setCacheRegion("query.organization.findOrganizationByClassification");
        criteria.setFetchMode("attributes", FetchMode.JOIN);

        List<OrganizationEntity> result = (List<OrganizationEntity>) criteria.list();
        if (result == null || result.size() == 0)
            return null;
        return result;
    }

    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.org.service.OrganizationDAO#findOrganizationByStatus(java.lang.String, java.lang.String)
      */
    public List<OrganizationEntity> findOrganizationByStatus(String parentId,
                                                       String status) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(OrganizationEntity.class);

        if (parentId != null) {
            criteria.add(Restrictions.eq("parentId", parentId));
        }
        if (status != null) {
            criteria.add(Restrictions.eq("status", status));
        }
        criteria.add(Restrictions.eq("classification", OrgClassificationEnum.ORGANIZATION));
        criteria.setFetchMode("attributes", FetchMode.JOIN);

        return criteria.list();

    }

    @Override
    protected Criteria getExampleCriteria(final OrganizationEntity organization) {
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

}
