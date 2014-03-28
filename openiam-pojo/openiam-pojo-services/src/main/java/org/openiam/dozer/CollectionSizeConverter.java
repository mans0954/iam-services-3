package org.openiam.dozer;

import org.dozer.CustomConverter;

import java.lang.Class;import java.lang.Integer;import java.lang.Object;import java.lang.Override;import java.util.Collection;
import java.util.List;

public class CollectionSizeConverter implements CustomConverter {

    @Override
    public Object convert(Object destVal, Object sourceVal, Class<?> destClass, Class<?> sourceClass) {
        Integer retVal = null;
        if(sourceVal != null) {
            final Collection sourceCollection = (Collection)sourceVal;
            retVal = sourceCollection.size();
        }
        return retVal;
    }

}
