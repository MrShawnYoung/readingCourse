package com.juvenxu.mvnbook.account.service;

public class AccountServiceException extends Exception {
	public AccountServiceException(String message) {
		super(message);
	}

	public AccountServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}
}