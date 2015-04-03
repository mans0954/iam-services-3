package org.openiam.access.review.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openiam.base.TreeNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 1/16/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessViewResponse", propOrder = {
        "error",
        "size",
        "beans",
        "exceptions",
        "isEmptySearchBean"
})
public class AccessViewResponse {
    public static final AccessViewResponse EMPTY_RESPONSE = new AccessViewResponse();
    static {
        EMPTY_RESPONSE.setEmptySearchBean(true);
    }

    private String error;
    private int size;
    private List<TreeNode<AccessViewBean>> beans;
    private List<TreeNode<AccessViewBean>> exceptions;
    private boolean isEmptySearchBean;

    public AccessViewResponse(){}

    public AccessViewResponse(final List<TreeNode<AccessViewBean>> beans, final int size) {
        this(beans, size, null);
    }
    public AccessViewResponse(final List<TreeNode<AccessViewBean>> beans, final int size, final List<TreeNode<AccessViewBean>> exceptions) {
        this.size = size;
        this.beans = beans;
        this.exceptions=exceptions;
    }

    public int getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<TreeNode<AccessViewBean>> getBeans() {
        return beans;
    }

    public void setBeans(List<TreeNode<AccessViewBean>> beans) {
        this.beans=beans;
    }

    public boolean isEmptySearchBean() {
        return isEmptySearchBean;
    }

    public void setEmptySearchBean(boolean isEmptySearchBean) {
        this.isEmptySearchBean = isEmptySearchBean;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<TreeNode<AccessViewBean>> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<TreeNode<AccessViewBean>> exceptions) {
        this.exceptions = exceptions;
    }
}
