package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.*;

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

	@Override
	public List<Map<String,String>> getAttributeByGroupIds(List<String> groupIds, String attrName) {
		String sql = new String("SELECT new map(ga.value as value, ge.id as id, ge.name as name) "
				+ "from org.openiam.idm.srvc.grp.domain.GroupAttributeEntity ga, org.openiam.idm.srvc.grp.domain.GroupEntity ge "
				+ "where ga.group.id=ge.id and ge.id in (:groupIds) ");
		Session session = getSession();
		Query qry = session.createQuery(sql);
		qry.setParameterList("groupIds", groupIds);
		log.info("sql = " + qry.toString());

		List<Map<String,String>> results = (List<Map<String,String>>) qry.setCacheable(this.cachable()).list();
		return results;
	}

	@Override
	public String getAttributeByGroupId(String groupId, String attrName){
		String sql = new String("SELECT ga.value as value "
				+ "from org.openiam.idm.srvc.grp.domain.GroupAttributeEntity ga "
				+ "where ga.group.id=:groupIds ");
		Session session = getSession();
		Query qry = session.createQuery(sql);
		qry.setString("groupIds", groupId);

		List<String> results = (List<String>) qry.setCacheable(this.cachable()).list();
		if (results == null) {
			return null;
		}
		return results.get(0);
	}


}
