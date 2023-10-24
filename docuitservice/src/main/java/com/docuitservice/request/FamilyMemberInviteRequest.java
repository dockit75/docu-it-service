package com.docuitservice.request;

import java.util.List;

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
public class FamilyMemberInviteRequest {

	
	public List<String> userIds;
	
	public String familyId;
	
	public String invitedBy;
	
}
