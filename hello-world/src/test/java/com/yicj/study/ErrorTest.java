package com.yicj.study;

import com.yicj.hello.utils.CommonUtil;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

public class ErrorTest {

    static void errorTest(int n){
        if (n == 5) throw new RuntimeException("我就是喜欢来搞惊喜") ;
        System.out.println("我消费的元素是 --> " + n);
    }

    static void observableErrorTest(int n){
        Observable.create(observer -> {
            try {
                observer.onNext(n);
                observer.onComplete();
            }catch (Exception e){
                observer.onError(e);
            }
        }).subscribe(
            x -> errorTest((int)x),
            Throwable::printStackTrace,
            ()-> System.out.println("Emission completed")
        ) ;
    }

    @Test
    void observableErrorTestAcc(){
        observableErrorTest(1);
        observableErrorTest(5);
    }


    static Integer errorTestP(int n){
        if (n == 5) throw new RuntimeException("我就是喜欢来搞惊喜") ;
        System.out.println("我消费的元素是---> " + n);
        return n +5 ;
    }

    static Observable<Integer> errorTestPro(int n){
        return Observable.fromCallable(() -> errorTestP(n)) ;
    }

    @Test
    void fromCallableTest(){
        errorTestPro(1).subscribe(
            x -> CommonUtil.log(x),
            Throwable::printStackTrace,
            ()-> System.out.println("Emission completed")
        ) ;
        errorTestPro(5).subscribe(
                x -> CommonUtil.log(x),
                Throwable::printStackTrace,
                ()-> System.out.println("Emission completed")
        ) ;
    }


}
