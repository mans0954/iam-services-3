package org.openiam.redis;

import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.runner.RunWith;
import org.openiam.config.UnitTestConfig;
import org.openiam.idm.srvc.msg.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.openiam.idm.srvc.msg.service.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({UnitTestConfig.class})
@Transactional
@Rollback(true)
public class TestRedisTaskScheduler extends AbstractTransactionalTestNGSpringContextTests {

	@Autowired
	private MailSender sender;
	
	@Test
	public void testMailTaskScheduler() throws InterruptedException {
		final Message mail = new Message();
		mail.setBody(RandomStringUtils.randomAlphanumeric(10));
		//mail.setExecutionDateTime(DateUtils.addSeconds(new Date(), 10));
		mail.setSubject(RandomStringUtils.randomAlphanumeric(10));
		mail.addTo("sdfds@dsfdsf.com");
		sender.send(mail);
		Thread.sleep(5000L);
	}
}
