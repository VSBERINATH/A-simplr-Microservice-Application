package com.code.fullstack_backend.exception;

public class UserNotFoundException  extends  RuntimeException{

    public UserNotFoundException(Long id){
        super("could not found user ID:"+id);
    }
}
