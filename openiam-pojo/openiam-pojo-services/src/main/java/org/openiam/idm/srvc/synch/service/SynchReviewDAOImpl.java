package org.openiam.idm.srvc.synch.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.searchbeans.SynchReviewSearchBean;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("synchReviewDAO")
public class SynchReviewDAOImpl extends BaseDaoImpl<SynchReviewEntity, String> implements SynchReviewDAO {

    private static final Log log = LogFactory.getLog(SynchReviewDAOImpl.class);

    @Override
    public List<SynchReviewEntity> findAllBySynchConfigId(String configId) {
        SynchReviewSearchBean searchBean = new SynchReviewSearchBean();
        searchBean.setSynchConfigId(configId);
        return getByExample(searchBean);
    }

    @Override
    protected String getPKfieldName() {
        return "synchReviewId";
    }

    @Override
    protected Criteria getExampleCriteria(SynchReviewEntity review) {
        Example example = Example.create(review);
        example.excludeProperty("sourceRejected"); // exclude boolean properties
        example.excludeProperty("skipSourceValid");
        example.excludeProperty("skipRecordValid");
        return getCriteria().add(example);
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        final Criteria criteria = getCriteria();
        if(searchBean != null && (searchBean instanceof SynchReviewSearchBean)) {
            final SynchReviewSearchBean sb = (SynchReviewSearchBean)searchBean;
            if (StringUtils.isNotBlank(sb.getSynchConfigId())) {
                criteria.add(Restrictions.eq("synchConfig.synchConfigId", sb.getSynchConfigId()));
            }
            if (sb.getKey() != null) {
                criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
            }
        }
        return criteria;
    }

}
