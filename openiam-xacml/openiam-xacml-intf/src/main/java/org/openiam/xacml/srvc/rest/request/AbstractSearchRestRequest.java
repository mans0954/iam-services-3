package org.openiam.xacml.srvc.rest.request;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.xacml.srvc.rest.request.constant.RestRequestType;

/**
 * Created by zaporozhec on 7/14/15.
 */
public abstract class AbstractSearchRestRequest<SearchBean extends AbstractSearchBean> extends AbstractRestRequest {

    private SearchBean searchBean;

    public AbstractSearchRestRequest() {
        super(RestRequestType.SEARCH);
    }

    public SearchBean getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(SearchBean searchBean) {
        this.searchBean = searchBean;
    }
}
