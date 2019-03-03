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

import com.company.support.HttpMessageConverters;
import com.company.support.SpringDecoder;
import com.company.support.SpringEncoder;
import com.company.support.SpringMvcContract;
import feign.Contract.BaseContract;
import feign.Feign;
import feign.MethodMetadata;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author lvzhen
 * @version Id: FeignRegister.java, v 0.1 2019/2/26 14:32 lvzhen Exp $$
 */
@Component
public class FeignRegister implements BeanFactoryPostProcessor {

    private static final String SCAN_BASE_PACKAGE = "com.company.clients";
    private static Logger log = LoggerFactory.getLogger(FeignRegister.class);


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        Reflections f = new Reflections(SCAN_BASE_PACKAGE);
        if (f == null) {
            log.warn(SCAN_BASE_PACKAGE + "  not find class....");
            return;
        }
        Set<Class<?>> set = f.getTypesAnnotatedWith(FeignApi.class);
        Feign.Builder builder = getFeignBuilder();
        for (Class<?> targetClass : set) {
            FeignApi annotation = targetClass.getAnnotation(FeignApi.class);
            Object target = builder.target(targetClass, annotation.serviceUrl());
            configurableListableBeanFactory.registerSingleton(targetClass.getName(), target);
        }
    }

    public Feign.Builder getFeignBuilder() {
        Feign.Builder builder = Feign.builder()
                //使用Jackson进行参数处理，如果有必要可以自行定义
                .encoder(new SpringEncoder())
                .decoder(new SpringDecoder())
                .contract(new SpringMvcContract())
                //超时处理
                /*.options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3))
                //每次请求时，自定义内部请求头部信息，例如：权限相关的信息
                .requestInterceptor(new RequestInterceptor() {
                    @Override
                    public void apply(RequestTemplate requestTemplate) {
                        requestTemplate.header("Content-Type", "application/json");
                        requestTemplate.header("Accept", "application/json");
                    }
                })*/;
        return builder;
    }

}