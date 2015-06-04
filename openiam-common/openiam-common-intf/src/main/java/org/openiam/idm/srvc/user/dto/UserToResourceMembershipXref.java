package org.openiam.idm.srvc.user.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.membership.dto.AbstractMembershipXref;
import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToResourceMembershipXref", propOrder = {
})
@DozerDTOCorrespondence(UserToResourceMembershipXrefEntity.class)
public class UserToResourceMembershipXref extends AbstractMembershipXref {

}
