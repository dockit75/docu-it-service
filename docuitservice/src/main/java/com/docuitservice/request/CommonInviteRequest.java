package com.docuitservice.request;

import java.util.List;

import lombok.Data;

@Data
public class CommonInviteRequest {
	
	private List<String> phoneNumbers;
	
	private String familyId;
	
	private String invitedBy;
}
