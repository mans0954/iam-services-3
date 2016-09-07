package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PhoneElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<PhoneEntity, PhoneSearchBean, String> {

	Page<String> findUserIds(final PhoneSearchBean sb, final Pageable pageable);
}
