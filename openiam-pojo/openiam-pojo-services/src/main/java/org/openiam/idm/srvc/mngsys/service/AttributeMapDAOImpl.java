package org.openiam.idm.srvc.mngsys.service;

/**
 * @author zaporozhec
 */
import java.util.List;
import javax.naming.InitialContext;

import net.sf.ehcache.search.expression.Criteria;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for domain model class AttributeMap.
 */
@Repository("attributeMapDAO")
public class AttributeMapDAOImpl extends
		BaseDaoImpl<AttributeMapEntity, String> implements AttributeMapDAO {

	public List<AttributeMapEntity> findByResourceId(String resourceId) {

		return (List<AttributeMapEntity>) this.getCriteria()
				.add(Restrictions.eq("resourceId", resourceId))
				.addOrder(Order.asc("mapForObjectType")).addOrder(Order.asc("managedSysId")).list();
	}

	public List<AttributeMapEntity> findAllAttributeMaps() {

		return (List<AttributeMapEntity>) this.getCriteria()
				.addOrder(Order.asc("resourceId")).list();
	}

	public int removeResourceAttributeMaps(String resourceId) {

		SQLQuery qry = getSession().createSQLQuery(
				"delete " + "from ATTRIBUTE_MAP  "
						+ "where RESOURCE_ID = :resourceId");

		qry.setString("resourceId", resourceId);
		return qry.executeUpdate();
	}

	@Override
	protected String getPKfieldName() {
		return "attributeMapId";
	}

}
