package org.openiam.idm.srvc.continfo.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailEntity;
 import org.springframework.stereotype.Repository;

import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vitalia on 5/31/16.
 */
@Repository("emailDAO")
public class EmailDAOImpl extends BaseDaoImpl<EmailEntity, String> implements EmailDAO {


    @Override
    protected Criteria getExampleCriteria(EmailEntity email){
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(email.getEmailId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), email.getEmailId()));
        }

        return criteria;
    }

    @Override
    protected String getPKfieldName() {

        return "emailId";
    }



    @Override
    public List<EmailEntity> getEmailsForUser(String userId, int from, int size) {
        final Criteria criteria =getCriteria();
        if (StringUtils.isNotBlank(userId)) {
            criteria.add(Restrictions.eq("parentId", userId));
        }
        return getList(criteria, from, size);
    }



    private List<EmailEntity> getList(Criteria criteria, int from, int size) {
        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }
}
