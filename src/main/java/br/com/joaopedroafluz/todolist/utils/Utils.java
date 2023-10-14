package br.com.joaopedroafluz.todolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;

public class Utils {

    public static String[] getNullPropertyNames(Object source) {
        final var beanWrapper = new BeanWrapperImpl(source);
        final var propertyDescriptors = beanWrapper.getPropertyDescriptors();

        final var emptyNames = new HashSet<String>();

        for (var propertyDescriptor : propertyDescriptors) {
            final var propertyValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());

            if (propertyValue == null) {
                emptyNames.add(propertyDescriptor.getName());
            }
        }

        final var result = new String[emptyNames.size()];

        return emptyNames.toArray(result);
    }

    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

}
