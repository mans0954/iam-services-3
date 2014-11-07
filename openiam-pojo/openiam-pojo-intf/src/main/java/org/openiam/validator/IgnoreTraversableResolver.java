package org.openiam.validator;

import javax.validation.Path;
import javax.validation.TraversableResolver;
import java.lang.annotation.ElementType;

public class IgnoreTraversableResolver implements TraversableResolver {

    @Override
    public boolean isReachable(Object traversableObject,
                               Path.Node traversableProperty, Class<?> rootBeanType,
                               Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    @Override
    public boolean isCascadable(Object traversableObject,
                                Path.Node traversableProperty, Class<?> rootBeanType,
                                Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

}