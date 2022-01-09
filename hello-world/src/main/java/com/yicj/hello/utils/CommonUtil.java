package com.yicj.hello.utils;

public class CommonUtil {
    public static void log(Object msg){
        System.out.println(
            Thread.currentThread().getName() +" : " + msg
        );
    }
}
