package com.docuitservice.response;

import java.util.Date;
import java.util.List;

import com.docuitservice.model.Member;

import lombok.Data;

@Data
public class FamilyDetails {
	
	private String id;
	private String name;
	private Boolean status;
	private String adminId;
	private Date createdAt;
	private Date updatedAt;
	private List<Member> member;

}
