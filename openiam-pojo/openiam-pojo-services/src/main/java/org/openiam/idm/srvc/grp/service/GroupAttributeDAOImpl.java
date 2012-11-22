package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.dto.*;
import org.springframework.stereotype.Repository;

@Repository("groupAttrDAO")
public class GroupAttributeDAOImpl extends BaseDaoImpl<GroupAttributeEntity, String> implements GroupAttributeDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
