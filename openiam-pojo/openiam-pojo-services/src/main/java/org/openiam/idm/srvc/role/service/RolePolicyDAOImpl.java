package org.openiam.idm.srvc.role.service;
// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.dto.RolePolicy;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.springframework.stereotype.Repository;

@Repository("rolePolicyDAO")
public class RolePolicyDAOImpl extends BaseDaoImpl<RolePolicyEntity, String> implements RolePolicyDAO {
    protected boolean cachable() {
        return true;
    }
    private static final Log log = LogFactory.getLog(RolePolicyDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "rolePolicyId";
	}

}

