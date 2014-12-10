package org.openiam.idm.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CustomJacksonMapper extends ObjectMapper {

	public CustomJacksonMapper() {
		super();
		this.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
		 .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
		 .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
		 .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public String mapToStringQuietly(final Object o) {
		try {
			return writeValueAsString(o);
		} catch(Throwable e) {
			return null;
		}
	}
}
