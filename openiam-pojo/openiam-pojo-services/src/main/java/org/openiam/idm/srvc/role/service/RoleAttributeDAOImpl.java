package org.openiam.idm.srvc.role.service;
// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11


import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.dto.*;
import org.springframework.stereotype.Repository;

@Repository("roleAttributeDAO")
public class RoleAttributeDAOImpl extends BaseDaoImpl<RoleAttributeEntity, String> implements RoleAttributeDAO {

    private static final Log log = LogFactory.getLog(RoleAttributeDAOImpl.class);
    
    /*
    private static String DELETE_BY_ROLE_ID = "DELETE FROM %s ra WHERE ra.roleId = :roleId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_ROLE_ID = String.format(DELETE_BY_ROLE_ID, domainClass.getSimpleName());
	}
	*/

	@Override
	protected String getPKfieldName() {
		return "roleAttrId";
	}
	/*
	@Override
	public void deleteByRoleId(String roleId) {
		final Query query = getSession().createQuery(DELETE_BY_ROLE_ID);
		query.setParameter("roleId", roleId);
		query.executeUpdate();
	}
	*/
}

