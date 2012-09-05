package org.openiam.bpm.activiti;

import javax.sql.DataSource;

import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bpm-context.xml")
public class TestInitialization {
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Test
	public void testInit() {
		System.out.println("testInit()");
	}
}
