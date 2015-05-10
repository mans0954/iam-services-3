package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UIThemeSearchBean", propOrder = {
	
})
public class UIThemeSearchBean extends AbstractSearchBean<UITheme, String> {

	public UIThemeSearchBean() {}

}
