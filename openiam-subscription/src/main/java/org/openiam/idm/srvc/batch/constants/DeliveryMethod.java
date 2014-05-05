package org.openiam.idm.srvc.batch.constants;

public enum DeliveryMethod {
    EMAIL, VIEW;

    public static DeliveryMethod parseMethod(String deliveryMethod) {
        return Enum.valueOf(DeliveryMethod.class, deliveryMethod);
    }
}
