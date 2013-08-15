package org.openiam.idm.searchbeans;

import java.util.Arrays;
import java.util.List;

import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultCase;

public class ManualReconciliationSearchBean {
    private int size;
    private int pageNumber;
    private String searchFieldName;
    private String searchFieldValue;
    private ReconciliationResultCase searchCase;
    private String orderBy;
    private String orderByFieldName;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSearchFieldName() {
        return searchFieldName;
    }

    public void setSearchFieldName(String searchFieldName) {
        this.searchFieldName = searchFieldName;
    }

    public String getSearchFieldValue() {
        return searchFieldValue;
    }

    public void setSearchFieldValue(String searchFieldValue) {
        this.searchFieldValue = searchFieldValue;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public ReconciliationResultCase getSearchCase() {
        return searchCase;
    }

    public void setSearchCase(ReconciliationResultCase searchCase) {
        this.searchCase = searchCase;
    }

    public String getOrderByFieldName() {
        return orderByFieldName;
    }

    public void setOrderByFieldName(String orderByFieldName) {
        this.orderByFieldName = orderByFieldName;
    }

}
