package org.openiam.idm.srvc.prov.request.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;

/**
 * DAO interface for the ProvisionRequest object.
 * @author suneet
 *
 */
public interface ProvisionRequestDAO extends BaseDao<ProvisionRequestEntity, String> {
}