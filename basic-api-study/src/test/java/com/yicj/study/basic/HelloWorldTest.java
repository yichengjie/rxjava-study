package com.yicj.study.basic;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
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
        Observable<Integer> observable = Observable.defer(() -> Observable.just(i)) ;
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

    /**
     * https://blog.csdn.net/Kongou/article/details/82629435
     * 自己如何使用RXJAVA的（网络请求封装）
     * 终于来到重点了。在理解了RXJAVA轻松切换线程的情况下，我们可以在上面大做文章了。网络请求的处理是我们开发过程中需要切换线程比较频繁的地方。
     * 通常耗时操作网络请求都会放在子线程中处理，而根据返回结果对UI进行的操作要放在主线程中进行。原始的方法就是在主线程中开启一个子线程，
     * 发起网络请求。之后在网络请求回调函数中在post到主线程。也有借助handler来实现的。通常这些方式实现之后，在读代码找关系逻辑的时候真的很绕，
     * 需要跳来跳去的。但是有了RXJAVA之后一切就不一样了，把请求和响应全在一条线上搞定。
     * 首先还是来回顾一下RXJAVA的工作流程。Observable创建，添加发送事件内容，选择上游线程，选择下游线程，以Observer为参数订阅，Observer决定响应处理。
     * 在这个流程中，其实我们大部分的操作已经可以实现了，选择上游线程就是新建一个子线程，选择下游线程就是回归到主线程，
     * Observer的响应事件就是在主线程对UI操作的事件。现在唯一的问题就是上游事件如何发送以及发送的时机。
     * 其实在这里最关键的封装就是把网络请求的回调方法放到上游事件发送里，把请求的响应结果作为信号发送，让下游处理。
     * （如果处理的内容比较多，操作复杂，可以处理完再发送。我们的原则是在UI线程里做较少的操作。）那么这一步是如何实现的呢。
     * 建造一个请求帮助类。这个帮助类提供一个发送请求方法，这个发送请求方法接收一个参数，这个参数是网络请求响应的监听接口。
     * 在上游调用请求方法，同时new一个监听接口作为参数传递给请求方法。这个监听接口实现的方法，其内容可以调用上游的发射器，
     * 将想要发送的内容在上游发送出去。
     * 简单点说，就是让网络请求接口的响应函数，在上游发送事件。这个操作是在上游子线程完成的。
     * 经过这样的封装，我们就实现了异步网络请求结果通知主线程操作。
     * 后续的处理都和平常一样了，值得注意的是由于现在都放在一串代码中执行了，所以很容易忽略两边的操作是处在不同线程的。
     * 建议在命名方法时，标注出此方法是在哪种线程中使用的，这样代码的逻辑更加清晰。
     */
}
