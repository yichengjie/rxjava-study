package com.yicj.study;

import com.yicj.hello.utils.CommonUtil;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Hello2Test {


    @Test
    void poolPushCollectionDanger(){
        List<Integer> integers = new ArrayList<>() ;
        integers.add(1);
        integers.add(2);
        integers.add(3);
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool() ;
        pushCollectionDanger(integers, forkJoinPool).subscribe(x -> {
            CommonUtil.log(x  +" 我是订阅者 1 ") ;
            sleep(1, TimeUnit.SECONDS);
        }) ;
        sleep(4, TimeUnit.SECONDS);
        /////////////
        pushCollectionDanger(integers, forkJoinPool).subscribe(x -> {
            CommonUtil.log(x  +" 我是订阅者 2 ") ;
            sleep(1, TimeUnit.SECONDS);
        }) ;
        sleep(10, TimeUnit.SECONDS);
    }

    @Test
    void test1(){
        ForkJoinPool pool = ForkJoinPool.commonPool() ;
        pool.submit(() ->{
            sleep(1, TimeUnit.SECONDS);
            System.out.println("test1");
        }) ;

        pool.shutdownNow() ;

        sleep(1, TimeUnit.SECONDS);

        pool.submit(() ->{
            sleep(2, TimeUnit.SECONDS);
            System.out.println("test2");
        }) ;

        pool.shutdownNow() ;
    }

    public static Observable<Integer> pushCollectionDanger(List<Integer> ids, ForkJoinPool commonPool){
        return Observable.create(observer ->{
            AtomicInteger atomicInteger = new AtomicInteger(ids.size()) ;
            ids.forEach(id -> commonPool.submit(()->{
                observer.onNext(id);
                if (atomicInteger.decrementAndGet() == 0){
                    commonPool.shutdownNow();
                    observer.onComplete();
                    System.out.println("关闭....");
                }
            }));
        }) ;
    }

    static void sleep(int timeout, TimeUnit unit){
        try {
            unit.sleep(timeout);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
