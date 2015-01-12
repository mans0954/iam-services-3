package org.openiam.idm.srvc.batch.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.batch.dao.BatchScheduleDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BatchScheduleDAOImpl extends BaseDaoImpl<BatchTaskScheduleEntity, String> implements BatchScheduleDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = super.getCriteria();
		if(searchBean != null && searchBean instanceof BatchTaskScheduleSearchBean) {
			final BatchTaskScheduleSearchBean batchSearchBean = (BatchTaskScheduleSearchBean)searchBean;
			if(StringUtils.isNotBlank(batchSearchBean.getTaskId())) {
				criteria.add(Restrictions.eq("task.id", batchSearchBean.getTaskId()));
			}
			if(batchSearchBean.getCompleted() != null) {
				criteria.add(Restrictions.eq("completed", batchSearchBean.getCompleted()));
			}
			
			if(batchSearchBean.getRunning() != null) {
				criteria.add(Restrictions.eq("running", batchSearchBean.getRunning()));
			}
			
			if(batchSearchBean.getNextScheduledRun() != null) {
				criteria.add(Restrictions.eq("nextScheduledRun", batchSearchBean.getNextScheduledRun()));
			} else {
				if(batchSearchBean.getNextScheduledRunFrom() != null && batchSearchBean.getNextScheduledRunTo() != null) {
					criteria.add(Restrictions.between("nextScheduledRun", batchSearchBean.getNextScheduledRunFrom(), batchSearchBean.getNextScheduledRunTo()));
				} else if(batchSearchBean.getNextScheduledRunFrom() != null) {
					criteria.add(Restrictions.gt("nextScheduledRun", batchSearchBean.getNextScheduledRunFrom()));
				} else if(batchSearchBean.getNextScheduledRunTo() != null) {
					criteria.add(Restrictions.lt("nextScheduledRun", batchSearchBean.getNextScheduledRunTo()));
				}
			}
		}
		return criteria;
	}

	
}
