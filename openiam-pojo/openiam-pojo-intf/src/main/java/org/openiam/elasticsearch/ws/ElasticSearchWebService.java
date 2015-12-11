package org.openiam.elasticsearch.ws;

import java.util.Set;

import javax.jws.WebService;

import org.openiam.base.ws.Response;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/user/service", name = "ElasticSearchWebService")
public interface ElasticSearchWebService {

	public Response reindex(final Class<?> entityClass);
	
	public Set<Class<?>> indexedClasses();
}
