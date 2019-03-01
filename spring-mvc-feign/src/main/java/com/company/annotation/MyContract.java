/**
 * Software License Declaration.
 * <p>
 * wandaph.com, Co,. Ltd.
 * Copyright ? 2017 All Rights Reserved.
 * <p>
 * Copyright Notice
 * This documents is provided to wandaph contracting agent or authorized programmer only.
 * This source code is written and edited by wandaph Co,.Ltd Inc specially for financial
 * business contracting agent or authorized cooperative company, in order to help them to
 * install, programme or central control in certain project by themselves independently.
 * <p>
 * Disclaimer
 * If this source code is needed by the one neither contracting agent nor authorized programmer
 * during the use of the code, should contact to wandaph Co,. Ltd Inc, and get the confirmation
 * and agreement of three departments managers  - Research Department, Marketing Department and
 * Production Department.Otherwise wandaph will charge the fee according to the programme itself.
 * <p>
 * Any one,including contracting agent and authorized programmer,cannot share this code to
 * the third party without the agreement of wandaph. If Any problem cannot be solved in the
 * procedure of programming should be feedback to wandaph Co,. Ltd Inc in time, Thank you!
 */
package com.company.annotation;

import com.company.AnnotatedParameterProcessor;
import com.sun.istack.internal.Nullable;
import feign.Contract.BaseContract;
import feign.Feign;
import feign.MethodMetadata;
import feign.Param;
import feign.Request;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * @author lvzhen
 * @version Id: MyContract.java, v 0.1 2019/3/1 9:50 lvzhen Exp $$
 */
public class MyContract extends BaseContract {


    /**
     * 基于方法的注解
     *
     * @param methodMetadata
     * @param annotation
     * @param method
     */
    @Override
    protected void processAnnotationOnMethod(MethodMetadata methodMetadata, Annotation annotation, Method method) {
        if (RequestMapping.class.isInstance(annotation)) {
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            // HTTP Method
            RequestMethod[] methods = methodMapping.method();
            if (methods.length == 0) {
                methods = new RequestMethod[]{RequestMethod.GET};
            }
            checkOne(method, methods, "method");
            String[] url = methodMapping.value();
            methodMetadata.template().append(url.length == 0 ? null : url[0]);
            methodMetadata.template().method(methods[0].name());
        }
    }

    private final Map<String, Method> processedMethods = new HashMap();


    @Override
    public MethodMetadata parseAndValidateMetadata(Class<?> targetType, Method method) {
        this.processedMethods.put(Feign.configKey(targetType, method), method);
        MethodMetadata md = super.parseAndValidateMetadata(targetType, method);
        return md;
    }

    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        boolean isHttpAnnotation = false;
        for (Annotation parameterAnnotation : annotations) {
            if (parameterAnnotation instanceof RequestParam) {
                return true;
            }
        }
        return isHttpAnnotation;
    }

    @Override
    protected void processAnnotationOnClass(MethodMetadata data, Class<?> targetType) {
        if (targetType.isAnnotationPresent(RequestMapping.class)) {
            String[] classAnnotation = targetType.getAnnotation(RequestMapping.class).value();
            checkState(classAnnotation.length > 0, "Headers annotation was empty on type %s.",
                    targetType.getName());
            String pathValue = emptyToNull(classAnnotation[0]);
            if (!pathValue.startsWith("/")) {
                pathValue = "/" + pathValue;
            }
            data.template().append(pathValue);
        }
    }

    private void checkOne(Method method, Object[] values, String fieldName) {
        checkState(values != null && values.length == 1,
                "Method %s can only contain 1 %s field. Found: %s", method.getName(),
                fieldName, values == null ? null : Arrays.asList(values));
    }

}