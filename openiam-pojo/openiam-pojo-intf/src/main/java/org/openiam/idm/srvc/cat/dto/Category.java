package org.openiam.idm.srvc.cat.dto;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "category", propOrder = {
        "categoryId",
        "createdBy",
        "categoryName",
        "categoryDesc",
        "createDate",
        "parentId",
        "showList",
        "displayOrder",
        "childCategories",
        "categoryLanguages"
})
@Entity
@Table(name="CATEGORY")
public class Category implements Serializable {
    private String categoryId;
    private String createdBy;
    private String categoryName;
    private String categoryDesc;
    @XmlSchemaType(name = "dateTime")
    private Date createDate;
    private String parentId;
    private int showList;
    private int displayOrder;
    private Category[] childCategories;
    private Set<CategoryLanguage> categoryLanguages = new HashSet<CategoryLanguage>(0);
    static final long serialVersionUID = 7480627520054050204L;

    public Category() {
        super();
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="CATEGORY_ID", length=20)
    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name="CREATED_BY", length=20)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name="CATEGORY_NAME", length=40)
    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Column(name="CATEGORY_DESC", length=80)
    public String getCategoryDesc() {
        return this.categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    @Column(name="CREATE_DATE", length=19)
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name="PARENT_ID", length=20)
    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Column(name="SHOW_LIST")
    public int getShowList() {
        return this.showList;
    }

    public void setShowList(int showList) {
        this.showList = showList;
    }

    @Column(name="DISPLAY_ORDER")
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @OneToMany(mappedBy = "id.categoryId",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    //@Transient
    public Set<CategoryLanguage> getCategoryLanguages() {
        return categoryLanguages;
    }

    public void setCategoryLanguages(Set<CategoryLanguage> categoryLanguages) {
        this.categoryLanguages = categoryLanguages;
    }

    @Transient
    public Category[] getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(Category[] childCategories) {
        this.childCategories = childCategories;
    }

}
