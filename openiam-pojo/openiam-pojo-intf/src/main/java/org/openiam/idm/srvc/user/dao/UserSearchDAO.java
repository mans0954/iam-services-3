package org.openiam.idm.srvc.user.dao;

import org.apache.lucene.search.Query;
import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;

public interface UserSearchDAO extends
        HibernateSearchDao<UserEntity, UserSearchBean, String> {

    Query parse(UserSearchBean query);

    Class<UserEntity> getEntityClass();
}