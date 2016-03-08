package org.openiam.base;

import java.io.Serializable;

public interface BaseIdentity extends Serializable {
	String getId();
	void setId(final String id);
}
