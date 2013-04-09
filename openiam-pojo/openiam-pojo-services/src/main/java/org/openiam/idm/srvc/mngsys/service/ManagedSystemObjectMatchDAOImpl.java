package org.openiam.idm.srvc.mngsys.service;

// Generated Dec 20, 2008 7:54:59 PM by Hibernate Tools 3.2.2.GA

import org.hibernate.Query;
import org.hibernate.Session;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Home object for domain model class MngSysObjectMatch.
 * @see org.openiam.idm.srvc.meta.service.MetadataService
 * @author Hibernate Tools
 */
@Repository("managedSysObjectMatchDAO")
public class ManagedSystemObjectMatchDAOImpl extends BaseDaoImpl<ManagedSystemObjectMatchEntity, String> implements ManagedSystemObjectMatchDAO {


    @Override
    protected String getPKfieldName() {
        return "objectSearchId";
    }

    /**
	 * Finds objects for an object type (like User, Group) for a ManagedSystem definition
	 * @param managedSystemId
	 * @param objectType
	 * @return
	 */
	public List<ManagedSystemObjectMatchEntity> findBySystemId(String managedSystemId, String objectType) {
		Session session = getSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity sys " +
						" where sys.managedSys = :managedSystemId  and sys.objectType = :objectType" );
		qry.setString("managedSystemId", managedSystemId);
		qry.setString("objectType", objectType);
		List<ManagedSystemObjectMatchEntity> result = (List<ManagedSystemObjectMatchEntity>)qry.list();
		if (result == null || result.size() == 0)
			return null;
		return result;
	}
	

}
