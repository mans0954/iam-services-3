package org.openiam.dozer.crossentity;

import java.util.Collection;

import org.dozer.Mapper;
import org.hibernate.proxy.HibernateProxyHelper;
import org.openiam.dozer.DozerDTOCorrespondence;

public abstract class AbstractCrossEntityCollectionConverter {

    protected Mapper mapper = null;

    protected Class<?> determineTargetClass(final Object entry) {
        Class<?> retVal = null;
        Class<?> sourceClass = HibernateProxyHelper.getClassWithoutInitializingProxy(entry);

        final DozerDTOCorrespondence correspondingClassAnnotation = sourceClass.getAnnotation(DozerDTOCorrespondence.class);
        if (correspondingClassAnnotation != null) {
            retVal = correspondingClassAnnotation.value();
        }
        if (retVal == null) {
            throw new RuntimeException(String.format("Class %s doesn't have the annoation %s, or value is null", sourceClass,
                                                     DozerDTOCorrespondence.class));
        }
        return retVal;
    }

    protected void convert(final Collection from, final Collection to) {
        if (from != null && to != null) {
            Class<?> targetClass = null;
            for (final Object entity : from) {
                if (entity != null) {
                    if (targetClass == null) {
                        targetClass = determineTargetClass(entity);
                    }
                    if (mapper != null) {
                        to.add(mapper.map(entity, targetClass));
                    } else {
                        to.add(entity);
                    }
                }
            }
        }
    }
}
