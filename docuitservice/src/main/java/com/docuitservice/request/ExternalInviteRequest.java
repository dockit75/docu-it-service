package com.docuitservice.request;

import lombok.Data;

@Data
public class ExternalInviteRequest {
	
	private String email;
	
	private String phone;
	
	private String familyId;
	
	private String invitedBy;


}
