package org.openiam.elasticsearch.dao;

import org.openiam.elasticsearch.model.LoginDoc;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoginElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<LoginDoc, LoginSearchBean, String> {

	public Page<String> findUserIds(final LoginSearchBean searchBean, final Pageable pageable);
	public int count(final LoginSearchBean searchBean);
}
