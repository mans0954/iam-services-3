package org.openiam.service.integration;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.base.KeyNameDTO;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public abstract class AbstractKeyNameServiceTest<T extends KeyNameDTO, S extends AbstractSearchBean<T, String>> extends AbstractTestNGSpringContextTests {

	protected abstract T newInstance();
	
	protected T createBean() {
		final T bean = newInstance();
		bean.setName(getRandomName());
		return bean;
	}
	
	protected String getRandomName() {
		return RandomStringUtils.randomAlphanumeric(5);
	}
}
