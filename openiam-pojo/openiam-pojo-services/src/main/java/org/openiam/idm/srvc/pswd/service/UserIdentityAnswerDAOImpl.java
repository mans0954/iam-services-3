package org.openiam.idm.srvc.pswd.service;

// Generated Aug 23, 2009 12:07:53 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

/**
 * DAO implementation object for the domain model class  UserIdentityAnswer.
 */
@Repository("identityAnswerDAO")
public class UserIdentityAnswerDAOImpl extends BaseDaoImpl<UserIdentityAnswerEntity, String> implements UserIdentityAnswerDAO {

	private static final Log log = LogFactory.getLog(UserIdentityAnswerDAOImpl.class);

	@Override
	public List<UserIdentityAnswer> findAnswersByUser(String userId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from UserIdentityAnswer ans "
				+ " where ans.userId = :userId" +
				  " order by ans.questionText asc ");
		qry.setString("userId", userId);

		
		List<UserIdentityAnswer> result = (List<UserIdentityAnswer>) qry.list();
		if (result == null || result.size() == 0)
			return null;

		return result;			
	}

	@Override
	protected String getPKfieldName() {
		return "identityAnsId";
	}

}
