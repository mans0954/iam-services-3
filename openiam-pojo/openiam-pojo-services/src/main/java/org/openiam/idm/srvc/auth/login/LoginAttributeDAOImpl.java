package org.openiam.idm.srvc.auth.login;
// Generated Feb 18, 2008 3:56:08 PM by Hibernate Tools 3.2.0.b11


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.domain.LoginAttributeEntity;
import org.springframework.stereotype.Repository;


@Repository("loginAttrDAO")
public class LoginAttributeDAOImpl extends BaseDaoImpl<LoginAttributeEntity, String> implements LoginAttributeDAO {

    private static final Log log = LogFactory.getLog(LoginAttributeDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "loginAttrId";
	}

}

