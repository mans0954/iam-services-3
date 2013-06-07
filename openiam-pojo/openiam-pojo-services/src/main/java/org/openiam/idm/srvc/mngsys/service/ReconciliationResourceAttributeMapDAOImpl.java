package org.openiam.idm.srvc.mngsys.service;

/**
 * @author zaporozhec
 */
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for domain model class AttributeMap.
 */
@Repository("reconciliationResourceAttributeMapDAO")
public class ReconciliationResourceAttributeMapDAOImpl extends
        BaseDaoImpl<ReconciliationResourceAttributeMapEntity, String> implements
        ReconciliationResourceAttributeMapDAO {

    @Override
    protected String getPKfieldName() {
        return "reconciliationResourceAttributeMapId";
    }

}
