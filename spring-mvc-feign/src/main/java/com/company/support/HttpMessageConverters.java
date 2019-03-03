package com.company.support;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * FileName: HttpMessageConverters
 * Author:   lvzhen
 * Date:      2019/3/3
 * Description: TODO
 */
public class HttpMessageConverters implements Iterable<HttpMessageConverter<?>> {
    private static final List<Class<?>> NON_REPLACING_CONVERTERS;
    private final List<HttpMessageConverter<?>> converters;

    public HttpMessageConverters(HttpMessageConverter... additionalConverters) {
        this((Collection) Arrays.asList(additionalConverters));
    }

    public HttpMessageConverters(Collection<HttpMessageConverter<?>> additionalConverters) {
        this(true, additionalConverters);
    }

    public HttpMessageConverters(boolean addDefaultConverters, Collection<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> defaultConverters;
        if (addDefaultConverters) {
            defaultConverters = this.getDefaultConverters();
        } else {
            defaultConverters = Collections.emptyList();
        }
        List<HttpMessageConverter<?>> combined = this.getCombinedConverters(converters, defaultConverters);
        combined = this.postProcessConverters(combined);
        this.converters = Collections.unmodifiableList(combined);
    }

    private List<HttpMessageConverter<?>> getCombinedConverters(Collection<HttpMessageConverter<?>> converters, List<HttpMessageConverter<?>> defaultConverters) {
        List<HttpMessageConverter<?>> combined = new ArrayList();
        List<HttpMessageConverter<?>> processing = new ArrayList(converters);
        Iterator var5 = defaultConverters.iterator();

        while (var5.hasNext()) {
            HttpMessageConverter<?> defaultConverter = (HttpMessageConverter) var5.next();
            Iterator iterator = processing.iterator();

            while (iterator.hasNext()) {
                HttpMessageConverter<?> candidate = (HttpMessageConverter) iterator.next();
                if (this.isReplacement(defaultConverter, candidate)) {
                    combined.add(candidate);
                    iterator.remove();
                }
            }

            combined.add(defaultConverter);
            if (defaultConverter instanceof AllEncompassingFormHttpMessageConverter) {
                this.configurePartConverters((AllEncompassingFormHttpMessageConverter) defaultConverter, converters);
            }
        }

        combined.addAll(0, processing);
        return combined;
    }

    private boolean isReplacement(HttpMessageConverter<?> defaultConverter, HttpMessageConverter<?> candidate) {
        Iterator var3 = NON_REPLACING_CONVERTERS.iterator();

        Class nonReplacingConverter;
        do {
            if (!var3.hasNext()) {
                return ClassUtils.isAssignableValue(defaultConverter.getClass(), candidate);
            }

            nonReplacingConverter = (Class) var3.next();
        } while (!nonReplacingConverter.isInstance(candidate));

        return false;
    }

    private void configurePartConverters(AllEncompassingFormHttpMessageConverter formConverter, Collection<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> partConverters = this.extractPartConverters(formConverter);
        List<HttpMessageConverter<?>> combinedConverters = this.getCombinedConverters(converters, partConverters);
        combinedConverters = this.postProcessPartConverters(combinedConverters);
        formConverter.setPartConverters(combinedConverters);
    }

    private List<HttpMessageConverter<?>> extractPartConverters(FormHttpMessageConverter formConverter) {
        Field field = ReflectionUtils.findField(FormHttpMessageConverter.class, "partConverters");
        ReflectionUtils.makeAccessible(field);
        return (List) ReflectionUtils.getField(field, formConverter);
    }

    protected List<HttpMessageConverter<?>> postProcessConverters(List<HttpMessageConverter<?>> converters) {
        return converters;
    }

    protected List<HttpMessageConverter<?>> postProcessPartConverters(List<HttpMessageConverter<?>> converters) {
        return converters;
    }

    private List<HttpMessageConverter<?>> getDefaultConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList();
        if (ClassUtils.isPresent("org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport", (ClassLoader) null)) {
            converters.addAll((new WebMvcConfigurationSupport() {
                public List<HttpMessageConverter<?>> defaultMessageConverters() {
                    return super.getMessageConverters();
                }
            }).defaultMessageConverters());
        } else {
            converters.addAll((new RestTemplate()).getMessageConverters());
        }

        this.reorderXmlConvertersToEnd(converters);
        return converters;
    }

    private void reorderXmlConvertersToEnd(List<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> xml = new ArrayList();
        Iterator iterator = converters.iterator();

        while (true) {
            HttpMessageConverter converter;
            do {
                if (!iterator.hasNext()) {
                    converters.addAll(xml);
                    return;
                }

                converter = (HttpMessageConverter) iterator.next();
            }
            while (!(converter instanceof AbstractXmlHttpMessageConverter));

            xml.add(converter);
            iterator.remove();
        }
    }

    @Override
    public Iterator<HttpMessageConverter<?>> iterator() {
        return this.getConverters().iterator();
    }

    public List<HttpMessageConverter<?>> getConverters() {
        return this.converters;
    }

    private static void addClassIfExists(List<Class<?>> list, String className) {
        try {
            list.add(Class.forName(className));
        } catch (NoClassDefFoundError | ClassNotFoundException var3) {
            ;
        }

    }

    static {
        List<Class<?>> nonReplacingConverters = new ArrayList();
        addClassIfExists(nonReplacingConverters, "org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter");
        NON_REPLACING_CONVERTERS = Collections.unmodifiableList(nonReplacingConverters);
    }
}
