package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToOrgMembershipHibernateDAO")
public class OrgToOrgMembershipHibernateDAO extends AbstractMembershipHibernateDAO<OrgToOrgMembershipXrefEntity> {

	@Override
	protected Class<OrgToOrgMembershipXrefEntity> getEntityClass() {
		return OrgToOrgMembershipXrefEntity.class;
	}

}
