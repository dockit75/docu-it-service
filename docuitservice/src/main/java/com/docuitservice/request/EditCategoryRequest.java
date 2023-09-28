package com.docuitservice.request;

import lombok.Data;

@Data
public class EditCategoryRequest {

	private String categoryId;
	private String categoryName;
	private String description;
	
}
