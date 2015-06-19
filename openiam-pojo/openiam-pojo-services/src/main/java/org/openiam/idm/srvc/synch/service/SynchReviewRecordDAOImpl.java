package org.openiam.idm.srvc.synch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Projections.rowCount;


@Repository("synchReviewRecordDAO")
public class SynchReviewRecordDAOImpl extends BaseDaoImpl<SynchReviewRecordEntity, String> implements SynchReviewRecordDAO {

    private static final Log log = LogFactory.getLog(SynchReviewRecordDAOImpl.class);

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(SynchReviewRecordEntity review) {
        Example example = Example.create(review);
        return getCriteria().add(example);
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        final Criteria criteria = getCriteria();
        return criteria;
    }

    @Override
    public SynchReviewRecordEntity getHeaderReviewRecord(String synchReviewId) {
        log.debug("getting header SynchReviewRecordEntity instance for SynchReviewEntity with id: " + synchReviewId);

        try {
            SynchReviewRecordEntity instance = (SynchReviewRecordEntity)getCriteria()
                    .add(Restrictions.eq("header", true))
                    .add(Restrictions.eq("synchReview.id", synchReviewId)).uniqueResult();
            return instance;

        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public List<SynchReviewRecordEntity> getRecordsBySynchReviewId(String synchReviewId, int from, int size) {
        log.debug("getting SynchReviewRecordEntities for SynchReviewEntity with id: " + synchReviewId);
        try {
            Criteria criteria = getCriteria()
                    .add(Restrictions.eq("synchReview.id", synchReviewId))
                    .add(Restrictions.eq("header", false));
            if (from > -1) {
                criteria.setFirstResult(from);
            }
            if (size > -1) {
                criteria.setMaxResults(size);
            }
            return criteria.list();
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public int getRecordsCountBySynchReviewId(String synchReviewId) {
        log.debug("getting SynchReviewRecordEntities count for SynchReviewEntity with id: " + synchReviewId);
        try {
            return ((Number)getCriteria()
                    .add(Restrictions.eq("synchReview.id", synchReviewId))
                    .add(Restrictions.eq("header", false))
                    .setProjection(rowCount())
                    .uniqueResult()).intValue();
        } catch (RuntimeException re) {
            log.error("get count failed", re);
            throw re;
        }
    }
}
