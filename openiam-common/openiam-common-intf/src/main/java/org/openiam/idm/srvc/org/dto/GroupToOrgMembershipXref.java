package org.openiam.idm.srvc.org.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.membership.dto.AbstractMembershipXref;
import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupToOrgMembershipXref", propOrder = {
})
@DozerDTOCorrespondence(GroupToOrgMembershipXrefEntity.class)
public class GroupToOrgMembershipXref extends AbstractMembershipXref {

}
