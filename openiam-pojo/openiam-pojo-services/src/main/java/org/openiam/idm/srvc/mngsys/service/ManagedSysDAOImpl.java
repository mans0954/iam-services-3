package org.openiam.idm.srvc.mngsys.service;

// Generated Nov 3, 2008 12:14:44 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Home object for domain model class ManagedSys.
 *
 * @author Hibernate Tools
 * @see org.openiam.idm.srvc.mngsys.service
 */
@Repository("managedSysDAO")
public class ManagedSysDAOImpl extends BaseDaoImpl<ManagedSysEntity, String> implements ManagedSysDAO {


    @Override
    protected Criteria getExampleCriteria(ManagedSysEntity example) {
        final Criteria criteria = getCriteria().addOrder(Order.asc("name"));
        if (example != null) {
            if (StringUtils.isNotBlank(example.getId())) {
                criteria.add(Restrictions.eq(getPKfieldName(), example.getId()));
            } else {
                if (StringUtils.isNotBlank(example.getName())) {
                    String name = example.getName();
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
                            criteria.add(Restrictions.ilike("name", name, matchMode));
                        } else {
                            criteria.add(Restrictions.eq("name", name));
                        }
                    }
                }
            }
        }
        return criteria;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public List<ManagedSysEntity> findbyConnectorId(String connectorId) {
        Criteria criteria = getCriteria().add(Restrictions.eq("connectorId", connectorId)).addOrder(Order.asc("name")).addOrder(Order.asc(getPKfieldName()));
        return (List<ManagedSysEntity>) criteria.list();
    }

//	@SuppressWarnings(value = "unchecked")
//	public List<ManagedSysEntity> findbyDomain(String domainId) {
//        Criteria criteria = getCriteria().add(Restrictions.eq("domainId",domainId)).addOrder(Order.asc(getPKfieldName()));
//		return (List<ManagedSysEntity>)criteria.list();
//	}

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<ManagedSysEntity> findAllManagedSys() {
        Criteria criteria = getCriteria().addOrder(Order.asc("name"));
        return (List<ManagedSysEntity>) criteria.list();
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findByName(java.lang.String)
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public ManagedSysEntity findByName(String name) {
        Criteria criteria = getCriteria().add(Restrictions.eq("name", name)).addOrder(Order.asc("name")).addOrder(Order.asc(getPKfieldName()));
        List<ManagedSysEntity> results = (List<ManagedSysEntity>) criteria.list();
        if (CollectionUtils.isNotEmpty(results)) {
            log.info("ManagedSys resultSet = " + results.size());
            return results.get(0);
        } else {
            log.info("No managedSys objects found. [findByName]");
            return null;
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public ManagedSysEntity findByResource(String resourceId, String status) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("resourceId", resourceId))
                .add(Restrictions.eq("status", status))
                .addOrder(Order.asc("name"));

        List<ManagedSysEntity> results = (List<ManagedSysEntity>) criteria.list();

        if (CollectionUtils.isNotEmpty(results)) {
            // avoids an exception in the event that there is more than 1 row with the same name
            log.info("ManagedSys resultSet = " + results.size());
            return results.get(0);
        }
        log.info("No managedSys objects found. [findByResource]");
        return null;

    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public String findIdByResource(String resourceId, String status) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("resourceId", resourceId))
                .add(Restrictions.eq("status", status))
                .setProjection(Projections.id());

        List<String> results = (List<String>) criteria.list();

        if (CollectionUtils.isNotEmpty(results)) {
            // avoids an exception in the event that there is more than 1 row with the same name
            log.info("ManagedSys resultSet = " + results.size());
            return results.get(0);
        }
        log.info("No managedSys objects fround.[findIdByResource]");
        return null;

    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<ManagedSysEntity> findByResource(String resourceId) {
        return getCriteria()
                .add(Restrictions.eq("resourceId", resourceId))
                .addOrder(Order.asc("name")).list();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<ManagedSysEntity> findAllManagedSysNames() {
        List<ManagedSysEntity> ret = new ArrayList<ManagedSysEntity>();
        List<Object[]> tmp = getHibernateTemplate().find("select id, name from ManagedSysEntity order by name asc");
        if (tmp != null && !tmp.isEmpty()) {
            Iterator<Object[]> iterator = tmp.iterator();
            while (iterator.hasNext()) {
                Object[] obj = iterator.next();
                ManagedSysEntity msys = new ManagedSysEntity();
                msys.setId((String) obj[0]);
                msys.setName((String) obj[1]);
                ret.add(msys);
            }
        }
        return ret;
    }
}
