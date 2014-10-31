package org.openiam.elasticsearch.factory;

import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 9/22/14.
 */
@Component
public class ESSearchDAOFactory {
    @Autowired
    @Qualifier("userSearchDAO")
    private HibernateSearchDao userSearchDAO;
    @Autowired
    @Qualifier("loginSearchDAO")
    private HibernateSearchDao loginSearchDAO;
    @Autowired
    @Qualifier("emailSearchDAO")
    private HibernateSearchDao emailSearchDAO;
    @Autowired
    @Qualifier("phoneSearchDAO")
    private HibernateSearchDao phoneSearchDAO;


    public HibernateSearchDao getSearchDAO(String className){
        if(userSearchDAO.getSearchEntityClass().getName().equals(className)){
            return userSearchDAO;
        } else if(loginSearchDAO.getSearchEntityClass().getName().equals(className)){
            return loginSearchDAO;
        }  else if(emailSearchDAO.getSearchEntityClass().getName().equals(className)){
            return emailSearchDAO;
        } else if(phoneSearchDAO.getSearchEntityClass().getName().equals(className)){
            return phoneSearchDAO;
        }
        return null;
    }
}
