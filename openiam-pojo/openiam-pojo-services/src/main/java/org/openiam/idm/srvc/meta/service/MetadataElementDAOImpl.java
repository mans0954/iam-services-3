package org.openiam.idm.srvc.meta.service;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for MetadataElement
 */
@Repository("metadataElementDAO")
public class MetadataElementDAOImpl extends
        BaseDaoImpl<MetadataElementEntity, String> implements
        MetadataElementDAO {
    @SuppressWarnings("unchecked")
    @Override
    public List<MetadataElementEntity> findbyCategoryType(String categoryType) {
        Criteria criteria = null;
        try {
            criteria = this.getCriteria().createAlias("metadataType", "mt")
                    .createAlias("mt.categories", "ct")
                    .add(Restrictions.eq("ct.categoryId", categoryType));
            return (List<MetadataElementEntity>) criteria.list();
        } catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public void removeByParentId(String id) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query qry = session.createQuery("delete from "
                    + domainClass.getSimpleName()
                    + " me where me.metadataTypeId = :id_t");
            qry.setString("id_t", id);
            qry.executeUpdate();
        } catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    protected String getPKfieldName() {
        return "metadataElementId";
    }

}
