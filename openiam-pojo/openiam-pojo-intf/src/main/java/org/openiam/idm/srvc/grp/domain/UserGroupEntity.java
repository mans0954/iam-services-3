package org.openiam.idm.srvc.grp.domain;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.UserGroup;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by: Alexander Duckardt
 * Date: 17.11.12
 */
@Entity
@Table(name="USER_GRP")
@DozerDTOCorrespondence(UserGroup.class)
@Embeddable
public class UserGroupEntity implements Serializable {
    private static final long serialVersionUID = 5686110876382504665L;
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="USER_GRP_ID", length=32)
    protected String userGrpId;
    
    @Column(name="GRP_ID",length=32,nullable=false)
    @Field(name = "groupId", index = Index.UN_TOKENIZED/*, store = Store.YES*/)
    protected String grpId;
    
    @Column(name="USER_ID",length=32,nullable=false)
    protected String userId;
    
    @Column(name="STATUS",length=20,nullable=false)
    protected String status;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.DATE)
    protected Date createDate;
    
    @Column(name="CREATED_BY",length=20)
    protected String createdBy;

    @ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private UserEntity user;

    public UserGroupEntity() {
    }

    public UserGroupEntity(String grpId, String userId) {
        this.grpId = grpId;
        this.userId = userId;
        status = "ACTIVE";
        createDate = new Date(System.currentTimeMillis());
    }


    public String getUserGrpId() {
        return userGrpId;
    }

    public void setUserGrpId(String userGrpId) {
        this.userGrpId = userGrpId;
    }

    public String getGrpId() {
        return grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
