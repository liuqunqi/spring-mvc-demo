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
package com.company;

import com.company.annotation.MyContract;
import com.company.clients.HelloClient;
import feign.Feign;
import feign.Feign.Builder;
import feign.jackson.JacksonEncoder;

/**
 * @author lvzhen
 * @version Id: Tests.java, v 0.1 2019/2/27 9:21 lvzhen Exp $$
 */
public class Tests {

    public static void main(String[] args) {
        Builder build = Feign.builder().encoder(new JacksonEncoder()).contract(new MyContract());
        HelloClient helloClient = build.target(HelloClient.class, "http://localhost:8762");
       /* PersonClient client = build.target(PersonClient.class, "http://localhost:8762");
        Person person = new Person();
        person.setSex("1");
        person.setName("angels");
        String result = client.savePerson(person);
        System.out.println(result);*/
       // String result2 = userClient.hello("{" + "\"name\":\"Ailensss\"" + "}");
        String result2 = helloClient.sayHello("Ailensss");
        System.out.println(result2);
    }
}