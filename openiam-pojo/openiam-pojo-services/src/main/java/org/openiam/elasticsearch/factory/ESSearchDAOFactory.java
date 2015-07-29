package org.openiam.elasticsearch.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 9/22/14.
 */
@Component
public class ESSearchDAOFactory implements ApplicationContextAware {
	
	private static final Log logger = LogFactory.getLog(ESSearchDAOFactory.class);
	
	private Map<String, HibernateSearchDao> searchDAOMap = new HashMap<String, HibernateSearchDao>();
	
    public HibernateSearchDao getSearchDAO(String className){
    	final HibernateSearchDao dao = searchDAOMap.get(className);
    	if(dao == null) {
    		throw new IllegalStateException(String.format("No search bean representing '%s'", className));
    	}
    	return dao;
    }


	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		arg0.getBeansOfType(HibernateSearchDao.class).forEach((k, dao) ->  {
			logger.info(String.format("Adding %s:%s", dao.getSearchEntityClass(), dao));
			searchDAOMap.put(dao.getSearchEntityClass().getName(), dao);
		});
	}
}
