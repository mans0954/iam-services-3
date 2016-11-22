package org.openiam.idm.srvc.batch.service;

import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.springframework.stereotype.Repository;

@Repository("batchConfigDAO")
public class BatchConfigDAOImpl extends BaseDaoImpl<BatchTaskEntity, String> implements BatchConfigDAO {

	private static final Log log = LogFactory.getLog(BatchConfigDAOImpl.class);


    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof BatchTaskSearchBean) {
            final BatchTaskSearchBean sb = (BatchTaskSearchBean)searchBean;

            if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
            	final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
                
                if(sb.getEnabled() != null) {
                	criteria.add(Restrictions.eq("enabled", sb.getEnabled()));
                }
            }
        }
        return criteria;
    }



	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
