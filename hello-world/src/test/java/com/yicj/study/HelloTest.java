package com.yicj.study;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class HelloTest {

    @Test
    @DisplayName("测试range")
    void range(){
        log("Range test before");
        Observable.range(5,3)
                .subscribe(HelloTest::log);
        log("Range test after");
    }

    @Test
    @DisplayName("测试just函数")
    void just(){
        log("Just test before") ;
        Observable.just("Jan", "Feb","Mar","Apl","May","Jun")
                .subscribe(HelloTest::log) ;
        log("Just test after");
    }


    @Test
    void just1(){
        log("Just test before") ;
        HelloTest.just("Hello")
                .subscribe(HelloTest::log) ;
        log("Just test after");
    }

    @Test
    void host(){
        Observable<Object> observable = Observable.create(observer -> {
            observer.onNext("处理的数字是: " + Math.random()  * 100);
            observer.onComplete();
        }).cache() ;
        observable.subscribe(consumer -> System.out.println("我处理的元素是: " + consumer)) ;
        observable.subscribe(consumer -> System.out.println("我处理的元素是: " + consumer)) ;
    }

    @Test
    void infinite(){
        Observable<Object> observable = Observable.create(observer -> {
            BigInteger i = BigInteger.ONE;
            while (true) {
                observer.onNext(i);
                i = i.add(BigInteger.ONE);
            }
        });
        observable.subscribe(x -> log(x)) ;
    }

    @Test
    void infiniteThread(){
        Observable<Object> observable = Observable.create(observer -> {
            Runnable runnable = () -> {
                BigInteger i = BigInteger.ONE;
                while (true) {
                    observer.onNext(i);
                    i = i.add(BigInteger.ONE);
                }
            };
            new Thread(runnable).start();
        });
        observable.subscribe(x -> log(x)) ;
        observable.subscribe(x -> log(x)) ;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void infiniteUnsubscribedThread(){

        Observable<Object> observable = Observable.create(observer -> {
            Runnable runnable = () -> {
                BigInteger i = BigInteger.ONE;
                while (!observer.isDisposed()) {
                    observer.onNext(i);
                    i = i.add(BigInteger.ONE);
                    System.out.println(Thread.currentThread().getName() + " 下一个消费的数字 " + i);
                }
            };
            new Thread(runnable).start();
        });

        Disposable disposable1 = observable.subscribe(x -> log(x));
        Disposable disposable2 = observable.subscribe(x -> log(x));

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        disposable1.dispose();
        disposable2.dispose();
        System.out.println("我取消订阅了 ");
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("程序结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static <T> Observable<T> just(T item){
        ObjectHelper.requireNonNull(item, "The item is null") ;
        return Observable.create(emitter ->{
            emitter.onNext(item) ;
            emitter.onComplete();
        }) ;
    }


    private static void log(Object msg){
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println(
                Thread.currentThread().getName() +" : " + msg
        );
    }
}
