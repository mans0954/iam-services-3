package org.openiam.service.integration.contentprovider;

import org.openiam.base.KeyDTO;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.service.integration.AbstractKeyServiceTest;

public abstract class AbstractContentProviderServiceTest<T extends KeyDTO, S extends AbstractSearchBean<T, String>>  extends AbstractKeyServiceTest<T, S> {

}
