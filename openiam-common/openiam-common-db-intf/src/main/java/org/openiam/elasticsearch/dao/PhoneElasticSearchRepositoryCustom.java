package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PhoneElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<PhoneSearchBean, String> {

	Page<String> findUserIds(final PhoneSearchBean sb, final Pageable pageable);
}
