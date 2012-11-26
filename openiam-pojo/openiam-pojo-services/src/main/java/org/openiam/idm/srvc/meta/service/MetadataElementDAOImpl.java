package org.openiam.idm.srvc.meta.service;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
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
        try {
            Session session = sessionFactory.getCurrentSession();
            Query qry = session
                    .createQuery("select me from MetadataElement me, "
                            + "		CategoryType ct, MetadataType m "
                            + " where ct.id.categoryId = :categoryId and "
                            + "       ct.id.typeId  = m.metadataTypeId  and "
                            + " 	    m.metadataTypeId = me.metadataTypeId "
                            + " order by me.metadataTypeId, me.attributeName ");
            qry.setString("categoryId", categoryType);
            List<MetadataElementEntity> results = (List<MetadataElementEntity>) qry
                    .list();
            return results;
        } catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public void removeByParentId(String id) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query qry = session
                    .createQuery("delete org.openiam.idm.srvc.meta.domain.MetadataElementEntity me "
                            + " where me.metadataTypeId = :id ");
            qry.setString("id", id);
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
