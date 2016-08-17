package org.openiam.idm.srvc.synch.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfigSearchBean;
import org.openiam.base.response.SynchConfigResponse;

import java.util.*;

/**
 * Interface for <code>IdentitySynchService</code>. All synchronization
 * activities are managed through this service.
 */
public interface IdentitySynchService {

	List<SynchConfigEntity> getAllConfig();

    SynchConfigEntity findById(java.lang.String id);

    SynchConfigEntity addConfig(SynchConfigEntity synchConfig);

    SynchConfigEntity mergeConfig(SynchConfigEntity synchConfig);

	void removeConfig(String configId);

    /**
     * Starts the synchronization process from a source.
     * @param config
     * @return
     */
	SyncResponse startSynchronization(SynchConfigEntity config);

    /**
     * Starts the custom synchronization process from a custom adapter.
     * @param config
     * @return
     */
    SyncResponse startCustomSynchronization(SynchConfigEntity config, String additionalValues);

    /**
     * Starts the synchronization process from a synch review object.
     * @param synchReview
     * @return
     */
    SyncResponse startSynchReview(SynchReviewEntity synchReview);

    /**
     * Tests if the connectivity information for our source system is correct.
     * @param config
     * @return
     */
    Response testConnection(SynchConfigEntity config);

    /**
     * Moves a set of users from resource or role. Users are selected based on some search criteria defined in the
     * config object.
     * @param config
     * @return
     */
    Response bulkUserMigration(BulkMigrationConfig config);

    /**
     * When resources associated with a role have been modified, the role membership needs to be resynchronized
     * @param roleId
     * @return
     */
    Response resynchRole(final String roleId);

    int count(SynchConfigSearchBean searchBean);

    List<SynchConfigEntity> findBeans(SynchConfigSearchBean searchBean, Integer from, Integer size);

    void deleteSynchReviewList(List<SynchReviewEntity> reviewList);

    List<SynchReviewEntity> getAllSynchReviewsBySynchConfigId(String synchConfigId);

    List<AttributeMapEntity> getSynchConfigAttributeMaps(String synchConfigId);

    List<AttributeMapEntity> getSynchConfigAttributeMaps(AttributeMapSearchBean searchBean);

    void deleteAttributesMapList(List<AttributeMapEntity> attrMap);


    public List<AttributeMap> getSynchConfigAttributeMapsDTO(String synchConfigId);
    public SynchConfigResponse findDTOById(String id);
    public Response testConnection(SynchConfig config);
    public SyncResponse startSynchronization(SynchConfig config);
    public SyncResponse startCustomSynchronization(SynchConfig config, String additionalValues);
}