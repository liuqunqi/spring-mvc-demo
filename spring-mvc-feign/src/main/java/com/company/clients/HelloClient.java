package com.company.clients;

//import org.springframework.cloud.netflix.clients.FeignClient;

import com.company.annotation.FeignApi;
import feign.Headers;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @项目名称: springmvcdemo
 * @标题信息: HelloClient
 * @创建人: Ailen
 * @创建日期: 2019/2/2417:55
 * @描述信息: TODO
 */
@FeignApi(serviceUrl = "http://localhost:8762")
@RequestMapping("/spring-cloud-service")
public interface HelloClient {

    @RequestMapping(value = "/sayHello", method = RequestMethod.POST)
    String sayHello(@RequestParam("name") String name);


    /* @RequestLine("POST /savePerson")
     @Headers("Content-type: application/json")*/
    @RequestMapping(value = "/savePerson", method = RequestMethod.POST)
    @ResponseBody
    String savePerson(@RequestBody Person person);

    @RequestMapping(value = "/updatePerson", method = RequestMethod.POST)
    @ResponseBody
    Person updatePerson(@RequestBody Person person);

}
