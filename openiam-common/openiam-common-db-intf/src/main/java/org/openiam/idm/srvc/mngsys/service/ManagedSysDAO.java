package org.openiam.idm.srvc.mngsys.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import java.util.List;

public interface ManagedSysDAO extends BaseDao<ManagedSysEntity, String> {

    @Deprecated
    List<ManagedSysEntity> findbyConnectorId(String connectorId);

//	 /**
//	  * Lists all managed systems belonging to a domain.
//	  * @param domainId
//	  * @return
//	  */
//	 List<ManagedSysEntity> findbyDomain(String domainId);
@Deprecated
    List<ManagedSysEntity> findAllManagedSys();

    /**
     * Returns a ManagedSys object for the specified name. The name is the value in the
     * name field in the ManagedSys object.
     *
     * @param name
     * @return
     */
    @Deprecated
    ManagedSysEntity findByName(String name);

    /**
     * Returns the managed system that is associated with the specified resource id.
     *
     * @param resourceId
     * @param status
     * @return
     */
    @Deprecated
    ManagedSysEntity findByResource(String resourceId, String status);

    /**
     * Returns the ID of managed system that is associated with the specified resource id.
     *
     * @param resourceId
     * @param status
     * @return
     */
    @Deprecated
    String findIdByResource(String resourceId, String status);
    @Deprecated
    List<ManagedSysEntity> findByResource(String resourceId);

    /**
     * Returns a ManagedSys object with only id and name
     *
     * @return
     */
    List<ManagedSysEntity> findAllManagedSysNames();


}