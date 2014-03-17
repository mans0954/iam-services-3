package org.openiam.idm.srvc.cat.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.BaseObject;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "category", propOrder = {
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
@DozerDTOCorrespondence(CategoryEntity.class)
public class Category extends KeyDTO {
    private String createdBy;
    private String categoryName;
    private String categoryDesc;
    @XmlSchemaType(name = "dateTime")
    private Date createDate;
    private String parentId;
    private int showList;
    private int displayOrder;
    private Set<Category> childCategories;
    private Set<CategoryLanguage> categoryLanguages = new HashSet<CategoryLanguage>(
            0);

    public Category() {
        super();
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return this.categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getShowList() {
        return this.showList;
    }

    public void setShowList(int showList) {
        this.showList = showList;
    }

    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<CategoryLanguage> getCategoryLanguages() {
        return categoryLanguages;
    }

    public void setCategoryLanguages(Set<CategoryLanguage> categoryLanguages) {
        this.categoryLanguages = categoryLanguages;
    }

    public Set<Category> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(Set<Category> childCategories) {
        this.childCategories = childCategories;
    }

}
