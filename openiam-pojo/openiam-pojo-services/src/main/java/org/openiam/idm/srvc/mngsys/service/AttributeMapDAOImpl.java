package org.openiam.idm.srvc.mngsys.service;

/**
 * @author zaporozhec
 */

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.DataException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * DAO implementation for domain model class AttributeMap.
 */
@Repository("attributeMapDAO")
public class AttributeMapDAOImpl extends
        BaseDaoImpl<AttributeMapEntity, String> implements AttributeMapDAO {

    public List<AttributeMapEntity> findByResourceId(String resourceId) {

        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("resourceId", resourceId))
                .addOrder(Order.asc("mapForObjectType"))
                .addOrder(Order.asc("mngSysPolicy.id")).list();
    }

    public List<AttributeMapEntity> findByMngSysPolicyId(String mngSysPolicyId) {
        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("mngSysPolicy.id", mngSysPolicyId))
                .addOrder(Order.asc("mapForObjectType"))
                .addOrder(Order.asc("mngSysPolicy.id")).list();
    }

    public List<AttributeMapEntity> findBySynchConfigId(String synchConfigId) {
        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("synchConfigId", synchConfigId))
                .addOrder(Order.asc("mapForObjectType"))
                .addOrder(Order.asc("synchConfigId")).list();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        final Criteria criteria = getCriteria();
        if (searchBean instanceof AttributeMapSearchBean) {
            AttributeMapSearchBean amsb = (AttributeMapSearchBean) searchBean;
            if (StringUtils.isNotBlank(amsb.getResourceId())) {
                criteria.add(Restrictions.eq("resourceId", amsb.getResourceId()));
            } else if (StringUtils.isNotBlank(amsb.getSynchConfigId())) {
                criteria.add(Restrictions.eq("synchConfigId", amsb.getSynchConfigId()));
            }
        }
        return criteria;
    }

    public List<AttributeMapEntity> findAllAttributeMaps() {
        return (List<AttributeMapEntity>) this.getCriteria()
                .addOrder(Order.asc("resourceId")).list();
    }

    public List<AttributeMapEntity> findByManagedSysId(String managedSysId) {
        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("managedSystem.id", managedSysId))
                .addOrder(Order.asc("mapForObjectType"))
                .addOrder(Order.asc("managedSystem.id")).list();
    }

    public AttributeMapEntity add(AttributeMapEntity entity) {
        if ((entity.getMngSysPolicy() == null)
                && StringUtils.isEmpty(entity.getSynchConfigId())) {
            String reason = "managedSys policy or synchConfigId must be specified";
            throw new DataException(reason, new Exception(
                    "managedSys policy and synchConfigId are null"));
        }

        return super.add(entity);
    }

    public void update(AttributeMapEntity entity) {
        if ((entity.getMngSysPolicy() == null)
                && StringUtils.isEmpty(entity.getSynchConfigId())) {
            String reason = "managedSys policy or synchConfigId must be specified";
            throw new DataException(reason, new Exception(
                    "managedSys policy and synchConfigId are null"));
        }
        super.update(entity);
    }

    @Override
    public void delete(List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List attrMap = getCriteria()
                    .add(Restrictions.in("id", ids)).list();
            deleteAttributesMapList(attrMap);
        }
    }

    @Override
    public void deleteAttributesMapList(List<AttributeMapEntity> attrMap) {
        if (!CollectionUtils.isEmpty(attrMap)) {
            for (AttributeMapEntity ame : attrMap) {
                getSession().delete(ame);
            }
        }
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
