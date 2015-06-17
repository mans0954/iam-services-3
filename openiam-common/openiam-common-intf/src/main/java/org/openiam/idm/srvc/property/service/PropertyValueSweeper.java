package org.openiam.idm.srvc.property.service;

import org.openiam.idm.srvc.lang.dto.Language;

public interface PropertyValueSweeper {

	String getValue(final String key, final Language language);
	
	String getString(final String key);
	boolean getBoolean(final String key);
	int getInt(final String key);
}
