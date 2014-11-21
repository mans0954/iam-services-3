package org.openiam.authmanager.util.strategy.access.review;


import org.openiam.authmanager.model.AccessViewBean;
import org.openiam.authmanager.util.strategy.helper.AccessReviewConstant;
import org.openiam.authmanager.util.strategy.helper.AccessReviewData;
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
    public List<TreeNode<AccessViewBean>> buildView() {
        try{
            List<TreeNode<AccessViewBean>> dataList = proceedSubTree(getRoleBeanList(getRoleEntitlementStrategy().getRoles(null),
                                                                                     false, null, AccessReviewConstant.INITIAL_LEVEL),
                                                                     AccessReviewConstant.INITIAL_LEVEL);
            return applyFilter(dataList);
        } catch (Exception ex){
            log.error(ex.getLocalizedMessage(), ex);
            return Collections.EMPTY_LIST;
        }
    }

    public List<TreeNode<AccessViewBean>> getExceptionsList(){
        try{
            accessReviewData.setTargetResourceIds(accessReviewData.getMatrix().getResourceIds());
            return getResourceBeanList(getResourceEntitlementStrategy().getResources(null), false, false);
        } catch (Exception ex){
            log.error(ex.getLocalizedMessage(), ex);
            return Collections.EMPTY_LIST;
        }
    }
}
