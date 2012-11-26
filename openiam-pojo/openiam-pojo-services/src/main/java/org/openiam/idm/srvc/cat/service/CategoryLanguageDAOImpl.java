package org.openiam.idm.srvc.cat.service;

// Generated Nov 22, 2008 1:32:51 PM by Hibernate Tools 3.2.2.GA

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.cat.domain.CategoryLanguageEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Object for CategoryLanguage
 * @author Suneet Shah
 */
@Repository("categoryLanguageDAO")
public class CategoryLanguageDAOImpl extends
        BaseDaoImpl<CategoryLanguageEntity, String> implements
        CategoryLanguageDAO {


    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
