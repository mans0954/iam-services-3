package org.openiam.core.dao;

import org.hibernate.criterion.Restrictions;
import org.openiam.core.domain.UserKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
    public List<UserKey> getByUserIdsKeyName(List<String> userIds, String keyName){
        return (getCriteria().add(Restrictions.in("userId", userIds))
                .add(Restrictions.eq("name", keyName)).list());
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) throws Exception {
        getSession().createQuery("delete from " + this.domainClass.getName() + " obj where obj.userId=?")
                .setParameter(0, userId).executeUpdate();
    }

    @Override
    public UserKey getByUserIdKeyName(String userId, String keyName) throws Exception {
//        List<UserKey> result = (List<UserKey>) getSession().createQuery(
//                "select obj from " + this.domainClass.getName() + " obj where obj.userId=:userId and obj.name=:keyName")
//                .setParameter("userId", userId).setParameter("keyName", keyName).list();
        return (UserKey)(getCriteria().add(Restrictions.eq("userId", userId))
                                      .add(Restrictions.eq("name", keyName)).uniqueResult());
//        if(result == null || result.isEmpty()) {
//            return null;
//        }
//        return result.get(0);
    }

    @Override
    public List<UserKey> getByUserId(String userId)throws Exception {
        return (List<UserKey>) getSession().createQuery(
                "select obj from " + this.domainClass.getName() + " obj where obj.userId=:userId")
                .setParameter("userId", userId).list();
    }

    @Override
    public List<UserKey> getSublist(int startPos, int size)throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(this.domainClass.getName()).append(" uk");
        return (List<UserKey>)getSession().createQuery(sql.toString()).setFirstResult(startPos).setMaxResults(size).list();
    }
}
