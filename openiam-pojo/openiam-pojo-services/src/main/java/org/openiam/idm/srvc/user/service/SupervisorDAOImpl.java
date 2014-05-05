package org.openiam.idm.srvc.user.service;

// Generated Feb 18, 2008 3:56:08 PM by Hibernate Tools 3.2.0.b11

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.SupervisorIDEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data Access Object implementation for domain model class Supervisor.
 * 
 * @see org.openiam.idm.srvc.user.dto.Supervisor
 */
@Repository("supervisorDAO")
public class SupervisorDAOImpl extends BaseDaoImpl<SupervisorEntity, SupervisorIDEntity> implements SupervisorDAO {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Transactional
    public void deleteById(SupervisorIDEntity id) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " s where s.id = :pk ");
        qry.setParameter("pk", id);
        qry.executeUpdate();
    }

    /**
     * Returns a list of Supervisor objects that represents the employees or
     * users for this supervisor
     * 
     * @param supervisorId
     * @return
     */
    public List<SupervisorEntity> findEmployees(String supervisorId) {
        Criteria criteria = getCriteria().createAlias("supervisor", "s").add(Restrictions.eq("s.id", supervisorId))
                        .addOrder(Order.asc("supervisor.id"));

        List<SupervisorEntity> results = (List<SupervisorEntity>) criteria.list();

        // initalize the objects in the collection

        int listSize = results.size();
        for (int i = 0; i < listSize; i++) {
            SupervisorEntity supr = results.get(i);
            org.hibernate.Hibernate.initialize(supr.getSupervisor());
            org.hibernate.Hibernate.initialize(supr.getEmployee());
        }

        return results;

    }

    public List<SupervisorEntity> findSupervisors(String employeeId) {
        Criteria criteria = getCriteria().createAlias("employee", "e").add(Restrictions.eq("e.id", employeeId));

        List<SupervisorEntity> results = (List<SupervisorEntity>) criteria.list();

        // initalize the objects in the collection

        int listSize = results.size();
        for (int i = 0; i < listSize; i++) {
            SupervisorEntity supr = results.get(i);
            org.hibernate.Hibernate.initialize(supr.getSupervisor());
            org.hibernate.Hibernate.initialize(supr.getEmployee());
        }

        return results;
    }

    public SupervisorEntity findPrimarySupervisor(String employeeId) {
        Criteria criteria = getCriteria().createAlias("employee", "e").add(Restrictions.eq("e.id", employeeId))
                        .add(Restrictions.eq("isPrimarySuper", 1)).addOrder(Order.asc("supervisor"));

        SupervisorEntity supr = (SupervisorEntity) criteria.uniqueResult();
        if (supr == null)
            return null;

        org.hibernate.Hibernate.initialize(supr.getSupervisor());
        org.hibernate.Hibernate.initialize(supr.getEmployee());

        return supr;
        // List<Supervisor> results = (List<Supervisor>)qry.list();
        // return results;
    }

    public SupervisorEntity findSupervisor(String superiorId, String subordinateId) {
        Criteria criteria = getCriteria().add(Restrictions.eq("supervisor.id", superiorId))
                        .add(Restrictions.eq("employee.id", subordinateId));

        SupervisorEntity supr = (SupervisorEntity) criteria.uniqueResult();
        if (supr == null)
            return null;

        org.hibernate.Hibernate.initialize(supr.getSupervisor());
        org.hibernate.Hibernate.initialize(supr.getEmployee());

        return supr;
    }

    // @Override
    public Set<String> getUniqueEmployeeIds() {
        final List<String> list = getCriteria().setProjection(Projections.property("employee.id")).list();
        return (list != null) ? new HashSet<String>(list) : Collections.EMPTY_SET;
    }

}
