package org.openiam.idm.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CustomJacksonMapper extends ObjectMapper {

	public CustomJacksonMapper() {
		super();
		this.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
	}
	
	public String mapToStringQuietly(final Object o) {
		try {
			return writeValueAsString(o);
		} catch(Throwable e) {
			return null;
		}
	}
}
