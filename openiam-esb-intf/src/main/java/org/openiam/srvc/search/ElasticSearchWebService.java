package org.openiam.srvc.search;

import java.util.List;

import javax.jws.WebService;

import org.openiam.base.ws.Response;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/user/service", name = "ElasticSearchWebService")
public interface ElasticSearchWebService {

	public Response reindex(final Class<?> entityClass);
	
	public List<Class<?>> indexedClasses();

	public Response scheduleReindex(final Class<?> entityClass, final String id, final Long delay);
}
