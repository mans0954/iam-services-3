package org.openiam.idm.srvc.mngsys.service;

/**
 * @author zaporozhec
 */
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.mule.util.StringUtils;
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
                .addOrder(Order.asc("managedSystem.id")).list();
    }

    public List<AttributeMapEntity> findByManagedSysId(String managedSysId) {
        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("managedSystem.id", managedSysId))
                .addOrder(Order.asc("mapForObjectType"))
                .addOrder(Order.asc("managedSystem.id")).list();
    }

    public List<AttributeMapEntity> findBySynchConfigId(String synchConfigId) {
        return (List<AttributeMapEntity>) this.getCriteria()
                .add(Restrictions.eq("synchConfigId", synchConfigId))
                .addOrder(Order.asc("mapForObjectType"))
                .addOrder(Order.asc("synchConfigId")).list();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean, boolean isCount) {
        final Criteria criteria = getCriteria();
        if (searchBean instanceof AttributeMapSearchBean) {
            AttributeMapSearchBean amsb = (AttributeMapSearchBean)searchBean;
            if(StringUtils.isNotBlank(amsb.getResourceId())) {
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

    public void removeResourceAttributeMaps(String resourceId) {
        AttributeMapEntity ame = (AttributeMapEntity)getSession()
                .createCriteria(AttributeMapEntity.class)
                .add(Restrictions.eq("attributeMapId", resourceId)).uniqueResult();
        getSession().delete(ame);
    }

    public AttributeMapEntity add(AttributeMapEntity entity) {
        if ((entity.getManagedSystem() == null)
                && StringUtils.isEmpty(entity.getSynchConfigId())) {
            String reason = "managedSys or synchConfigId must be specified";
            throw new DataException(reason, new Exception(
                    "managedSys and synchConfigId are null"));
        }
        return super.add(entity);
    }

    public void update(AttributeMapEntity entity) {
        if ((entity.getManagedSystem() == null)
                && StringUtils.isEmpty(entity.getSynchConfigId())) {
            String reason = "managedSys or synchConfigId must be specified";
            throw new DataException(reason, new Exception(
                    "managedSys and synchConfigId are null"));
        }
        super.update(entity);
    }

    @Override
    public void delete(List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List attrMap = getSession().createCriteria(AttributeMapEntity.class)
                    .add(Restrictions.in("attributeMapId", ids)).list();
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
        return "attributeMapId";
    }

}
