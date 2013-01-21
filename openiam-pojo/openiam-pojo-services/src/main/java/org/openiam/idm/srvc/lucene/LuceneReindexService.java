package org.openiam.idm.srvc.lucene;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@ManagedResource(objectName="org.openiam.authorization.manager:name=LuceneReindexService")
public class LuceneReindexService implements InitializingBean/*, Runnable*/ {

	private Date lastReindexTimestamp = new Date();
	private Map<String, HibernateSearchDao> daoMap;
	
	private static final Logger log = Logger.getLogger(LuceneReindexService.class);
	
	/*
	private boolean forceThreadShutdown = false;
	
	@Value("${org.openiam.lucene.reindex.threadsweep}")
	private long sweepInterval;
	
	private ExecutorService service = new  ScheduledThreadPoolExecutor(1);
	*/
	
	@SuppressWarnings("rawtypes")
	public void setHibernateSearchDAOs(final Collection<HibernateSearchDao> hibernateSearchDaos) {
		daoMap = new HashMap<String, HibernateSearchDao>();
		for (final HibernateSearchDao hibernateSearchDao : hibernateSearchDaos) {
			daoMap.put(hibernateSearchDao.getSearchEntityClass().getSimpleName().toLowerCase(), hibernateSearchDao);
		}
    }
	
	//called by spring
	@ManagedOperation(description="Reindex Lucene")
	public void sweep() {
		log.info("Checking if Lucene indecies should be reindexed..");	
		boolean wasSynchronized = false;
		if(MapUtils.isNotEmpty(daoMap)) {
			for(final String entityName : daoMap.keySet()) {
				@SuppressWarnings("rawtypes")
				final HibernateSearchDao hibernateSearchDao = daoMap.get(entityName);
				if (hibernateSearchDao != null) {
					if(!hibernateSearchDao.isSynchronizing()) {
						wasSynchronized = true;
						log.warn(String.format("Attempting to reindex '%s'", entityName.toLowerCase()));
					    hibernateSearchDao.synchronizeIndexes(false);
					} else {
						log.warn(String.format("Entity '%s' is already synchronizing its indecies - skipping entity", entityName.toLowerCase()));
					}
				}
			}
		}
		
		if(wasSynchronized) {
			log.info("Successfully finished reindexing");
			lastReindexTimestamp = new Date();
		} else {
			log.info("No reindexing necessary...");
		}
	}
	
	/*
	@PreDestroy
	public void destroy() {
		forceThreadShutdown = true;
	}
	
	@Override
	public void run() {
		while(true && !forceThreadShutdown) {
			try {
				sweep();
				Thread.sleep(sweepInterval);
			} catch(Throwable e) {
				try {
					Thread.sleep(sweepInterval);
				} catch(Throwable e2) {
					
				}
				log.error("Error while executing thread", e);
			}
		}
	}
	*/

	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
		//service.submit(this);
	}
}
