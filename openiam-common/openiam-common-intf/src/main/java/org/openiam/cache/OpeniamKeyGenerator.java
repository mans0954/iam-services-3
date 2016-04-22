package org.openiam.cache;

import java.io.Serializable;
import java.util.List;

public interface OpeniamKeyGenerator extends Serializable {

	/* 
	 * 
	 */
	public List<String> generateKey(final Object parameter);
}
