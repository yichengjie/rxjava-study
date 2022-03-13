package com.yicj.study;

import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class BaseOperatorTest {

    //concat可以做到不交错的发射两个或多个Observable的发射物，
    // 并且只有前一个Observable终止(onComplete)才会订阅下一个Observable
    @Test
    public void concat() throws InterruptedException {
        Observable<String> cache = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            emitter.onNext("hello");
            //emitter.onComplete();
        });
        //
        Observable<String> network = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            emitter.onNext("world");
            //一定要有onComplete，不然不会执行第二个Observale
            emitter.onComplete();
        });
        //
        Observable.concat(cache, network)
                .subscribe(elem -> {
                    log.info("elem : {}", elem);
                }, error -> {
                    log.error("error : ", error);
                });
        Thread.sleep(1000);
    }


    @Test
    public void asyncCallback() throws InterruptedException {
        // 创建数据源
        Observable<String> observable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            initAsyncCallback(val -> {
                emitter.onNext(val);
                emitter.onComplete();
            });
        });

        // 订阅
        observable.subscribe(elem -> {
            log.info("elem : {}", elem) ;
        },error -> {
            log.error("error : ", error);
        }) ;
        Thread.sleep(3000);
    }

    @Test
    public void asyncObservable() throws InterruptedException {
        log.info("start ...");
        Observable<String> observable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            Observable<String> stringObservable = this.initAsyncObservable();
            stringObservable.subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
        });

        // 订阅
        observable.subscribe(elem -> {
            log.info("elem : {}", elem) ;
        },error -> {
            log.error("error : ", error);
        }) ;
        //Thread.sleep(3000);
        log.info("end ...");
    }

    // 异步执行
    public void initAsyncCallback(Consumer<String> consumer){
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumer.accept("hello world");
        });
        // 启动线程
        thread.start();
    }


    public Observable<String> initAsyncObservable(){
       return Observable.create((ObservableOnSubscribe<String>) emitter -> {
           Thread thread = new Thread(() -> {
               try {
                   Thread.sleep(2000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               emitter.onNext("hello world");
               emitter.onComplete();
           });
           //
           thread.start();
       }) ;
    }



}
