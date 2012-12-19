package org.openiam.idm.srvc.cat.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.CategoryDozerConverter;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.cat.dto.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("categorydataService")
public class CategoryDataServiceImpl implements CategoryDataService {

    @Autowired
    CategoryDAO categoryDao;
    @Autowired
    CategoryLanguageDAO categoryLanguageDao;
    @Autowired
    CategoryDozerConverter categoryDozerConverter;
    private static final Log log = LogFactory
            .getLog(CategoryDataServiceImpl.class);

    @Transactional
    public void addCategory(Category cat) {
        if (cat == null) {
            throw (new NullPointerException("Category object is null"));
        }
        CategoryEntity catEntity = categoryDozerConverter.convertToEntity(cat,
                true);
        categoryDao.save(catEntity);
    }

    public List<Category> getAllCategories(boolean nested) {
        return categoryDozerConverter.convertToDTOList(
                categoryDao.findRootCategories(), nested);
    }

    public Category getCategory(String categoryId) {
        if (categoryId == null) {
            throw (new NullPointerException("CategoryId is null"));
        }
        return categoryDozerConverter.convertToDTO(
                categoryDao.findById(categoryId), false);
    }

    public List<Category> getChildCategories(String categoryId, boolean nested) {
        return categoryDozerConverter.convertToDTOList(
                categoryDao.findChildCategories(categoryId), nested);
    }

    @Transactional
    public int removeCategory(String categoryId, boolean nested) {
        if (categoryId == null) {
            throw (new NullPointerException("CategoryId is null"));
        }
        if (!nested) {
            CategoryEntity parentCat = new CategoryEntity();
            parentCat.setCategoryId(categoryId);
            categoryDao.delete(parentCat);
            return 1;
        }

        StringBuffer catIdBuf = new StringBuffer();
        List<CategoryEntity> catList = new ArrayList<CategoryEntity>();

        catList = categoryDao.findChildCategories(categoryId);

        if (catList == null || catList.isEmpty()) {
            // nothing to delete
            return 0;
        }

        int size = catList.size();
        for (int i = 0; i < size; i++) {
            CategoryEntity cat = catList.get(i);
            if (catIdBuf.length() > 0) {
                catIdBuf.append(",");
            }
            catIdBuf.append(" '" + cat.getCategoryId() + "' ");

            String catStr = getRecursiveCatId(cat.getCategoryId());
            if (catStr != null) {
                if (catIdBuf.length() > 0) {
                    catIdBuf.append(",");
                }
                catIdBuf.append(catStr);
            }
        }
        String catIdStr = catIdBuf.toString();
        int count = categoryDao.removeGroupList(catIdStr);
        Category parentCat = new Category();
        parentCat.setCategoryId(categoryId);
        categoryDao.delete(categoryDozerConverter.convertToEntity(parentCat,
                false));
        return count++;
    }

    /**
     * Recursively get that list of categories.
     * @param parentGroupId
     * @param groupList
     * @return
     */
    private String getRecursiveCatId(String parentCatId) {
        StringBuffer catIdBuf = new StringBuffer();

        List<CategoryEntity> categoryList = categoryDao
                .findChildCategories(parentCatId);
        if (categoryList == null || categoryList.isEmpty()) {
            return null;
        }
        int size = categoryList.size();
        for (int i = 0; i < size; i++) {
            CategoryEntity cat = categoryList.get(i);
            if (catIdBuf.length() > 0) {
                catIdBuf.append(",");
            }
            catIdBuf.append(" '" + cat.getCategoryId() + "' ");

            // check for child group

            String catStr = getRecursiveCatId(cat.getCategoryId());
            if (catStr != null) {
                if (catIdBuf.length() > 0) {
                    catIdBuf.append(",");
                }
                catIdBuf.append(catStr);
            }
            log.debug("Category ids after = " + catIdBuf.toString());
        }
        return catIdBuf.toString();
    }

    @Transactional
    public void updateCategory(Category cat) {
        if (cat == null) {
            throw (new NullPointerException("Category object is null"));
        }
        categoryDao.save(categoryDozerConverter.convertToEntity(cat, true));
    }
}
