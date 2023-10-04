package com.docuitservice.request;

import lombok.Data;

@Data
public class ExternalInviteAcceptRequest {
	
	private String externalInviteId;
	
	private String userId;
}
