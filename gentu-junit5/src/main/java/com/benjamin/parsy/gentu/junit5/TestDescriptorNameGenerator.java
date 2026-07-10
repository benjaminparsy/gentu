package com.benjamin.parsy.gentu.junit5;

import com.benjamin.parsy.gentu.core.annotation.TestDescriptor;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;
import java.util.List;

public class TestDescriptorNameGenerator extends DisplayNameGenerator.Standard {

    @Override
    public String generateDisplayNameForMethod(List<Class<?>> enclosingInstanceTypes, Class<?> testClass, Method testMethod) {

        TestDescriptor td = testMethod.getAnnotation(TestDescriptor.class);

        if (td != null) {
            return td.testName();
        }

        return super.generateDisplayNameForMethod(enclosingInstanceTypes, testClass, testMethod);
    }

}
