package org.openiam.idm.srvc.auth.login;

// Generated May 22, 2009 10:08:01 AM by Hibernate Tools 3.2.2.GA

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void deleteByUser(String userId){
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ")
           .append(this.domainClass.getName())
           .append(" where userId=:user");

        getSession().createQuery(sql.toString()).setString("user", userId)
                    .executeUpdate();
    }
}
