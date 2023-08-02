package org.jsp.shopping.exception;

import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MainExceptionHandler {
@ExceptionHandler(ArithmeticException.class)
public ResponseEntity<ResponseStructure<String>> handle(ArithmeticException exception)
{
	ResponseStructure<String> structure=new ResponseStructure<>();
	structure.setStatus(HttpStatus.BAD_REQUEST.value());
	structure.setMessage("There is an Arithmetic exception");
	structure.setData(exception.getMessage());
	
	return new ResponseEntity<ResponseStructure<String>>(structure,HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(NoZeroException.class)
public ResponseEntity<ResponseStructure<String>> handle(NoZeroException exception)
{
	ResponseStructure<String> structure=new ResponseStructure<>();
	structure.setStatus(HttpStatus.BAD_REQUEST.value());
	structure.setMessage("There is an NoZero exception");
	structure.setData(exception.getMessage());
	
	return new ResponseEntity<ResponseStructure<String>>(structure,HttpStatus.BAD_REQUEST);
}
}
