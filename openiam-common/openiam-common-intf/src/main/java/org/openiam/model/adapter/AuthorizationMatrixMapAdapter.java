package org.openiam.model.adapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openiam.model.adapter.AuthorizationMatrixMap.AuthorizationMatrixMapEntry;

public class AuthorizationMatrixMapAdapter extends XmlAdapter<AuthorizationMatrixMap, Map<String, Set<String>>> {

	@Override
	public Map<String, Set<String>> unmarshal(final AuthorizationMatrixMap v)
			throws Exception {
		final Map<String, Set<String>> retVal = new HashMap<String, Set<String>>();
		if(v != null && CollectionUtils.isNotEmpty(v.getEntries())) {
			for(final AuthorizationMatrixMapEntry entry : v.getEntries()) {
				retVal.put(entry.getKey(), entry.getValues());
			}
		}
		return retVal;
	}

	@Override
	public AuthorizationMatrixMap marshal(Map<String, Set<String>> v)
			throws Exception {
		final AuthorizationMatrixMap retVal = new AuthorizationMatrixMap();
		if(v != null && MapUtils.isNotEmpty(v)) {
			final List<AuthorizationMatrixMap.AuthorizationMatrixMapEntry> entries = new LinkedList<AuthorizationMatrixMap.AuthorizationMatrixMapEntry>();
			for(final String key : v.keySet()) {
				entries.add(new AuthorizationMatrixMapEntry(key, v.get(key)));
			}
			retVal.setEntries(entries);
		}
		return retVal;
	}

	
}
