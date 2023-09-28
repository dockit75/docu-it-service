package com.docuitservice.response;

import java.util.Date;

import lombok.Data;

@Data
public class CategoryDetails {

	private String categoryId;
	private String categoryName;
	private String description;
	private Boolean status;
	private Date createdAt;
	private Date updatedAt;	
	
}
