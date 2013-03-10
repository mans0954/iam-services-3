package org.openiam.idm.srvc.pswd.service;

// Generated Aug 23, 2009 12:07:53 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.role.dto.Role;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

/**
 * DAO implementation object for the domain model class IdentityQuestion.
 */
@Repository("identityQuestDAO")
public class IdentityQuestionDAOImpl extends BaseDaoImpl<IdentityQuestionEntity, String> implements IdentityQuestionDAO {

	private static final Log log = LogFactory
			.getLog(IdentityQuestionDAOImpl.class);
	
	@Override
	public List<IdentityQuestion> findAllQuestionsByQuestionGroup(String questionGroup) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from IdentityQuestion iq "
				+ " where iq.identityQuestGrp = :questionGroup" +
				  " order by iq.questionText asc ");
		qry.setString("questionGroup", questionGroup);
		// enable caching
		qry.setCacheable(true);
		qry.setCacheRegion("query.iq.findQuestionByQuestionGroup");
		
		List<IdentityQuestion> result = (List<IdentityQuestion>) qry.list();
		if (result == null || result.size() == 0)
			return null;

		return result;		
	}
	
	@Override
	public List<IdentityQuestion> findAllQuestionsByUser(String userId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from IdentityQuestion iq "
				+ " where iq.userId = :userId" +
				  " order by iq.questionText asc ");
		qry.setString("userId", userId);

		
		List<IdentityQuestion> result = (List<IdentityQuestion>) qry.list();
		if (result == null || result.size() == 0)
			return null;

		return result;			
	}

	@Override
	protected String getPKfieldName() {
		return "identityQuestionId";
	}
}
