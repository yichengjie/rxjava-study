package com.yicj.study.basic;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class HelloSingleTest {

    @Test
    @DisplayName("create操作符")
    void create(){
        // 作用同Single.just(addValue(1, 2));
        Single.create((SingleOnSubscribe<Integer>) emitter -> {
            // 这里被指定在IO线程
            log.info("single input : {}", 3);
            emitter.onSuccess(3);
        }).subscribeOn(Schedulers.io())// 指定运行在IO线程
        .subscribe(new SingleObserver<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {
                log.info("onSubscribe : {}", d);
            }
            @Override
            public void onSuccess(Integer value) {
                log.info("onSuccess : {}", value);
            }
            @Override
            public void onError(Throwable e) {
                log.info("onError: {}", e.getMessage());
            }
        } ) ;
    }


    @Test
    @DisplayName("map操作符")
    void map(){
        Single.just(1)
            .map(integer -> "x")
            .subscribe(val ->{
                log.info("value : {}", val);
            });
    }

    @Test
    @DisplayName("flatMap操作符")
    void flatMap(){
        Single.just(1).flatMap(new Function<Integer, SingleSource<String>>() {
            @Override
            public SingleSource<String> apply(Integer value) throws Exception {
                return Single.just(value +"");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String value) throws Exception {
                // 这里返回的是String类型
                log.info("_flatMap : {}", value); // 注意这里返回值的区别
            }
        }) ;

        Single.just(1).map(new Function<Integer, Single<String>>() {
            @Override
            public Single<String> apply(Integer integer) throws Exception {
                return Single.just("" + integer);
            }
        }).subscribe(new Consumer<Single<String>>() {
            @Override
            public void accept(Single<String> stringSingle) throws Exception {
                // 这里返回的是Single<String>类型
                log.info("_map : {}", stringSingle);  // 注意这里返回值的区别
            }
        }) ;
    }


    @Test
    @DisplayName("flatMapObservable操作符")
    void flatMapObservable(){
        Single.just(1).flatMapObservable(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just("H", "3", "c");
            }
        }).subscribe(s -> {
            log.info("value : {}" , s);
        }) ;
    }

    @Test
    @DisplayName("subscribeOn操作符")
    void subscribeOn(){
        //Schedulers.computation();// 计算线程
        //Schedulers.from(executor);// 自定义
        //Schedulers.immediate();// 当前线程
        //Schedulers.io();// io线程
        //Schedulers.newThread();// 创建新线程
        //Schedulers.trampoline();// 当前线程队列执行
    }

    @Test
    @DisplayName("onErrorReturn操作符")
    void onErrorReturn(){
        //当函数抛出错误的时候给出一个返回值
        Single.create(new SingleOnSubscribe<Integer>(){
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                emitter.onError(new Throwable(" x error test"));
            }
          }).onErrorReturn(throwable -> 2)
          .subscribe(val ->{
              log.info("value : {}", val);
          }) ;
    }

    @Test
    @DisplayName("timeout操作符")
    void timeout(){
        //超时操作操作，在指定时间内如果没有调用onSuccess()就判定为失败，且可支持失败的时候调用其他Single()
    }

    @Test
    void toSingle(){
        // api已经不可用
        //Observable.just(1).toSingle();
    }


    @Test
    @DisplayName("zip & zipWith")
    void zip(){
        Single<Integer> s1 = Single.just(1);
        Single<Integer> s2 = Single.just(2);
        Single.zip(s1, s2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return integer +", " + integer2 +", zip" ;
            }
        }).subscribe(val ->{
            log.info("value : {}", val);
        }) ;
    }
}
