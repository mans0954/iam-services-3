package org.openiam.idm.srvc.user.service;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.stereotype.Repository;

@Repository("profilePictureDAO")
public class ProfilePictureDAOImpl extends BaseDaoImpl<ProfilePictureEntity, String> implements ProfilePictureDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public void deleteById(String picId) {
        Query qry = getSession().createQuery("delete " + domainClass.getName() + " s where s.id = :pk ");
        qry.setParameter("pk", picId);
        qry.executeUpdate();
    }

    @Override
    public ProfilePictureEntity getByUserId(String userId) {
        Criteria c = getCriteria().add(Restrictions.eq("user.id", userId));
        return (ProfilePictureEntity)c.uniqueResult();
    }

    @Override
    public void deleteByUserId(String userId) {
        ProfilePictureEntity entity = getByUserId(userId);
        if (entity != null) {
            deleteById(entity.getId());
        }
    }

}
