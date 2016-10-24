package org.openiam.srvc.search;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.elasticsearch.service.ElasticsearchReindexProcessor;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Set;

@Service("elasticSearchWS")
@WebService(endpointInterface = "org.openiam.srvc.search.ElasticSearchWebService",
			targetNamespace = "urn:idm.openiam.org/srvc/user/service",
			serviceName = "ElasticSearchWebService",
			portName = "ElasticSearchWebServicePort")
public class ElasticSearchWebServiceImpl extends AbstractBaseService implements ElasticSearchWebService {
	
	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ElasticsearchReindexProcessor reindexer;
	
	@Override
	@WebMethod
	public Response reindex(Class<?> entityClass) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		final IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
		auditLog.setAction("reindex");
		auditLog.setTargetClass(entityClass);
		try {
			final int numOfReindexedEntities = reindexer.reindex(entityClass);
			auditLog.put("numOfReindexedEntities", Integer.valueOf(numOfReindexedEntities).toString());
			auditLog.succeed();
			response.succeed();
		} catch(Throwable e) {
			auditLog.fail();
			response.fail();
			response.setErrorText(ExceptionUtils.getFullStackTrace(e));
			logger.error(String.format("Can't reindex %s", entityClass), e);
		} finally {
			auditLogHelper.enqueue(auditLog);
		}
		return response;
	}

	@Override
	public Set<Class<?>> indexedClasses() {
		return reindexer.getIndexedClasses();
	}

}
