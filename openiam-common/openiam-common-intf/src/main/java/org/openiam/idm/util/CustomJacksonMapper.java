package org.openiam.idm.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

public class CustomJacksonMapper extends ObjectMapper {

	public CustomJacksonMapper() {
		super();
		this.configure(Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
		 .configure(Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
		 .configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public String mapToStringQuietly(final Object o) {
		try {
			return writeValueAsString(o);
		} catch(Throwable e) {
			return null;
		}
	}
}
