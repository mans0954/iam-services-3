package org.openiam.access.review.strategy;


import org.openiam.constants.AccessReviewConstant;
import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.model.AccessViewBean;
import org.openiam.base.TreeNode;

import java.util.Collections;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 5/27/14.
 */
public class RoleViewStrategy extends AccessReviewStrategy {
    public RoleViewStrategy(AccessReviewData accessReviewData) {
        super(accessReviewData);
    }

    @Override
    public List<TreeNode<AccessViewBean>> buildView(AccessViewBean parent) {
        try{
            List<TreeNode<AccessViewBean>> dataList = proceedSubTree(getRoleBeanList(getRoleEntitlementStrategy().getRoles(parent),
                                                                                     false, null, AccessReviewConstant.INITIAL_LEVEL),
                                                                     AccessReviewConstant.INITIAL_LEVEL);
            return applyFilter(dataList);
        } catch (Exception ex){
        	LOG.error(ex.getLocalizedMessage(), ex);
            return Collections.EMPTY_LIST;
        }
    }

    public List<TreeNode<AccessViewBean>> getExceptionsList(){
    	/*
        try{
            accessReviewData.setTargetResourceIds(accessReviewData.getMatrix().getResourceIds());
            return getResourceBeanList(getResourceEntitlementStrategy().getResources(null), false, false);
        } catch (Exception ex){
            log.error(ex.getLocalizedMessage(), ex);
            return Collections.EMPTY_LIST;
        }
        */ return null;
    }
}
