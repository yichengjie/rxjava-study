package com.yicj.study.basic;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;

@Slf4j
public class FlowableTest {

    @Test
    @DisplayName("被压测试")
    void create() throws IOException {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for(int i=0;i<10000;i++){
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR) //指定背压处理策略，抛出异常
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.newThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                log.info("info : {}", integer.toString());
                Thread.sleep(1000);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                log.error("error : {}",throwable.toString());
                System.exit(0);
            }
        });
        System.in.read() ;
    }


    @Test
    @DisplayName("request后才开始发送数据")
    void test2(){
        //Flowable并不是订阅就开始发送数据，而是需等到执行Subscription.request()才能开始发送数据
        Flowable.range(1,10).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);//设置请求数
            }

            @Override
            public void onNext(Integer integer) {
                log.info("value : {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.error("onError : {}" ,t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("onComplete ...");
            }
        });
    }

}
