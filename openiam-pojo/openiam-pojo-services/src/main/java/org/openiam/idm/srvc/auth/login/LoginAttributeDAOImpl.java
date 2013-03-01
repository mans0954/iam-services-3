package org.openiam.idm.srvc.auth.login;
// Generated Feb 18, 2008 3:56:08 PM by Hibernate Tools 3.2.0.b11


import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.domain.LoginAttributeEntity;
import org.springframework.stereotype.Repository;


@Repository("loginAttrDAO")
public class LoginAttributeDAOImpl extends BaseDaoImpl<LoginAttributeEntity, String> implements LoginAttributeDAO {

    private static final Log log = LogFactory.getLog(LoginAttributeDAOImpl.class);

    private static String DELETE_BY_LOGIN_ID = "DELETE FROM %s lg WHERE lg.loginId = :loginId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_LOGIN_ID = String.format(DELETE_BY_LOGIN_ID, domainClass.getSimpleName());
	}
    
	@Override
	protected String getPKfieldName() {
		return "loginAttrId";
	}

	@Override
	public void deleteByLoginId(String loginId) {
		final Query query = getSession().createQuery(DELETE_BY_LOGIN_ID);
		query.setParameter("loginId", loginId);
		query.executeUpdate();
	}

}

