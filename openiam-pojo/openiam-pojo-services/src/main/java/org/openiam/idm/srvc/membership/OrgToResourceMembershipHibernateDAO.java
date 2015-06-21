package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.ResourceToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToResourceMembershipHibernateDAO")
public class OrgToResourceMembershipHibernateDAO extends AbstractMembershipHibernateDAO<ResourceToOrgMembershipXrefEntity> {

	@Override
	protected Class<ResourceToOrgMembershipXrefEntity> getEntityClass() {
		return ResourceToOrgMembershipXrefEntity.class;
	}

}
