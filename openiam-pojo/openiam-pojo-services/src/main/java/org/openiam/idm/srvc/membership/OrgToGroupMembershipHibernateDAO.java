package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToGroupMembershipHibernateDAO")
public class OrgToGroupMembershipHibernateDAO extends AbstractMembershipHibernateDAO<GroupToOrgMembershipXrefEntity> {

	@Override
	protected Class<GroupToOrgMembershipXrefEntity> getEntityClass() {
		return GroupToOrgMembershipXrefEntity.class;
	}

}
