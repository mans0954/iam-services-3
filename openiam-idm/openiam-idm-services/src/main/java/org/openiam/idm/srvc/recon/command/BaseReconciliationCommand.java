package org.openiam.idm.srvc.recon.command;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseReconciliationCommand {

	protected static final char MULTIPLE_VALUES_DELIMITER = '^';
	protected static final String DEFAULT_REQUESTER_ID = "3000";
	public static final String OPENIAM_MANAGED_SYS_ID = "0";

	protected Map<String, String> attributesToMap(List<ExtensibleAttribute> attributes) {
		Map<String, String> line = new HashMap<String, String>();
		for (ExtensibleAttribute attr : attributes) {
			if (attr.getValue() != null) {
				line.put(attr.getName(), attr.getValue());
			} else if (attr.getAttributeContainer() != null &&
					CollectionUtils.isNotEmpty(attr.getAttributeContainer().getAttributeList()) &&
					line.get(attr.getName()) == null) {
				StringBuilder value = new StringBuilder();
				boolean isFirst = true;
				for (BaseAttribute ba : attr.getAttributeContainer().getAttributeList()) {
					if (!isFirst) {
						value.append(MULTIPLE_VALUES_DELIMITER);
					} else {
						isFirst = false;
					}
					value.append(ba.getValue());
				}
				line.put(attr.getName(), value.toString());
			} else if (attr.getValueList() != null && attr.getValueList().size() > 0) {
                StringBuilder value = new StringBuilder();
                boolean isFirst = true;
                for (String val : attr.getValueList()) {
                    if (!isFirst) {
                        value.append(MULTIPLE_VALUES_DELIMITER);
                    } else {
                        isFirst = false;
                    }
                    value.append(val);
                }
                line.put(attr.getName(), value.toString());
            }
		}
		return line;
    }
}
