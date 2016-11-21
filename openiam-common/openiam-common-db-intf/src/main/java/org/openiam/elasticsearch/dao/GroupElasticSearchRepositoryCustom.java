package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.GroupDoc;
import org.openiam.idm.searchbeans.GroupSearchBean;

public interface GroupElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<GroupDoc, GroupSearchBean, String> {

}
