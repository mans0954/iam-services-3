package org.openiam.idm.srvc.batch.service;

import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
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
            final BatchTaskSearchBean taskSearchBean = (BatchTaskSearchBean)searchBean;

            if(StringUtils.isNotBlank(taskSearchBean.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), taskSearchBean.getKey()));
            } else {
                if(StringUtils.isNotBlank(taskSearchBean.getName())) {
                    criteria.add(Restrictions.eq("name", taskSearchBean.getName()));
                }
                
                if(taskSearchBean.getEnabled() != null) {
                	criteria.add(Restrictions.eq("enabled", taskSearchBean.getEnabled()));
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
