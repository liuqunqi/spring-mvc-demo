/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wandaph.mvc.openfeign;

import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Feign contract method parameters processor.
 *
 * @author Jakub Narloch
 * @author Abhijit Sarkar
 */
public interface AnnotatedParameterProcessor {

    /**
     * Retrieves the processor supported annotation type.
     *
     * @return the annotation type
     */
    Class<? extends Annotation> getAnnotationType();

    /**
     * Process the annotated parameters.
     *
     * @param context    the parameters context
     * @param annotation the annotation instance
     * @param method     the method that contains the annotation
     * @return whether the parameters is http
     */
    boolean processArgument(AnnotatedParameterContext context, Annotation annotation,
                            Method method);

    /**
     * Specifies the parameters context.
     *
     * @author Jakub Narloch
     */
    interface AnnotatedParameterContext {

        /**
         * Retrieves the method metadata.
         *
         * @return the method metadata
         */
        MethodMetadata getMethodMetadata();

        /**
         * Retrieves the index of the parameters.
         *
         * @return the parameters index
         */
        int getParameterIndex();

        /**
         * Sets the parameters name.
         *
         * @param name the name of the parameters
         */
        void setParameterName(String name);

        /**
         * Sets the template parameters.
         *
         * @param name the template parameters
         * @param rest the existing parameters values
         * @return parameters
         */
        Collection<String> setTemplateParameter(String name, Collection<String> rest);

    }

}
