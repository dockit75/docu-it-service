package com.docuitservice.response;

import lombok.Data;

@Data
public class CategoryResponse {

	private String categoryId;
	private String categoryName;
	private Boolean status;
	private long fileCount;

}
