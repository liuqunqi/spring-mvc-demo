# spring-mvc-demo


Spring mvc 3.X 引入 feign 源码分析:

目前所使用的feign来自于3个部分，或者说2个部分。 
第一个部分是来自社区的open-feign。具体来源是 Netflix还是社区未验证。 
第二个部分是 Netflix的封装。 
第三个部分是来自于Spring的封装。 
实际上大伙也就知道feign呢和那个影片租赁公司 Netflix脱不开关系就是了。以上三个来源最终变成了 
1. org.springframework.cloud:spring-cloud-netflix-core 
2. io.github.openfeign:feign-core


--openFeign的关键标签类：
1. Client(接口) - Feign(抽象类) - ReflectiveFeign(实现类)。 
2. RequestTemplate 
3. InvocationHandlerFactory(接口) - SynchronousMethodHandler(实现类) 
4. Decoder与Encoder
调用栈大约如下 
1. ReflectiveFeign 被反射实例化 
2. 调用ReflectiveFeign.invoke 
3. 调用SynchronousMethodHandler.invoke。此处实例化RequestTemplate 
4. 调用SynchronousMethodHandler.executeAndDecode 
5. 将RequestTemplate build为request,调用http客户端执行 
6. 将Response Decode为Object并返回
--------------------- 

--spring cloud 封装.

1.@EnableFeignClients注解将所有带有@FeignClient的类或接口注册到Spring中，注册类为FeignClientFactoryBean

2.FeignClientFactoryBean.getObject()方法返回的是一个代理类，InvocationHandler中包含类中每个方法对应的MethodHandler，也就是SynchronousMethodHandler，方法真正执行就是SynchronousMethodHandler.invoke()方法

3.LoadBalancerFeignClient.execute()方法进行业务的处理，在这一步操作中就用到了ribbon和Hystrix功能
