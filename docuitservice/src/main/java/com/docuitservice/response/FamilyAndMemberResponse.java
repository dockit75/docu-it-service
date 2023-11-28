package com.docuitservice.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyAndMemberResponse {

	private String id;
	private String name;
	private Boolean status;		
	private String createdBy;
	private List<MemberResponse> membersList;
    
}
