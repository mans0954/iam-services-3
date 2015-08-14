package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.SupervisorIDEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;

import java.util.List;
import java.util.Set;

public interface SupervisorDAO extends BaseDao<SupervisorEntity, SupervisorIDEntity>{

    /**
     * Returns a list of Supervisor objects that represents the employees or users for this supervisor
     *
     * @param supervisorId
     * @return
     */
//    public List<SupervisorEntity> findEmployees(String supervisorId);

    /**
     * Returns a List of supervisor objects that represents the supervisors for this employee or user.
     *
     * @param employeeId
     * @return
     */
//    public List<SupervisorEntity> findSupervisors(String employeeId);

    /**
     * Returns the primary supervisor for this employee. Null if no primary is defined.
     *
     * @param employeeId
     * @return
     */
//    public SupervisorEntity findPrimarySupervisor(String employeeId);
//
//    public SupervisorEntity findSupervisor(String superiorId, String subordinateId);
//
    Set<String> getUniqueEmployeeIds();

    void deleteById(SupervisorIDEntity id);
}