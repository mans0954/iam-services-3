package org.openiam.core.dao;

import org.openiam.core.domain.UserKey;
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
        sessionFactory.getCurrentSession()
                      .createQuery("delete from " + this.domainClass.getName() + " obj where obj.userId=?1")
                      .setParameter(1, userId).executeUpdate();
    }

    @Override
    public UserKey getByUserIdKeyName(String userId, String keyName) throws Exception {
        List<UserKey> result = (List<UserKey>) sessionFactory.getCurrentSession().createQuery(
                "select from " + this.domainClass.getName() + " obj where obj.userId=?1 and obj.name=?2")
                                                             .setParameter(1, userId).setParameter(2, keyName).list();

        if(result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
}
