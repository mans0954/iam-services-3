package org.openiam.base.ws;

import org.openiam.base.OrderConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by: Alexander Duckardt
 * Date: 7/31/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SortParam", propOrder = {
        "orderBy",
        "sortBy"
})
public class SortParam {
    private OrderConstants orderBy = OrderConstants.ASC;
    private String sortBy;

    public SortParam() {
    }

    public SortParam(String sortBy) {
        this(OrderConstants.ASC, sortBy);
    }

    public SortParam(OrderConstants orderBy, String sortBy) {
        this.orderBy = orderBy;
        this.sortBy = sortBy;
    }

    public OrderConstants getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderConstants orderBy) {
        this.orderBy = orderBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
