package com.uiineed.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 应用程序主入口类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}