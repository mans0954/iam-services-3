package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation object for the domain model class IdentityQuestion.
 */
@Repository("identityQuestDAO")
public class IdentityQuestionDAOImpl extends BaseDaoImpl<IdentityQuestionEntity, String> implements IdentityQuestionDAO {

	private static final Log log = LogFactory.getLog(IdentityQuestionDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(final IdentityQuestionEntity example) {
		final Criteria criteria = getCriteria();
		if (example.isActive() !=null){
			criteria.add(Restrictions.eq("active", example.isActive()));
		}
		if (StringUtils.isNotEmpty(example.getQuestionText())) {
			criteria.add(Restrictions.eq("questionText", example.getQuestionText()));
		}
		if(example.getIdentityQuestGrp() != null) {
			criteria.add(Restrictions.eq("identityQuestGrp.id", example.getIdentityQuestGrp().getId()));
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
