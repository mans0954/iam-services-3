package org.openiam.idm.srvc.cat.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.dto.Category;

@Entity
@Table(name="CATEGORY")
@DozerDTOCorrespondence(Category.class)
public class CategoryEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "CATEGORY_ID", length = 20)
    private String categoryId;

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @Column(name = "CATEGORY_NAME", length = 40)
    private String categoryName;

    @Column(name = "CATEGORY_DESC", length = 80)
    private String categoryDesc;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "PARENT_ID", length = 20)
    private String parentId;

    @Column(name = "SHOW_LIST")
    private int showList;

    @Column(name = "DISPLAY_ORDER")
    private int displayOrder;

    @OneToMany(mappedBy = "categoryId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Set<CategoryEntity> childCategories;

    @OneToMany(mappedBy = "id.categoryId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Set<CategoryLanguageEntity> categoryLanguages = new HashSet<CategoryLanguageEntity>(0);

    static final long serialVersionUID = 7480627520054050204L;

    public CategoryEntity() {
        super();
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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


    public Set<CategoryLanguageEntity> getCategoryLanguages() {
        return categoryLanguages;
    }

    /**
     * @return the childCategories
     */
    public Set<CategoryEntity> getChildCategories() {
        return childCategories;
    }

    /**
     * @param childCategories the childCategories to set
     */
    public void setChildCategories(Set<CategoryEntity> childCategories) {
        this.childCategories = childCategories;
    }

    public void setCategoryLanguages(Set<CategoryLanguageEntity> categoryLanguages) {
        this.categoryLanguages = categoryLanguages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((categoryDesc == null) ? 0 : categoryDesc.hashCode());
        result = prime * result
                + ((categoryId == null) ? 0 : categoryId.hashCode());
        result = prime
                * result
                + ((categoryLanguages == null) ? 0 : categoryLanguages
                        .hashCode());
        result = prime * result
                + ((categoryName == null) ? 0 : categoryName.hashCode());
        result = prime * result
                + ((childCategories == null) ? 0 : childCategories.hashCode());
        result = prime * result
                + ((createDate == null) ? 0 : createDate.hashCode());
        result = prime * result
                + ((createdBy == null) ? 0 : createdBy.hashCode());
        result = prime * result + displayOrder;
        result = prime * result
                + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + showList;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryEntity other = (CategoryEntity) obj;
        if (categoryDesc == null) {
            if (other.categoryDesc != null)
                return false;
        } else if (!categoryDesc.equals(other.categoryDesc))
            return false;
        if (categoryId == null) {
            if (other.categoryId != null)
                return false;
        } else if (!categoryId.equals(other.categoryId))
            return false;
        if (categoryLanguages == null) {
            if (other.categoryLanguages != null)
                return false;
        } else if (!categoryLanguages.equals(other.categoryLanguages))
            return false;
        if (categoryName == null) {
            if (other.categoryName != null)
                return false;
        } else if (!categoryName.equals(other.categoryName))
            return false;
        if (childCategories == null) {
            if (other.childCategories != null)
                return false;
        } else if (!childCategories.equals(other.childCategories))
            return false;
        if (createDate == null) {
            if (other.createDate != null)
                return false;
        } else if (!createDate.equals(other.createDate))
            return false;
        if (createdBy == null) {
            if (other.createdBy != null)
                return false;
        } else if (!createdBy.equals(other.createdBy))
            return false;
        if (displayOrder != other.displayOrder)
            return false;
        if (parentId == null) {
            if (other.parentId != null)
                return false;
        } else if (!parentId.equals(other.parentId))
            return false;
        if (showList != other.showList)
            return false;
        return true;
    }

}
