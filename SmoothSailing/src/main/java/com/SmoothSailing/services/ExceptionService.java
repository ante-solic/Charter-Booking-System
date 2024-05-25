package com.SmoothSailing.services;

public class ExceptionService extends RuntimeException{
    public ExceptionService(String message){
        super(message);
    }
}
