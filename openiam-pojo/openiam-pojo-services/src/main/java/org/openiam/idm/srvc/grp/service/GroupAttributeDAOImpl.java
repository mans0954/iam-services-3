package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.dto.*;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.stereotype.Repository;

@Repository("groupAttrDAO")
public class GroupAttributeDAOImpl extends BaseDaoImpl<GroupAttributeEntity, String> implements GroupAttributeDAO {

	/*
	 private static String DELETE_BY_GROUP_ID = "DELETE FROM %s ga WHERE ga.groupId = :groupId";
		
		@PostConstruct
		public void initSQL() {
			DELETE_BY_GROUP_ID = String.format(DELETE_BY_GROUP_ID, domainClass.getSimpleName());
		}
	*/
	
	@Override
	protected String getPKfieldName() {
		return "id";
	}

	/*
	@Override
	public void deleteByGroupId(String groupId) {
		final Query query = getSession().createQuery(DELETE_BY_GROUP_ID);
		query.setParameter("groupId", groupId);
		query.executeUpdate();
	}
	*/

	public List<GroupAttributeEntity> findGroupAttributes(String groupId, final Set<String> metadataElementIds){
		List<GroupAttributeEntity> retVal = null;
		if(CollectionUtils.isNotEmpty(metadataElementIds)) {
			final Criteria criteria = getCriteria().add(Restrictions.eq("group.id", groupId));
			criteria.add(Restrictions.in("element.id", metadataElementIds));
			retVal = criteria.list();
		} else {
			retVal = Collections.EMPTY_LIST;
		}
		return retVal;
	}

	@Override
	@LocalizedDatabaseGet
	public List<GroupAttributeEntity> findGroupAttributes(String groupId) {
		return (List<GroupAttributeEntity>)getCriteria().add(Restrictions.eq("group.id",groupId)).addOrder(Order.asc("name")).list();
	}
}
