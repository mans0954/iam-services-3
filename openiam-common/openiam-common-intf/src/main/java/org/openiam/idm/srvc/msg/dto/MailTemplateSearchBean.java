package org.openiam.idm.srvc.msg.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.SearchBean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailTemplateSearchBean", propOrder = {
})
public class MailTemplateSearchBean extends AbstractKeyNameSearchBean<MailTemplateDto, String> implements SearchBean {

}
