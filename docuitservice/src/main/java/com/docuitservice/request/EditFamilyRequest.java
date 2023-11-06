package com.docuitservice.request;

import lombok.Data;

@Data
public class EditFamilyRequest {

	private String name;
	private String familyId;
	private String adminId;

}
