package org.openiam.idm.srvc.membership;

import org.openiam.elasticsearch.dao.OpeniamElasticSearchRepository;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MembershipElasticSearchRepository<T extends AbstractMembershipXrefEntity> 
		extends OpeniamElasticSearchRepository<T, String> {

}
