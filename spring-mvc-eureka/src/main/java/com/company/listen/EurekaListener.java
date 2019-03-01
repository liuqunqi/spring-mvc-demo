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
package com.company.listen;

import com.company.util.InetUtils;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * @author lvzhen
 * @version Id: EurekaListener.java, v 0.1 2019/2/25 17:20 lvzhen Exp $$
 */
public class EurekaListener implements ServletContextListener, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(EurekaListener.class);

    private ApplicationInfoManager applicationInfoManager;
    private EurekaClient eurekaClient;
    private DynamicPropertyFactory configInstance = DynamicPropertyFactory.getInstance();


    /**
     * 初始化EurekaClient
     */
    public void initEurekaClient() {
        Properties properties =new Properties();

        //EurekaInstanceConfig 应用实例配置接口
        //(重在应用实例，例如，应用名、应用的端口等等。此处应用指的是，Application Consumer 和 Application Provider)
        EurekaInstanceConfig instanceConfig = new MyDataCenterInstanceConfigExt();
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        //EurekaClientConfig
        // 重在 Eureka-Client，例如， 连接的 Eureka-Server 的地址、获取服务提供者列表的频率、注册自身为服务提供者的频率等等。
        eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
    }


    public void start() {
        // A good practice is to register as STARTING and only change status to UP
        // after the service is ready to receive traffic
        logger.info("Registering service to eureka with STARTING status");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);
        logger.info("Simulating service initialization by sleeping for 2 seconds...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Nothing
        }

        // Now we change our status to UP
        logger.info("Done sleeping, now changing status to UP");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        waitForRegistrationWithEureka(eurekaClient);
        logger.info("Service started and ready to process requests..");

       /* try {
            int myServingPort = applicationInfoManager.getInfo().getPort();  // read from my registered info
            ServerSocket serverSocket = new ServerSocket(myServingPort);
            final Socket s = serverSocket.accept();
            logger.info("Client got connected... processing request from the client" + myServingPort);
            processRequest(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Simulating service doing work by sleeping for " + 5 + " seconds...");
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            // Nothing
        }*/
    }


    public void stop() {
        if (eurekaClient != null) {
            logger.info("Shutting down server. Demo over.");
            eurekaClient.shutdown();
        }
    }

    /**
     * Application Service 的 Eureka Server 初始化以及注册是异步的，需要一段时间 此处等待初始化及注册成功
     *
     * @param eurekaClient
     */
    private void waitForRegistrationWithEureka(EurekaClient eurekaClient) {
        // my vip address to listen on
        String vipAddress = configInstance.getStringProperty("eureka.vipAddress", "sampleservice.mydomain.net").get();
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
            } catch (Throwable e) {
                logger.info("Waiting ... verifying service registration with eureka ...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void processRequest(final Socket s) {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = rd.readLine();
            if (line != null) {
                logger.info("Received a request from the example client: " + line);
            }
            String response = "BAR " + new Date();
            logger.info("Sending the response to the client: " + response);

            PrintStream out = new PrintStream(s.getOutputStream());
            out.println(response);

        } catch (Throwable e) {
            System.err.println("Error processing requests");
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Value("${eureka.port}")
    String port;
    @Value("${eureka.name}")
    String applicaitonName;

    private static final String UNKNOWN = "unknown";
    private Environment environment;
    private String appname = UNKNOWN;
    private String virtualHostName = UNKNOWN;
    private String secureVirtualHostName = UNKNOWN;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getVirtualHostName() {
        return virtualHostName;
    }

    public void setVirtualHostName(String virtualHostName) {
        this.virtualHostName = virtualHostName;
    }

    public String getSecureVirtualHostName() {
        return secureVirtualHostName;
    }

    public void setSecureVirtualHostName(String secureVirtualHostName) {
        this.secureVirtualHostName = secureVirtualHostName;
    }

    @Override
    public void setEnvironment(Environment environment) {
        String springAppName = this.environment.getProperty("spring.application.name","");
        if (StringUtils.hasText(springAppName)) {
            setAppname(springAppName);
            setVirtualHostName(springAppName);
            setSecureVirtualHostName(springAppName);
        }
    }

    /**
     * 拓展配置信息
     */
    class MyDataCenterInstanceConfigExt extends MyDataCenterInstanceConfig {
        /**
         * 让注册到服务的名称是机器的ip ，非主机名
         **/
        @Override
        public String getHostName(boolean refresh) {
            try {
                // return InetAddress.getLocalHost().getHostAddress();
                return InetUtils.getLocalIpAddr();
            } catch (Exception e) {
                return super.getHostName(refresh);
            }
        }

        /**
         * 防止虚拟网卡获取本地IP问题
         **/
        @Override
        public String getIpAddress() {
            try {
                return InetUtils.getLocalIpAddr();
            } catch (Exception e) {
                return super.getIpAddress();
            }
        }

        /**
         * 获取本机启动中tomcat/Jetty端口号
         **/
        @Override
        public int getNonSecurePort() {
            int tomcatPort = 0;
            try {
                MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
                Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                        Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
                tomcatPort = Integer.valueOf(objectNames.iterator().next().getKeyProperty("port"));
                if (!StringUtils.isEmpty(System.getProperty("jetty.port"))) {
                    tomcatPort = Integer.parseInt(System.getProperty("jetty.port"));
                }
            } catch (Exception e) {
                return super.getNonSecurePort();
            }
            return tomcatPort;
        }

        /**
         * 获取应用名
         **/
        @Override
        public String getAppname() {
            try {
                if (!StringUtils.isEmpty(applicaitonName)) {
                    return getAppname();
                } else {
                    return super.getAppname();
                }
            } catch (Exception e) {
                return super.getAppname();
            }
        }

        @Override
        public String getInstanceId() {
            try {
                //${spring.cloud.client.ipAddress}:${server.port}/${eureka.name}
                return getIpAddress()+":"+getAppname()+":"+getNonSecurePort();
            } catch (Exception e) {
                return super.getInstanceId();
            }
        }
    }


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        initEurekaClient();
        start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        stop();
    }

}