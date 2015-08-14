package org.openiam.hazelcast;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;

@Component
@DependsOn("sessionFactory") /* depends on hibernate.  Otherwise, two hazelcast instnaces will be created! */
public class HazelcastConfiguration {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private HazelcastInstance hzInstance;
	
	@Value("${hibernate.cache.provider_configuration_file_resource_path}")
	private String configFilename;

	@PostConstruct
	public void init() throws FileNotFoundException {
		hzInstance = Hazelcast.getAllHazelcastInstances().iterator().next();
		if(log.isInfoEnabled()) {
			log.info(String.format("Using hazelcast instance: %s", hzInstance));
		}
	}
	
	public IMap getMap(final String name) {
		return hzInstance.getMap(name);
	}
	
	public ILock getLock(final String name) {
		return hzInstance.getLock(name);
	}
}
