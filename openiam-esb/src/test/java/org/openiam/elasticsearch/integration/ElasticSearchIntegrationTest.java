package org.openiam.elasticsearch.integration;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.srvc.search.ElasticSearchWebService;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ElasticSearchIntegrationTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("elasticSearchServiceClient")
	private ElasticSearchWebService elasticSearchServiceClient;
	
	@Test
	public void testReindex() {
		elasticSearchServiceClient.indexedClasses().forEach(clazz -> {
			final Response wsResponse = elasticSearchServiceClient.reindex(clazz);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		});
	}

//	@Test
	public void testScheduleReindex() {
		elasticSearchServiceClient.indexedClasses().forEach(clazz -> {
			final Response wsResponse = elasticSearchServiceClient.scheduleReindex(clazz, null, 10000l);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		});
	}
	
	@Test
	public void testNegativeDelay() {
		final Response wsResponse = elasticSearchServiceClient.scheduleReindex(OrganizationEntity.class, "100", -1L);
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());
	}
}
