package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.recon.domain.ReconciliationSituationEntity;
import org.springframework.stereotype.Repository;


/**
 * Home object for domain model class ReconciliationSituation.
 * 
 * @see org.openiam.idm.srvc.recon.service.ReconciliationSituationDAO
 * @author Hibernate Tools
 */
@Repository
public class ReconciliationSituationDAOImpl extends
        BaseDaoImpl<ReconciliationSituationEntity, String> implements
        ReconciliationSituationDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
