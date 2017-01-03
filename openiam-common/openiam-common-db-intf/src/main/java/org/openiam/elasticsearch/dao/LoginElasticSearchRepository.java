package org.openiam.elasticsearch.dao;

import java.util.List;

import org.openiam.elasticsearch.model.LoginDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginElasticSearchRepository extends OpeniamElasticSearchRepository<LoginDoc, String>, LoginElasticSearchRepositoryCustom {

	@Override
	public default Class<LoginDoc> getDocumentClass() {
		return LoginDoc.class;
	}
	
	public List<LoginDoc> findByUserId(String userId);
	
	public LoginDoc findFirstByUserIdAndManagedSysId(String userId, String managedSysId);
}
