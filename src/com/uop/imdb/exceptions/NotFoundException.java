package com.uop.imdb.exceptions;

//when requested data don't exist
public class NotFoundException extends Exception {
    public NotFoundException(String msg) { super(msg); }
}