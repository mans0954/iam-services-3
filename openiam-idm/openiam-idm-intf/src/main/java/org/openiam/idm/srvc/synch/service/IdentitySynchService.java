package org.openiam.idm.srvc.synch.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SyncResponse;

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

    Integer getSynchConfigCountByExample(SynchConfigEntity example);

    List<SynchConfigEntity> getSynchConfigsByExample(SynchConfigEntity example, Integer from, Integer size);

    void deleteSynchReviewList(List<SynchReviewEntity> reviewList);

    List<SynchReviewEntity> getAllSynchReviewsBySynchConfigId(String synchConfigId);

    List<AttributeMapEntity> getSynchConfigAttributeMaps(String synchConfigId);

    List<AttributeMapEntity> getSynchConfigAttributeMaps(AttributeMapSearchBean searchBean);

    public void deleteAttributesMapList(List<AttributeMapEntity> attrMap);

}