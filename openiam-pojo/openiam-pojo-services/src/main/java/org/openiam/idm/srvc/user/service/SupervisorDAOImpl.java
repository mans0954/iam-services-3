package org.openiam.idm.srvc.user.service;
// Generated Feb 18, 2008 3:56:08 PM by Hibernate Tools 3.2.0.b11


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDao;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.SupervisorIDEntity;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

/**
 * Data Access Object implementation for domain model class Supervisor.
 * @see org.openiam.idm.srvc.user.dto.Supervisor
 */
@Repository("supervisorDAO")
public class SupervisorDAOImpl extends BaseDaoImpl<SupervisorEntity, SupervisorIDEntity> implements SupervisorDAO  {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    /**
     * Returns a list of Supervisor objects that represents the employees or users for this supervisor
     * @param supervisorId
     * @return
     */
    public List<SupervisorEntity> findEmployees(String supervisorId) {
        Criteria criteria = getCriteria()
                .createAlias("supervisor","s")
                .add(Restrictions.eq("s.userId",supervisorId))
                .addOrder(Order.asc("supervisor.userId"));

    	List<SupervisorEntity> results = (List<SupervisorEntity>)criteria.list();

    	// initalize the objects in the collection
    	
    	int listSize = results.size();
    	for (int i=0; i<listSize; i++) {
    		SupervisorEntity supr = results.get(i);
    		org.hibernate.Hibernate.initialize(supr.getSupervisor());
    		org.hibernate.Hibernate.initialize(supr.getEmployee());
    	}
    	
    	return results;
    	
    }

    public List<SupervisorEntity> findSupervisors(String employeeId) {
        Criteria criteria = getCriteria()
                .createAlias("employee","e")
                .add(Restrictions.eq("e.userId",employeeId));

    	List<SupervisorEntity> results = (List<SupervisorEntity>)criteria.list();

        // initalize the objects in the collection

        int listSize = results.size();
        for (int i=0; i<listSize; i++) {
            SupervisorEntity supr = results.get(i);
            org.hibernate.Hibernate.initialize(supr.getSupervisor());
            org.hibernate.Hibernate.initialize(supr.getEmployee());
        }

        return results;
    }
    
    public SupervisorEntity findPrimarySupervisor(String employeeId) {
    	Criteria criteria = getCriteria()
                .createAlias("employee","e")
                .add(Restrictions.eq("e.userId",employeeId))
                .add(Restrictions.eq("isPrimarySuper",1))
                .addOrder(Order.asc("supervisor"));

    	SupervisorEntity supr = (SupervisorEntity)criteria.uniqueResult();
    	if (supr == null)
    		return null;

    	org.hibernate.Hibernate.initialize(supr.getSupervisor());
    	org.hibernate.Hibernate.initialize(supr.getEmployee());
   	
    	return supr;
    	//List<Supervisor> results = (List<Supervisor>)qry.list();
    	//return results;        	
    }

    public SupervisorEntity findSupervisor(String superiorId, String subordinateId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("supervisor.userId", superiorId))
                .add(Restrictions.eq("employee.userId", subordinateId));

        SupervisorEntity supr = (SupervisorEntity)criteria.uniqueResult();
        if (supr == null)
            return null;

        org.hibernate.Hibernate.initialize(supr.getSupervisor());
        org.hibernate.Hibernate.initialize(supr.getEmployee());

        return supr;
    }

//	@Override
	public Set<String> getUniqueEmployeeIds() {
		final List<String> list = getCriteria().setProjection(Projections.property("employee.userId")).list();
		return (list != null) ? new HashSet<String>(list) : Collections.EMPTY_SET;
	}

}



