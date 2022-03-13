package com.yicj.study;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class BaseOperatorTest {

    //concat可以做到不交错的发射两个或多个Observable的发射物，
    // 并且只有前一个Observable终止(onComplete)才会订阅下一个Observable
    @Test
    public void hello() throws InterruptedException {
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

}
