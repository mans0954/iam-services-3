package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.recon.domain.ReconciliationSituationEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class ReconciliationSituation.
 * 
 * @see org.openiam.idm.srvc.pswd.service.ReconciliationSituation
 * @author Hibernate Tools
 */
@Repository("reconSituationDAO")
public class ReconciliationSituationDAOImpl extends
        BaseDaoImpl<ReconciliationSituationEntity, String> implements
        ReconciliationSituationDAO {

    @Override
    protected String getPKfieldName() {
        return "reconSituationId";
    }

}
