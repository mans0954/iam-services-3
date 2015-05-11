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

public abstract class AbstractContentProviderServiceTest<T, S extends AbstractSearchBean<T, String>>  extends AbstractKeyServiceTest<T, S> {

}
