package org.openiam.idm.srvc.audit.service;

// Generated Nov 30, 2007 3:01:47 AM by Hibernate Tools 3.2.0.b11

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.springframework.stereotype.Repository;

/**
 * RDMBS implementation the DAO for IdmAudit
 * @see org.openiam.idm.srvc.audit.dto.IdmAuditLog
 * @author Suneet Shah 
 */
@Repository("idmAuditLogCustomDAO")
public class IdmAuditLogCustomDAOImpl extends
        BaseDaoImpl<IdmAuditLogCustomEntity, String> implements
        IdmAuditLogCustomDAO {

    @Override
    public List<IdmAuditLogCustomEntity> getByIdmAuditLogId(String logId) {
        Criteria criteria = this.getCriteria().add(
                Restrictions.eq("logId", logId));
        return (List<IdmAuditLogCustomEntity>) criteria.list();
    }

    @Override
    protected String getPKfieldName() {
        return "customIdmAuditLogId";
    }

}
