package com.docuitservice.response;

import com.docuitservice.model.Family;
import com.docuitservice.model.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberResponse {

	private Family family;
    private Member member;
    
}
