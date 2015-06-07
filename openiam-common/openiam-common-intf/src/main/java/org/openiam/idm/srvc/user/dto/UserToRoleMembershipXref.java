package org.openiam.idm.srvc.user.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.membership.dto.AbstractMembershipXref;
import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToRoleMembershipXref", propOrder = {
})
@DozerDTOCorrespondence(UserToRoleMembershipXrefEntity.class)
public class UserToRoleMembershipXref extends AbstractMembershipXref {

}
