package com.company.controller;

import com.company.clients.HelloClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @项目名称: springmvcdemo
 * @标题信息: DemoController
 * @创建人: Ailen
 * @创建日期: 2019/2/2417:57
 * @描述信息: TODO
 */
@Controller
public class DemoController {

    @Autowired
    HelloClient userClient;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String sayHello() {
        return "demo";
    }

    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    @ResponseBody
    public String hello(@RequestParam String name) {
        Map map= new HashMap<>();
        map.put("name",name);
       return userClient.sayHello(JSONObject.valueToString(map));
        //return JSONObject.fromObject(name).toString();
    }

}
