package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.springframework.stereotype.Repository;

@Repository("identityAnswerDAO")
public class UserIdentityAnswerDAOImpl extends BaseDaoImpl<UserIdentityAnswerEntity, String> implements UserIdentityAnswerDAO {

	private static final Log log = LogFactory.getLog(UserIdentityAnswerDAOImpl.class);

	@Override
	protected Criteria getExampleCriteria(final UserIdentityAnswerEntity example) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(example.getUserId())) {
			criteria.add(Restrictions.eq("userId", example.getUserId()));
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
