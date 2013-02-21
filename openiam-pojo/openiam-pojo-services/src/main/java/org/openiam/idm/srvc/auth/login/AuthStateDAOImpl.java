package org.openiam.idm.srvc.auth.login;

// Generated May 22, 2009 10:08:01 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.dto.AuthStateEntity;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

/**
 * DAO implementation object for AuthState. AuthState is used to track the state of an authenticated user across a 
 * security domain an managed systems.
 * @see org.openiam.idm.srvc.auth.dto
 * @author Suneet Shah
 */
@Repository("authStateDAO")
public class AuthStateDAOImpl extends BaseDaoImpl<AuthStateEntity, String> implements AuthStateDAO {

	@Override
	protected String getPKfieldName() {
		return "userId";
	}

	@Override
	public void saveAuthState(final AuthStateEntity authState) {
		if(findById(authState.getUserId()) == null) {
			save(authState);
		} else {
			merge(authState);
		}
	}
}
