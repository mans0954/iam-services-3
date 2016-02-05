package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;

/**
 * @author zaporozhec
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
	name = "DefaultReconciliationAttributeMap", 
	propOrder = {})
@DozerDTOCorrespondence(DefaultReconciliationAttributeMapEntity.class)
public class DefaultReconciliationAttributeMap extends KeyNameDTO {

    private static final long serialVersionUID = -4584242607384442243L;

}
