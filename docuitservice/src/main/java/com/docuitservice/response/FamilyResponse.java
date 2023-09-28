package com.docuitservice.response;

import java.util.Date;

import lombok.Data;

@Data
public class FamilyResponse {
	
	private String id;
	private String name;
	private Boolean status;
	private String adminId;
	private Date createdAt;
	private Date updatedAt;

}
