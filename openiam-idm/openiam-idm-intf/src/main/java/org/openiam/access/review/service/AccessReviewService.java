package org.openiam.access.review.service;

import java.util.Date;

import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.access.review.model.AccessViewResponse;
import org.openiam.base.TreeNode;
import org.openiam.idm.srvc.lang.dto.Language;

/**
 * Created by alexander on 21.11.14.
 */
public interface AccessReviewService {
    AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType, final Date date, final Language language);
    AccessViewResponse getAccessReviewSubTree(String parentId, String parentBeanType, boolean isRootOnly, AccessViewFilterBean filter, String viewType, final Date date, final Language language);
}
