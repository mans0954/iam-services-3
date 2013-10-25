package org.openiam.core.dao;

import org.openiam.core.domain.UserKey;
import org.openiam.idm.srvc.auth.dto.Login;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 08.10.12
 */
@Repository
public class UserKeyImpl extends BaseDaoImpl<UserKey, String> implements UserKeyDao {
    @Override
    protected String getPKfieldName() {
        return "userKeyId";
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) throws Exception {
        getSession().createQuery("delete from " + this.domainClass.getName() + " obj where obj.user.userId=?")
                    .setParameter(0, userId).executeUpdate();
    }

    @Override
    public UserKey getByUserIdKeyName(String userId, String keyName) throws Exception {
        List<UserKey> result = (List<UserKey>) getSession().createQuery(
                "select obj from " + this.domainClass.getName() + " obj where obj.user.userId=:userId and obj.name=:keyName")
                                                             .setParameter("userId", userId).setParameter("keyName", keyName).list();

        if(result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public List<UserKey> getByUserId(String userId)throws Exception {
        return (List<UserKey>) getSession().createQuery(
                "select obj from " + this.domainClass.getName() + " obj where obj.user.userId=:userId")
                                                             .setParameter("userId", userId).list();
    }

    @Override
    public List<UserKey> getSublist(int startPos, int size)throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(this.domainClass.getName()).append(" uk");
        return (List<UserKey>)getSession().createQuery(sql.toString()).setFirstResult(startPos).setMaxResults(size).list();
    }
}
