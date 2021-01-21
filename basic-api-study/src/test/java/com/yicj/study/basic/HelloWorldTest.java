package com.yicj.study.basic;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

//https://www.jianshu.com/p/cd3557b1a474
@Slf4j
public class HelloWorldTest {

    @Test
    @DisplayName("简单测试RXJava")
    void test1(){
        //1. 创建被观察者
        Observable<String> novel = Observable.create(emitter -> {
            emitter.onNext("连载1");
            emitter.onNext("连载2");
            int a = 1/0 ;
            emitter.onNext("连载3");
            emitter.onComplete();
        });
        // 创建观察者
        Observer<String> reader = new Observer<String>() {
            Disposable mDisposable ;
            @Override
            public void onSubscribe(Disposable disposable) {
                mDisposable = disposable ;
                log.info("onSubscribe ...");
            }
            @Override
            public void onNext(String value) {
                if ("连载2".equals(value)){
                    mDisposable.dispose();
                }
                log.info("onNext : {}", value);
            }
            @Override
            public void onError(Throwable throwable) {
                log.info("onError : {}", throwable.getMessage());
            }
            @Override
            public void onComplete() {
                log.info("onComplete..");
            }
        } ;
        //3. 绑定订阅关系
        novel.subscribe(reader) ;
    }


    @Test
    @DisplayName("异步和链式编程")
    void test2(){


    }
}
