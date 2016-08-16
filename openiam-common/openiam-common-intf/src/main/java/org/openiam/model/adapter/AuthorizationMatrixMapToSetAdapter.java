package org.openiam.model.adapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openiam.model.adapter.AuthorizationMatrixMapToSet.AuthorizationMatrixMapToSetEntry;

public class AuthorizationMatrixMapToSetAdapter extends XmlAdapter<AuthorizationMatrixMapToSet, Map<String, Map<String, Set<String>>>> {

	@Override
	public Map<String, Map<String, Set<String>>> unmarshal(final AuthorizationMatrixMapToSet v)
			throws Exception {
		final Map<String, Map<String, Set<String>>> retVal = new HashMap<String, Map<String, Set<String>>>();
		if(v != null && CollectionUtils.isNotEmpty(v.getEntries())) {
			for(final AuthorizationMatrixMapToSetEntry entry : v.getEntries()) {
				retVal.put(entry.getKey(), entry.getValues());
			}
		}
		return retVal;
	}

	@Override
	public AuthorizationMatrixMapToSet marshal(final Map<String, Map<String, Set<String>>> v)
			throws Exception {
		final AuthorizationMatrixMapToSet retVal = new AuthorizationMatrixMapToSet();
		if(v != null && MapUtils.isNotEmpty(v)) {
			final List<AuthorizationMatrixMapToSet.AuthorizationMatrixMapToSetEntry> entries = new LinkedList<AuthorizationMatrixMapToSet.AuthorizationMatrixMapToSetEntry>();
			for(final String key : v.keySet()) {
				entries.add(new AuthorizationMatrixMapToSetEntry(key, v.get(key)));
			}
			retVal.setEntries(entries);
		}
		return retVal;
	}

}
