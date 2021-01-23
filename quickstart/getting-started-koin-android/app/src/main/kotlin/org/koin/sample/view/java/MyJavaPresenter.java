package org.koin.sample.view.java;

import org.koin.sample.HelloRepository;

public class MyJavaPresenter {

    private HelloRepository repository;

    public MyJavaPresenter(HelloRepository repository) {
        this.repository = repository;
    }

    public String sayHello(){
        String hello = repository.giveHello();

        return hello+" from MyJavaPresenter";
    }

}
