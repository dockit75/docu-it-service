package com.docuitservice.response;

import com.docuitservice.model.User;
import lombok.Data;

@Data
public class MemberResponse {

	private String id;
	private String inviteStatus;
	private Boolean status;	
	private User user;

}
