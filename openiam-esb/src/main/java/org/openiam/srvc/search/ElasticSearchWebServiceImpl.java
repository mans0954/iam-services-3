package org.openiam.srvc.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.response.list.ClassListResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
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
	}

	@Override
	public List<Class<?>> indexedClasses() {
		return this.getValueList(EsAPI.IndexedClasses, new EmptyServiceRequest(), ClassListResponse.class);
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
