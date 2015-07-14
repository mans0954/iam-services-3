package org.openiam.rest.request;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.rest.request.constant.RestRequestType;

/**
 * Created by zaporozhec on 7/14/15.
 */
public abstract class AbstractSearchRestRequest<SearchBean extends AbstractSearchBean> extends AbstractRestRequest {

    private SearchBean searchBean;

    private int from = -1;
    private int size = -1;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

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
