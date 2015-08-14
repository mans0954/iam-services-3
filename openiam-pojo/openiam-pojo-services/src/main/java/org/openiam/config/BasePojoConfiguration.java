package org.openiam.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import net.sf.ehcache.Ehcache;

@Configuration
@Import({BaseConfiguration.class, JMXConfig.class})
public class BasePojoConfiguration {
	
	@Value("${mail.host}")
	private String mailHost;
	
	@Value("${mail.port}")
	private int mailPort;
	
	@Value("${mail.username}")
	private String mailUserName;
	
	@Value("${mail.password}")
	private String mailPassword;
	
	@Value("${mail.smtp.auth}")
	private String smtpAuth;
	
	@Value("${mail.smtp.starttls.enable}")
	private String startTlsEnabled;

	@Bean(name="jmsReceiverThreadPoolTaskExecutor")
	public ThreadPoolTaskExecutor jmsReceiverThreadPoolTaskExecutor() {
		final ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
		e.setDaemon(true);
		return e;
	}
	
	@Bean(name="emailSender")
	public JavaMailSenderImpl emailSender() {
		final JavaMailSenderImpl s = new JavaMailSenderImpl();
		s.setHost(mailHost);
		s.setPort(mailPort);
		s.setUsername(mailUserName);
		s.setPassword(mailPassword);
		
		final Properties props = new Properties();
		props.put("mail.smtp.auth", smtpAuth);
		props.put("mail.smtp.starttls.enable", startTlsEnabled);
		s.setJavaMailProperties(props);
		return s;
	}
	
	@Bean(name="dto2entityDeepDozerMapper")
	public DozerBeanMapper dto2entityDeepDozerMapper() {
		return buildMapper(new String[] {"org/openiam/dozer/dto2entity/entity2dto.common.mappings.xml", "org/openiam/dozer/dto2entity/entity2dto.deep.mappings.xml"});
	}
	@Bean(name="dto2entityShallowDozerMapper")
	public DozerBeanMapper dto2entityShallowDozerMapper() {
		return buildMapper(new String[] {"org/openiam/dozer/dto2entity/entity2dto.common.mappings.xml", "org/openiam/dozer/dto2entity/entity2dto.shallow.mappings.xml"});
	}
	@Bean(name="shallowDozerMapper")
	public DozerBeanMapper shallowDozerMapper() {
		return buildMapper(new String[] {"org/openiam/dozer/dozer-common-mapping.xml", "org/openiam/dozer/dozer-shallow-mapping.xml"});
	}
	@Bean(name="deepDozerMapper")
	public DozerBeanMapper deepDozerMapper() {
		return buildMapper(new String[] {"org/openiam/dozer/dozer-common-mapping.xml", "org/openiam/dozer/dozer-deep-mapping.xml"});
	}
	
	private DozerBeanMapper buildMapper(final String[] mappingFile) {
		final List<String> mappingFiles = new ArrayList<String>(Arrays.asList(mappingFile));
		final DozerBeanMapper mapper = new DozerBeanMapper(mappingFiles);
		return mapper;
	}
	
	@Bean(name="auditCacheManager")
	public EhCacheManagerFactoryBean auditCacheManager() {
		final EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		bean.setShared(true);
		bean.setConfigLocation(new ClassPathResource("audit.ehcache.xml"));
		return bean;
	}
	
	@Bean(name="auditLogBuilderCache")
	public Ehcache auditLogBuilderCache() {
		return auditCacheManager().getObject().getCache("AuditLogBuilderCache");
	}
}