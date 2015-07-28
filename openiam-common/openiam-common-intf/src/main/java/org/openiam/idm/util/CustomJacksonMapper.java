package org.openiam.idm.util;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component("customJacksonMapper")
public class CustomJacksonMapper extends ObjectMapper {

	public CustomJacksonMapper() {
		super();
		this.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
		 .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
		 .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
		 .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		final SimpleModule stringModule = new SimpleModule("MyModule", new Version(1, 0, 0, null)).addDeserializer(String.class, new StringDeserializer());
		this.registerModule(stringModule);
	}
	
	private class StringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            String str = StringUtils.trimToNull(parser.getText());
            return str;
        }

	}
	
	public String mapToStringQuietly(final Object o) {
		try {
			return writeValueAsString(o);
		} catch(Throwable e) {
			return null;
		}
	}
}
