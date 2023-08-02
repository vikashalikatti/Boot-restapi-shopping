package org.jsp.shopping.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NoZeroException extends Exception {
	
	String message="Should not be zero";
	
	@Override
	public String getMessage() {
		return message;
	}
}
