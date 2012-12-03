package org.openiam.idm.srvc.meta.service;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.cat.service.CategoryDAO;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

// import org.apache.log4j.Category;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends
        BaseDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {

    @Autowired
    private CategoryDAO categoryDao;

    @Override
    public void addCategoryToType(String typeId, String categoryId) {
        CategoryEntity cat = categoryDao.findById(categoryId);
        if (cat == null)
            return;
        MetadataTypeEntity type = findById(typeId);
        Set<CategoryEntity> categorySet = type.getCategories();
        categorySet.add(cat);

        try {
            sessionFactory.getCurrentSession().save(type);
            log.debug("persist type successful");
        } catch (RuntimeException re) {
            re.printStackTrace();
            log.error("persist failed", re);
            throw re;
        }
    }

    @Override
    public void removeCategoryFromType(String typeId, String categoryId) {

        MetadataTypeEntity type = findById(typeId);
        org.hibernate.Hibernate.initialize(type.getCategories());
        Set<CategoryEntity> categorySet = type.getCategories();
        if (categorySet == null || categorySet.isEmpty()) {
            return;
        }
        Iterator<CategoryEntity> it = categorySet.iterator();
        while (it.hasNext()) {
            CategoryEntity cat = it.next();
            if (cat.getCategoryId().equalsIgnoreCase(categoryId)) {
                it.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MetadataTypeEntity> findTypesInCategory(String categoryId) {
    	final Criteria criteria = getCriteria().createAlias("categories", "category").add(Restrictions.eq("category.categoryId", categoryId));
        return criteria.list();
    }

    @Override
    protected String getPKfieldName() {
        return "metadataTypeId";
    }

}
