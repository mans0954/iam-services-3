package org.openiam.idm.srvc.continfo.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailEntity;
import org.springframework.stereotype.Repository;

import javax.naming.InitialContext;
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
}
