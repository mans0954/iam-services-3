package org.openiam.base.request;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;

/**
 * Created by alexander on 08/08/16.
 */
public class BaseSearchServiceRequest<SearchBeanData extends AbstractSearchBean> extends BaseServiceRequest {
    private SearchBeanData searchBean;
    private int from;
    private int size;
    public BaseSearchServiceRequest(){}
    public BaseSearchServiceRequest(SearchBeanData searchBean){
        this(searchBean, -1, -1);
    }
    public BaseSearchServiceRequest(SearchBeanData searchBean, int from, int size) {
        this(searchBean, from, size, (String)null);
    }

    public BaseSearchServiceRequest(SearchBeanData searchBean, int from, int size, String languageId) {
        this.searchBean = searchBean;
        this.from = from;
        this.size = size;
        if(StringUtils.isNotBlank(languageId)){
            setLanguageId(languageId);
        }
    }

    public BaseSearchServiceRequest(SearchBeanData searchBean, int from, int size, Language language) {
        this.searchBean = searchBean;
        this.from = from;
        this.size = size;
        setLanguage(language);
    }

    public SearchBeanData getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(SearchBeanData searchBean) {
        this.searchBean = searchBean;
    }

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
}
