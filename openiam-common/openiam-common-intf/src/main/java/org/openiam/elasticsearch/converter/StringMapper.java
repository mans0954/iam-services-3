package org.openiam.elasticsearch.converter;

public class StringMapper implements FieldMapper<String> {

	@Override
	public String map(Object o) {
		String retVal = null;
		if(o != null) {
			if(o instanceof String) {
				retVal = (String)o;
			} else {
				retVal = o.toString();
			}
		}
		return retVal;
	}

}
