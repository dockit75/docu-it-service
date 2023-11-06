package com.docuitservice.request;

import java.util.List;

import lombok.Data;

@Data
public class MemberOperationRequest {
	
	private List<String> memberIds;

}
