package com.yicj.study.basic;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//https://www.cnblogs.com/lanjiabin/p/13261608.html
@Slf4j
public class HelloWorldTest {

    @Test
    @DisplayName("基础发送")
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
    @DisplayName("集合发送")
    void fromIterable(){
        List<Integer> list = Arrays.asList(1,2,3) ;
        Observable.fromIterable(list).subscribe(new MyLatchLogObservable<>(null)) ;
    }

    @Test
    @DisplayName("仅用于测试使用")
    void basicApi(){
        Observer observer = new MyLatchLogObservable(null) ;
        // empty
        Observable observable1= Observable.empty() ;
        observable1.subscribe(observer) ;
        log.info("-------------------------------");
        // error
        Observable observable2 = Observable.error(new RuntimeException("测试异常")) ;
        observable2.subscribe(observer) ;
        log.info("-------------------------------");
        // never
        // 该方法创建得被观察对象不发送事件的特点，不发送任何事件
        Observable observable3 = Observable.never() ;
        observable3.subscribe(observer) ;
        log.info("-------------------------------");
    }


    @Test
    @DisplayName("立刻发送10以下")
    void just(){
        Observable.just(1,2,3,4,5,6,7).subscribe(new MyLatchLogObservable<>(null)) ;
    }




    private Integer i =10 ;

    @Test
    @DisplayName("获取最新数据")
    void defer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1) ;
        // 第一次赋值
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        }) ;
        // 第二次赋值
        i = 15 ;
        observable.subscribe(new MyLatchLogObservable<>(latch)) ;
        latch.await();
    }

    @Test
    @DisplayName("延迟发送")
    void timer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2) ;
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new MyLatchLogObservable<>(latch)) ;
        latch.await();
    }

    @Test
    @DisplayName("周期发送，无限")
    void interval() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(6) ;
        /**
         * 从0开始递增
         * 初始延迟时间（第一次延迟时间）
         * 续数字发射之间的时间间隔（一个周期时间）
         * 时间单位
         */
        Observable.interval(3,2,TimeUnit.SECONDS)
                .subscribe(new MyLatchLogObservable<>(latch)) ;
        latch.await();
    }

    @Test
    @DisplayName("周期发送，有限，指定数据")
    void intervalRange() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(11) ;
        /**
         * @param start 起始值
         * @param count 总共要发送的值的数量，如果为零，则运算符将在初始延迟后发出onComplete
         * @param initialDelay 发出第一个值（开始）之前的初始延迟
         * @param period 后续值之间的时间段
         * @param unit 时间单位
         * */
        Observable.intervalRange(3,10,2,1, TimeUnit.SECONDS)
                .subscribe(new MyLatchLogObservable<>(latch)) ;
        latch.await();
    }

    @Test
    @DisplayName("无延迟，Integer类型指定数据")
    void range() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(6) ;
        /**
         * @param start序列中第一个Integer的值
         * @param count 要生成的顺序整数的数量
         * */
        Observable.range(3, 5).subscribe(new MyLatchLogObservable<>(latch)) ;
        latch.await();
    }


    @Test
    @DisplayName("无延迟，Long类型指定数据")
    void rangeLong() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(9) ;
        /**
         * @param start Long类型，序列中第一个Integer的值
         * @param count Long类型，要生成的顺序整数的数量
         * */
         Observable.rangeLong(3, 8).subscribe(new MyLatchLogObservable<>(latch)) ;
         latch.await();
    }

    class MyLatchLogObservable <T> implements Observer<T>{
        private CountDownLatch latch ;
        public MyLatchLogObservable(CountDownLatch latch){
            this.latch = latch ;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            log.info("开始采用subscribe连接...");
        }

        @Override
        public void onNext(T value) {
            log.info("接受事件 onNext : {}" ,value);
            if (latch != null){
                latch.countDown();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            log.info("接受事件 onError：{}", throwable.getMessage());
        }

        @Override
        public void onComplete() {
            log.info("接受事件 onComplete");
            if (latch!= null){
                latch.countDown();
            }
        }
    }
}
