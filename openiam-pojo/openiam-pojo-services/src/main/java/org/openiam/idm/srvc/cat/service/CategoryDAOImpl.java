package org.openiam.idm.srvc.cat.service;

// Generated Nov 22, 2008 1:32:51 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for Category
 * @author Suneet Shah
 */
@Repository("categoryDAO")
public class CategoryDAOImpl extends BaseDaoImpl<CategoryEntity, String>
        implements CategoryDAO {

    /**
     * Return a list of Categories where the parentId is null.
     * @return
     */
    public List<CategoryEntity> findRootCategories() {
        Criteria criteria = getCriteria().add(Restrictions.isNull("parentId"))
                .addOrder(Order.asc("categoryId"));
        // Session session = sessionFactory.getCurrentSession();
        // Query qry = session
        // .createQuery("from org.openiam.idm.srvc.cat.domain.CategoryEntry cat "
        // +
        // " where cat.parentId is null order by cat.categoryId asc");
        // List<CategoryEntity> results = (List<CategoryEntity>) qry.list();
        return (List<CategoryEntity>) criteria.list();
    }

    /**
     * Return a list of Categories for the specified parentId.
     * @param parentId
     * @return
     */
    public List<CategoryEntity> findChildCategories(String parentId) {
        Criteria criteria = getCriteria().add(
                Restrictions.eq("parentId", parentId)).addOrder(
                Order.asc("categoryId"));
        return (List<CategoryEntity>) criteria.list();
    }

    /**
     * Removes a list of categories
     * @param catIdList
     * @return
     */
    public int removeGroupList(String catIdList) {
        Session session = getSession();
        Query qry = session.createQuery("delete " + domainClass.getName()
                + " as cat where cat.categoryId in (" + catIdList + ")");
        return qry.executeUpdate();
    }

    @Override
    protected String getPKfieldName() {
        return "categoryId";
    }

}
