package org.openiam.access.review.strategy;


import org.openiam.access.review.constant.AccessReviewData;
import org.openiam.model.AccessViewBean;
import org.openiam.base.TreeNode;

import java.util.Collections;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 6/4/14.
 */
public class GroupViewStrategy extends AccessReviewStrategy  {
    protected GroupViewStrategy(AccessReviewData accessReviewData) {
        super(accessReviewData);
    }

    @Override
    public List<TreeNode<AccessViewBean>> buildView(AccessViewBean parent) {
        return Collections.EMPTY_LIST;
    }
}
