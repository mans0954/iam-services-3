package org.openiam.core.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
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
    @Column(name="KEY", length=80, nullable = false)
    private String key;

    @ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

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
}
