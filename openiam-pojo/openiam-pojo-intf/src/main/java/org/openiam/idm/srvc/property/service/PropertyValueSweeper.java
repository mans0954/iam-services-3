package org.openiam.idm.srvc.property.service;

import org.openiam.idm.srvc.lang.dto.Language;

public interface PropertyValueSweeper {

	public String getValue(final String key, final Language language);
	
	public String getString(final String key);
	public boolean getBoolean(final String key);
	public int getInt(final String key);
}
