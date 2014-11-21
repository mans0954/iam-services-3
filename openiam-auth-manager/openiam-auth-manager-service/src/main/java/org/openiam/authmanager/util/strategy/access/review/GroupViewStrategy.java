package org.openiam.authmanager.util.strategy.access.review;


import org.openiam.authmanager.model.AccessViewBean;
import org.openiam.authmanager.util.strategy.helper.AccessReviewData;
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
    public List<TreeNode<AccessViewBean>> buildView() {
        return Collections.EMPTY_LIST;
    }
}
