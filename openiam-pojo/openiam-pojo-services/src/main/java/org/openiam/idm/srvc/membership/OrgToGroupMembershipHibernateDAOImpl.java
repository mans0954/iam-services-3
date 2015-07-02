package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToGroupMembershipHibernateDAO")
public class OrgToGroupMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<GroupToOrgMembershipXrefEntity>
	   implements OrgToGroupMembershipHibernateDAO {

	@Override
	protected Class<GroupToOrgMembershipXrefEntity> getEntityClass() {
		return GroupToOrgMembershipXrefEntity.class;
	}

}
