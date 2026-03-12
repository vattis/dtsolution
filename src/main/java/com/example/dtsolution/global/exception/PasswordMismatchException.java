package com.example.dtsolution.global.exception;

public class PasswordMismatchException extends RuntimeException {

  public PasswordMismatchException(String message) {
    super(message);
  }
}
