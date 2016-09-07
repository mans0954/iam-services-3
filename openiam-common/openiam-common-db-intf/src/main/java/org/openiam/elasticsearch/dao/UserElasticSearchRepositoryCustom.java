package org.openiam.elasticsearch.dao;

import java.util.List;

import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;

public interface UserElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<UserEntity, UserSearchBean, String> {

}
