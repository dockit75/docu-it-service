package com.docuitservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FamilyMemberInviteAcceptedRequest {

	private String userId;
	
	private String familyId;
	
	private String inviteStatus;

}
