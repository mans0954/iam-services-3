package org.openiam.am.srvc.service;

import java.util.Date;

import org.openiam.model.AccessViewFilterBean;
import org.openiam.model.AccessViewResponse;
import org.openiam.idm.srvc.lang.dto.Language;

/**
 * Created by alexander on 21.11.14.
 */
public interface AccessReviewService {
    AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType, final Date date);
    AccessViewResponse getAccessReviewSubTree(String parentId, String parentBeanType, boolean isRootOnly, AccessViewFilterBean filter, String viewType, final Date date);
}
