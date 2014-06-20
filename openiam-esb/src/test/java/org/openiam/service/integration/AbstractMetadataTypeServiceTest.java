package org.openiam.service.integration;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.base.AdminResourceDTO;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

public abstract class AbstractMetadataTypeServiceTest<T extends AdminResourceDTO, S extends AbstractSearchBean<T, String>> extends AbstractKeyNameServiceTest<T, S> {

}
