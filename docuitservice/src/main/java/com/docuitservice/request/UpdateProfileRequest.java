package com.docuitservice.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {

	private String userId;
	private String name;
	private String gender;
	private String imageUrl;

}
