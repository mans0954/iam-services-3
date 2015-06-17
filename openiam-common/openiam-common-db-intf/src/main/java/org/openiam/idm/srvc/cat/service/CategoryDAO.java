package org.openiam.idm.srvc.cat.service;

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;

/**
 * DAO Interface for Category
 *
 * @author suneet shah
 */
public interface CategoryDAO extends BaseDao<CategoryEntity, String> {

    /**
     * Return a list of Categories where the parentId is null.
     *
     * @return
     */
    List<CategoryEntity> findRootCategories();

    /**
     * Return a list of Categories for the specified parentId.
     *
     * @param parentId
     * @return
     */
    List<CategoryEntity> findChildCategories(String parentId);

    /**
     * Removes a list of categories
     *
     * @param catIdList
     * @return
     */
    int removeGroupList(String catIdList);

}