package org.openiam.elasticsearch.dao;

import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailElasticSearchRepositoryCustom extends AbstractCustomElasticSearchRepository<EmailAddressEntity, EmailSearchBean, String> {

	Page<String> findUserIds(final EmailSearchBean sb, final Pageable pageable);
}
