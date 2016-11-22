package org.openiam.elasticsearch.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfStringMapper implements FieldMapper<List<String>> {

	@Override
	public List<String> map(Object o) {
		List<String> retVal = null;
		if(o != null) {
			if(o instanceof String[]) {
				retVal = Arrays.asList((String[])o);
			} else if(o instanceof List) {
				retVal = new ArrayList<String>(((List)o).size());
				for(final Object value : (List)o) {
					retVal.add((String)value);
				}
			}
		}
		return retVal;
	}

}
