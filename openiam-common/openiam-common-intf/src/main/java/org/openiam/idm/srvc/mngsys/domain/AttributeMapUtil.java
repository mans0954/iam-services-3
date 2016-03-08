package org.openiam.idm.srvc.mngsys.domain;

import org.openiam.idm.srvc.mngsys.dto.AttributeMap;

public class AttributeMapUtil {

    public static String getAttributeIDMFieldName(AttributeMap a) {
        if (a == null)
            return null;
        if (a.getReconResAttribute() == null)
            return null;
        if (a.getReconResAttribute().getAttributePolicy() != null)
            return a.getReconResAttribute().getAttributePolicy().getName();
        if (a.getReconResAttribute().getDefaultAttributePolicy() != null)
            return a.getReconResAttribute().getDefaultAttributePolicy()
                    .getName();
        return null;
    }

	public static String getAttributeIDMFieldName(AttributeMapEntity a) {
		if (a == null)
			return null;
		if (a.getReconResAttribute() == null)
			return null;
		if (a.getReconResAttribute().getAttributePolicy() != null)
			return a.getReconResAttribute().getAttributePolicy().getName();
		if (a.getReconResAttribute().getDefaultAttributePolicy() != null)
			return a.getReconResAttribute().getDefaultAttributePolicy()
					.getName();
		return null;
	}
}
