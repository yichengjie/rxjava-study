package com.yicj.study.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.yicj.study.servlet.servlet")
public class MyServletApplication {

    //https://blog.csdn.net/zcl_love_wx/article/details/52075428
    public static void main(String[] args) {
        SpringApplication.run(MyServletApplication.class, args) ;
    }
}
