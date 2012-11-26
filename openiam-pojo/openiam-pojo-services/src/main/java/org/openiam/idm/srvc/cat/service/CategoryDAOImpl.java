package org.openiam.idm.srvc.cat.service;

// Generated Nov 22, 2008 1:32:51 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for Category
 * @author Suneet Shah
 */
@Repository("categoryDAO")
public class CategoryDAOImpl extends BaseDaoImpl<CategoryEntity, String>
        implements
        CategoryDAO {
	
	/**
	 * Return a list of Categories where the parentId is null.
	 * @return
	 */
    public List<CategoryEntity> findRootCategories() {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.cat.dto.Category cat " + 
				" where cat.parentId is null order by cat.categoryId asc");
        List<CategoryEntity> results = (List<CategoryEntity>) qry.list();
		return results;		
	}
	

	
	/**
	 * Return a list of Categories for the specified parentId.
	 * @param parentId
	 * @return
	 */
    public List<CategoryEntity> findChildCategories(String parentId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.cat.dto.Category cat " + 
				" where cat.parentId = :parentId order by cat.categoryId asc");
		qry.setString("parentId", parentId);
        List<CategoryEntity> results = (List<CategoryEntity>) qry.list();
		return results;
		
	}
	
	/**
	 * Removes a list of categories
	 * @param catIdList
	 * @return
	 */
	public int removeGroupList(String catIdList) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("delete org.openiam.idm.srvc.cat.dto.Category cat  " + 
					" where cat.categoryId in (" + catIdList + ")" );
		return qry.executeUpdate();		
    }

    @Override
    protected String getPKfieldName() {
        return "categoryId";
    }
	
}
