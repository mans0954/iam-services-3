package org.openiam.elasticsearch.dao;

import java.util.List;
import java.util.Set;

import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoginElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<LoginEntity, LoginSearchBean, String> {

	public Page<String> findUserIds(final LoginSearchBean searchBean, final Pageable pageable);
	public int count(final LoginSearchBean searchBean);
	public List<String> findIds(final LoginSearchBean searchBean, final Pageable pageable);
}
