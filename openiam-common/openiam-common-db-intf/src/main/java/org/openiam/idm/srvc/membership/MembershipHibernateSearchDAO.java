package org.openiam.idm.srvc.membership;

import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.openiam.idm.searchbeans.MembershipSearchBean;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;

public interface MembershipHibernateSearchDAO<T extends AbstractMembershipXrefEntity> 
		extends HibernateSearchDao<T, MembershipSearchBean, String> {

}
