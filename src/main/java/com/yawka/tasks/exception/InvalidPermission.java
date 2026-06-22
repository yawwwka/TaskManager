package com.yawka.tasks.exception;

public class InvalidPermission extends RuntimeException {
    public InvalidPermission(String message) {
        super(message);
    }
}
