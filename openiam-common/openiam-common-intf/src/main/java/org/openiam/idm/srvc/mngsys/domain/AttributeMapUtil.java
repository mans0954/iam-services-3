package org.openiam.idm.srvc.mngsys.domain;

public class AttributeMapUtil {

    public static String getAttributeIDMFieldName(AttributeMapEntity a) {
        if (a == null)
            return null;
        if (a.getReconResAttribute() == null)
            return null;
        if (a.getReconResAttribute().getAttributePolicy() != null)
            return a.getReconResAttribute().getAttributePolicy().getName();
        if (a.getReconResAttribute().getDefaultAttributePolicy() != null)
            return a.getReconResAttribute().getDefaultAttributePolicy()
                    .getDefaultAttributeMapName();
        return null;
    }
}
