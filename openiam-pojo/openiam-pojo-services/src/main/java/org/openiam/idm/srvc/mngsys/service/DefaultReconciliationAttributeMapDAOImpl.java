package org.openiam.idm.srvc.mngsys.service;

/**
 * @author zaporozhec
 */
import java.util.List;

import org.hibernate.criterion.Order;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for domain model class AttributeMap.
 */
@Repository("defaultReconciliationAttributeMapDAO")
public class DefaultReconciliationAttributeMapDAOImpl extends
        BaseDaoImpl<DefaultReconciliationAttributeMapEntity, String> implements
        DefaultReconciliationAttributeMapDAO {

    @Override
    protected String getPKfieldName() {
        return "defaultAttributeMapId";
    }

    @Override
    public List<DefaultReconciliationAttributeMapEntity> getAll() {
        return (List<DefaultReconciliationAttributeMapEntity>) this
                .getCriteria().list();
    }

}
