package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.searchbean.converter.ReconConfigSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class ReconiliationConfig.
 */
@Repository
public class ReconciliationConfigDAOImpl extends
        BaseDaoImpl<ReconciliationConfigEntity, String> implements
        ReconciliationConfigDAO {

    @Autowired
    private ReconConfigSearchBeanConverter reconConfigSearchBeanConverter;

    public ReconciliationConfigEntity get(String id) {
        return (ReconciliationConfigEntity)getSession().get(ReconciliationConfigEntity.class,id);
    }

    @Override
    protected Criteria getExampleCriteria(SearchBean searchBean, boolean isCount) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof ReconConfigSearchBean) {
            final ReconConfigSearchBean reconSearchBean = (ReconConfigSearchBean)searchBean;
            criteria = getExampleCriteria(reconConfigSearchBeanConverter.convert(reconSearchBean));
        }
        return criteria;
    }

    public ReconciliationConfigEntity findByResourceIdByType(
            java.lang.String resourceId, String type) throws HibernateException {
        Criteria criteria = this.getCriteria().add(
                Restrictions.eq("resourceId", resourceId)).add(Restrictions.eq("reconType",type));
        List<ReconciliationConfigEntity> result = (List<ReconciliationConfigEntity>) criteria
                .list();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public List<ReconciliationConfigEntity> findByResourceId(
            java.lang.String resourceId) throws HibernateException {
        Criteria criteria = this.getCriteria().add(
                Restrictions.eq("resourceId", resourceId));
        List<ReconciliationConfigEntity> result = (List<ReconciliationConfigEntity>) criteria
                .list();
        return result;
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
