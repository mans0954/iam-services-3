package org.openiam.srvc.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.response.ClassListResponse;
import org.openiam.base.response.IntResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexProcessor;
import org.openiam.mq.constants.api.EsAPI;
import org.openiam.mq.constants.queue.common.EsReindexQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@Service("elasticSearchWS")
@WebService(endpointInterface = "org.openiam.srvc.search.ElasticSearchWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/user/service",
			serviceName = "ElasticSearchWebService",
			portName = "ElasticSearchWebServicePort")
public class ElasticSearchWebServiceImpl extends AbstractApiService implements ElasticSearchWebService {
	
	private final Log logger = LogFactory.getLog(this.getClass());



	@Autowired
	public ElasticSearchWebServiceImpl(EsReindexQueue queue) {
		super(queue);
	}

	@Override
	@WebMethod
	public Response reindex(Class<?> entityClass) {
		ElasticsearchReindexRequest reindexRequest = ElasticsearchReindexRequest.getUpdateReindexRequest(entityClass);
		this.getValue(EsAPI.Reindex, reindexRequest, IntResponse.class);
		return new Response(ResponseStatus.SUCCESS);
//		final Response response = new Response(ResponseStatus.SUCCESS);
//		final IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
//		auditLog.setAction("reindex");
//		auditLog.setTargetClass(entityClass);
//		try {
//			final int numOfReindexedEntities = reindexer.reindex(entityClass);
//			auditLog.put("numOfReindexedEntities", Integer.valueOf(numOfReindexedEntities).toString());
//			auditLog.succeed();
//			response.succeed();
//		} catch(Throwable e) {
//			auditLog.fail();
//			response.fail();
//			response.setErrorText(ExceptionUtils.getFullStackTrace(e));
//			logger.error(String.format("Can't reindex %s", entityClass), e);
//		} finally {
//			auditLogHelper.enqueue(auditLog);
//		}
//		return response;
	}

	@Override
	public List<Class<?>> indexedClasses() {
		return this.getValueList(EsAPI.IndexedClasses, new EmptyServiceRequest(), ClassListResponse.class);
//		return reindexer.getIndexedClasses();
	}


	@Override
	public Response scheduleReindex(final Class<?> entityClass, final String id, final Long delay){
		ElasticsearchReindexRequest reindexRequest = ElasticsearchReindexRequest.getUpdateReindexRequest(entityClass);
		if(StringUtils.isNotBlank(id)){
			reindexRequest.addEntityId(id);
		}
		rabbitMQSender.schedule(this.getRabbitMqQueue(), EsAPI.Reindex, delay, reindexRequest);
		return new Response(ResponseStatus.SUCCESS);
	}
}
