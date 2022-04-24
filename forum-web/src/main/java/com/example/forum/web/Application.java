package com.example.forum.web;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * <pre>
 *     Forum run!
 * </pre>
 *
 * @author : saysky
 * @date 2022/4/16
 */
@Slf4j
@SpringBootApplication()
@EnableCaching
@MapperScan("com.example.forum.core.*.mapper")
@ComponentScan("com.example.forum")
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        String serverPort = context.getEnvironment().getProperty("server.port");
        log.info("Forum started at http://localhost:" + serverPort);
    }

}
