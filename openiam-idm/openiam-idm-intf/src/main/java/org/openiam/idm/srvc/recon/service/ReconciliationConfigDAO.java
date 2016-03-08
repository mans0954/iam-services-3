package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.*;

import java.util.List;

/**
 * Home object for domain model class ReconiliationConfig.
 * 
 * @author Hibernate Tools
 */
public interface ReconciliationConfigDAO extends
        BaseDao<ReconciliationConfigEntity, String> {

    ReconciliationConfigEntity findByResourceIdByType(
            java.lang.String resourceId, String type);

    ReconciliationConfigEntity get(String id);

}
