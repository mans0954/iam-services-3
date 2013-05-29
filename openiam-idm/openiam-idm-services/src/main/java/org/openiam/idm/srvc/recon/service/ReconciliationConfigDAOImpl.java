package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class ReconiliationConfig.
 */
@Repository("reconConfig")
public class ReconciliationConfigDAOImpl extends
        BaseDaoImpl<ReconciliationConfigEntity, String> implements
        ReconciliationConfigDAO {

    public ReconciliationConfigEntity findByResourceId(
            java.lang.String resourceId) throws HibernateException {
        Criteria criteria = this.getCriteria().add(
                Restrictions.eq("resourceId", resourceId));
        List<ReconciliationConfigEntity> result = (List<ReconciliationConfigEntity>) criteria
                .list();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public void removeByResourceId(java.lang.String resourceId) {
        try {

            Query qry = this.getSession().createQuery(
                    "delete org.openiam.idm.srvc.recon.dto.ReconciliationConfig rc "
                            + " where rc.resourceId = :resourceId ");
            qry.setString("resourceId", resourceId);
            qry.executeUpdate();
        } catch (HibernateException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    @Override
    protected String getPKfieldName() {
        return "reconConfigId";
    }

}
