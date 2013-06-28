package org.openiam.idm.srvc.mngsys.service;

/**
 * @author zaporozhec
 */
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.DataException;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
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

    public List<AttributeMapEntity> findBySynchConfigId(String synchConfigId) {
        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("synchConfigId", synchConfigId))
                .addOrder(Order.asc("mapForObjectType")).addOrder(Order.asc("synchConfigId")).list();
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
    public AttributeMapEntity add(AttributeMapEntity entity) {
        if (StringUtils.isEmpty(entity.getManagedSysId()) &&
                StringUtils.isEmpty(entity.getSynchConfigId())) {
            String reason = "managedSysId or synchConfigId must be specified";
            throw new DataException(reason, new Exception("managedSysId and synchConfigId are null"));
        }
        return super.add(entity);
    }
    public void update(AttributeMapEntity entity) {
        if (StringUtils.isEmpty(entity.getManagedSysId()) &&
                StringUtils.isEmpty(entity.getSynchConfigId())) {
            String reason = "managedSysId or synchConfigId must be specified";
            throw new DataException(reason, new Exception("managedSysId and synchConfigId are null"));
        }
        super.update(entity);
    }

	@Override
	protected String getPKfieldName() {
		return "attributeMapId";
	}

}
