package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.OrderDaoImpl;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.searchbean.converter.IdentityQuestionSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation object for the domain model class IdentityQuestion.
 */
@Repository("identityQuestDAO")
public class IdentityQuestionDAOImpl extends OrderDaoImpl<IdentityQuestionEntity, String> implements IdentityQuestionDAO {

	private static final Log log = LogFactory.getLog(IdentityQuestionDAOImpl.class);

	@Autowired
	private IdentityQuestionSearchBeanConverter questionSearchBeanConverter;

	@Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if (searchBean != null && searchBean instanceof IdentityQuestionSearchBean) {
			final IdentityQuestionSearchBean identityQuestionSearchBean = (IdentityQuestionSearchBean) searchBean;
			final  IdentityQuestionEntity entity = questionSearchBeanConverter.convert(identityQuestionSearchBean);
			criteria = this.getExampleCriteria(entity);
		}
		return criteria;
	}

	@Override
	protected Criteria getExampleCriteria(final IdentityQuestionEntity example) {
		final Criteria criteria = getCriteria();
		if (example.getActive() !=null){
			criteria.add(Restrictions.eq("active", example.getActive()));
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

	protected String getReferenceType() {
		return "IdentityQuestionEntity.displayNameMap";
	}
}
