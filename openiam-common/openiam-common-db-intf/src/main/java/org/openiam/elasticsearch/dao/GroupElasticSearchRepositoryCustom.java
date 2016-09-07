package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

public interface GroupElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<GroupEntity, GroupSearchBean, String> {

}
