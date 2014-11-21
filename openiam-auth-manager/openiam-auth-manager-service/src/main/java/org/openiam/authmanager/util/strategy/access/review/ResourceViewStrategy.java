package org.openiam.authmanager.util.strategy.access.review;


import org.openiam.authmanager.model.AccessViewBean;
import org.openiam.authmanager.util.strategy.helper.AccessReviewData;
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

    @Override
    public List<TreeNode<AccessViewBean>> buildView() {
        try{
            // get direct resources
            Set<AccessViewBean> resourceIds = this.getResourceEntitlementStrategy().getResources(null);
            List<TreeNode<AccessViewBean>> dataList = proceedSubTree(getResourceBeanList(resourceIds, true, false), 0);
            return applyFilter(dataList);
        } catch (Exception ex){
            log.error(ex.getLocalizedMessage(), ex);
            return Collections.EMPTY_LIST;
        }
    }
}
