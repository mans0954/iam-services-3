package org.openiam.idm.srvc.org.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.springframework.stereotype.Repository;

@Repository("orgAttrDAO")
public class OrganizationAttributeDAOImpl extends BaseDaoImpl<OrganizationAttributeEntity, String> implements OrganizationAttributeDAO {

	@Override
	protected String getPKfieldName() {
		return "attrId";
	}


}
