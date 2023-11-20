package org.jsp.shopping.helper;

import lombok.Data;

@Data
public class ResponseStructure<T> {
	int status;
	String message;
	T data;
	String data2;
}
