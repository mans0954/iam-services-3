package org.openiam.idm.srvc.auth.login;

// Generated May 22, 2009 10:08:01 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.AuthStateId;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO implementation object for AuthState. AuthState is used to track the state of an authenticated user across a 
 * security domain an managed systems.
 * @see org.openiam.idm.srvc.auth.dto
 * @author Suneet Shah
 */
@Repository("authStateDAO")
public class AuthStateDAOImpl extends BaseDaoImpl<AuthStateEntity, AuthStateId> implements AuthStateDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof AuthStateSearchBean) {
			final AuthStateSearchBean authStateSearchBean = (AuthStateSearchBean)searchBean;
			if(authStateSearchBean.getKey() != null) {
				if(StringUtils.isNotBlank(authStateSearchBean.getKey().getUserId())) {
					criteria.add(Restrictions.eq("id.userId", authStateSearchBean.getKey().getUserId()));
				}
				if(StringUtils.isNotBlank(authStateSearchBean.getKey().getTokenType())) {
					criteria.add(Restrictions.eq("id.tokenType", authStateSearchBean.getKey().getTokenType()));
				}
			}
			
			if(authStateSearchBean.isOnlyActive()) {
				criteria.add(Restrictions.ne("token", "LOGOUT").ignoreCase());
			}
		}
		return criteria;
	}
	
	@Override
	protected Criteria getExampleCriteria(final AuthStateEntity entity) {
		final Criteria criteria = super.getCriteria();
		if(entity != null) {
			if(entity.getId() != null) {
				if(entity.getId().getUserId() != null) {
					criteria.add(Restrictions.eq("id.userId", entity.getId().getUserId()));
				}
				if(entity.getId().getTokenType() != null) {
					criteria.add(Restrictions.eq("id.tokenType", entity.getId().getTokenType()));
				}
			}
			
			if(entity.getAa() != null) {
				criteria.add(Restrictions.eq("aa", entity.getAa()));
			}
			
		}
		return criteria;
	}

	@Override
	public void saveAuthState(final AuthStateEntity authState) {
		if(findById(authState.getId()) == null) {
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
           .append(" where id.userId=:userId");

        getSession().createQuery(sql.toString()).setString("userId", userId)
                    .executeUpdate();
    }
}
