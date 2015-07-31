package org.openiam.access.review.strategy;


import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.access.review.model.AccessViewBean;
import org.openiam.base.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 5/28/14.
 */
public class ResourceViewStrategy extends AccessReviewStrategy {
    protected ResourceViewStrategy(AccessReviewData accessReviewData) {
        super(accessReviewData);
    }

    public List<TreeNode<AccessViewBean>> buildView(AccessViewBean parent){
        try{
            // get direct resources
            Set<AccessViewBean> resourceIds = this.getResourceEntitlementStrategy().getResources(parent);
            List<TreeNode<AccessViewBean>> dataList = proceedSubTree(getResourceBeanList(resourceIds, true, false), 0);
            return applyFilter(dataList);
        } catch (Exception ex){
        	LOG.error(ex.getLocalizedMessage(), ex);
            return Collections.EMPTY_LIST;
        }
    }
}
