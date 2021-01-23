package com.yicj.study.mvc.controller;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/single")
    public Single<String> single(){
        return Single.just("single vlaue") ;
    }


    @GetMapping("/singleWithResponse")
    public ResponseEntity<Single<String>> singleWithResponse(){
        return new ResponseEntity<>(Single.just("single value with response"), HttpStatus.NOT_FOUND) ;
    }

    @GetMapping("/throw")
    public Single<Object> error(){
        return Single.error(new RuntimeException("unexpected ...")) ;
    }


    @GetMapping("/single2")
    public Single<String> single2(){
        return Observable.just("single value from observable ").first("default value") ;
    }
}
