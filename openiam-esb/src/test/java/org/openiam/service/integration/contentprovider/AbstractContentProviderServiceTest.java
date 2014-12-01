package org.openiam.service.integration.contentprovider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractContentProviderServiceTest<T extends KeyDTO, S extends AbstractSearchBean<T, String>>  extends AbstractKeyServiceTest<T, S> {
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	protected ContentProviderWebService contentProviderServiceClient;
	
	@Autowired
	@Qualifier("authProviderServiceClient")
	protected AuthProviderWebService authProviderServiceClient;

	protected ContentProvider createContentProvider() {
		final ContentProvider cp = new ContentProvider();
		cp.setName(getRandomName());
		cp.setAuthCookieName(getRandomName());
		cp.setDomainPattern(getRandomName());
		cp.setAuthCookieDomain(cp.getDomainPattern());
		cp.setUrl(getRandomName());
		cp.setAuthProviderId(authProviderServiceClient.findAuthProviderBeans(null, 0, 1).get(0).getId());
		
		final ContentProviderServer server = new ContentProviderServer();
		server.setServerURL(getRandomName());
		final Set<ContentProviderServer> serverSet = new HashSet<ContentProviderServer>();
		serverSet.add(server);
		cp.setServerSet(serverSet);
		
		final Set<AuthLevelGroupingContentProviderXref> groupingXrefs = new HashSet<AuthLevelGroupingContentProviderXref>();
		//for(final AuthLevelGrouping grouping : contentProviderServiceClient.getAuthLevelGroupingList()) {
		contentProviderServiceClient.getAuthLevelGroupingList().forEach((final AuthLevelGrouping grouping) -> {
			final AuthLevelGroupingContentProviderXref xref = new AuthLevelGroupingContentProviderXref();
			final AuthLevelGroupingContentProviderXrefId id = new AuthLevelGroupingContentProviderXrefId();
			id.setGroupingId(grouping.getId());
			xref.setId(id);
			groupingXrefs.add(xref);
		});
		//}
		cp.setGroupingXrefs(groupingXrefs);
		return cp;
	}
}
