package org.openiam.idm.srvc.batch.service;

import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.springframework.stereotype.Repository;

@Repository("batchConfigDAO")
public class BatchConfigDAOImpl extends BaseDaoImpl<BatchTaskEntity, String> implements BatchConfigDAO {

	private static final Log log = LogFactory.getLog(BatchConfigDAOImpl.class);

	
	
	@Override
	protected Criteria getExampleCriteria(BatchTaskEntity entity) {
		final Criteria criteria = getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
			} else {
				if(StringUtils.isNotBlank(entity.getName())) {
					criteria.add(Restrictions.eq("name", entity.getName()));
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
