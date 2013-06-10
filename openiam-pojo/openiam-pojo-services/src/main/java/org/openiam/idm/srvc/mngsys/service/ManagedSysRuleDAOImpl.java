package org.openiam.idm.srvc.mngsys.service;

// Generated Nov 3, 2008 12:14:44 AM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class ManagedSys.
 * 
 * @see org.openiam.idm.srvc.mngsys.service
 * @author Hibernate Tools
 */
@Repository("managedSysRuleDAO")
public class ManagedSysRuleDAOImpl extends
        BaseDaoImpl<ManagedSysRuleEntity, String> implements ManagedSysRuleDAO {

    @Override
    protected String getPKfieldName() {
        return "managedSysRuleId";
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ManagedSysRuleEntity> findbyManagedSystemId(String managedSysId) {
        if (managedSysId == null)
            return null;
        Criteria cr = this.getCriteria().add(
                Restrictions.eq("managedSysId", managedSysId));
        try {
            return (List<ManagedSysRuleEntity>) cr.list();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
