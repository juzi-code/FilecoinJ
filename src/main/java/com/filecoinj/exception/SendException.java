package com.filecoinj.exception;

public class SendException extends Exception {
    public SendException(){
        super();
    }

    public SendException(String message){
        super(message);
    }
}
