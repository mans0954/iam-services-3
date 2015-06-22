package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MngSysPolicyDto", propOrder = {})
@DozerDTOCorrespondence(ManagedSysEntity.class)
public class MngSysPolicyDto {

}
