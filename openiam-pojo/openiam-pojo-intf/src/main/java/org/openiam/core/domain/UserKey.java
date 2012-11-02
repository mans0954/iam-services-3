package org.openiam.core.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 * Created by: Alexander Duckardt
 * Date: 05.10.12
 */
@Entity
@Table(name = "USER_KEY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserKey implements Serializable {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="USER_KEY_ID", length=32, nullable = false)
    private String userKeyId;
    @Column(name="USER_ID", length=32, nullable = false)
    private String userId;
    @Column(name="NAME", length=40, nullable = false)
    private String name;
    @Column(name="KEY_VALUE", length=255, nullable = false)
    private String key;

    @ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private UserEntity user;

    public String getUserKeyId() {
        return userKeyId;
    }

    public void setUserKeyId(String userKeyId) {
        this.userKeyId = userKeyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        UserKey userKey = (UserKey) o;

        if(key != null ? !key.equals(userKey.key) : userKey.key != null) {
            return false;
        }
        if(name != null ? !name.equals(userKey.name) : userKey.name != null) {
            return false;
        }
        if(userId != null ? !userId.equals(userKey.userId) : userKey.userId != null) {
            return false;
        }
        if(userKeyId != null ? !userKeyId.equals(userKey.userKeyId) : userKey.userKeyId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = userKeyId != null ? userKeyId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
