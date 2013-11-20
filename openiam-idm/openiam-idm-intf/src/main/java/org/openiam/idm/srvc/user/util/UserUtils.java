package org.openiam.idm.srvc.user.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;

public class UserUtils {
    public static Map<String, ReconciliationResultField> extensibleAttributeListToReconciliationResultFieldMap(
            ExtensibleUser findedObject) {
        Map<String, ReconciliationResultField> user2Map = new HashMap<String, ReconciliationResultField>();
        for (ExtensibleAttribute a : findedObject.getAttributes()) {
            ReconciliationResultField field = new ReconciliationResultField();
            if (a.getName().equals(findedObject.getPrincipalFieldName())) {
                field.setKeyField(true);
            }
            if (a.isMultivalued()) {
                field.setValues(a.getValueList());
            } else {
                List<String> l = new ArrayList<String>();
                l.add(a.getValue());
                field.setValues(l);
            }
            user2Map.put(a.getName(), field);

        }
        return user2Map;
    }

    public static List<ExtensibleAttribute> reconciliationResultFieldMapToExtensibleAttributeList(
            ReconciliationResultRow header,
            Map<String, ReconciliationResultField> map) {
        List<ExtensibleAttribute> result = new ArrayList<ExtensibleAttribute>();
        int i = 0;
        for (ReconciliationResultField field : map.values()) {
            ExtensibleAttribute e = new ExtensibleAttribute(header.getFields()
                    .get(++i).getValues().get(0), field.getValues().get(0));
            result.add(e);

        }
        return result;
    }
}
