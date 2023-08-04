package org.jsp.shopping.helper;

import java.util.List;

import org.jsp.shopping.dto.Product;

import lombok.Data;

@Data
public class ResponseStructure<T> {
	int status;
	String message;
	T data;
}
