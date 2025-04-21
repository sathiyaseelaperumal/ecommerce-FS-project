package com.supertiles.ecommerce.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class MyExceptionController {
	
	@ExceptionHandler(NoResourceFoundException.class)
	public String handle() {
		return "error.html";
	}
	
	@ExceptionHandler(exception = Exception.class)
	public String handle1() {
		return "error.html";
	}

	

}
