package org.openiam.cache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openiam.base.BaseIdentity;
import org.springframework.stereotype.Component;

@Component
public class DefaultOpeniamKeyGenerator implements OpeniamKeyGenerator {

	@Override
	public List<String> generateKey(final Object parameter) {
		if(parameter != null) {
			final List<String> keys = new LinkedList<String>();
			if(parameter instanceof String) {
				keys.add((String)parameter);
			} else if(parameter instanceof BaseIdentity) {
				keys.add(((BaseIdentity)parameter).getId());
			} else if(parameter instanceof Collection) {
				for(final Object param : (Collection)parameter) {
					if(param instanceof String) {
						keys.add((String)param);
					} else if(param instanceof BaseIdentity) {
						keys.add(((BaseIdentity)param).getId());
					} else {
						throw new IllegalStateException(String.format("Colleciton had an object %s that was neither a String or BaseIdentity", param));
					}
				}
			} else {
				throw new IllegalStateException(String.format("%s was neither a String or BaseIdentity", parameter));
			}
			return keys;
		}
		return null;
	}

}
