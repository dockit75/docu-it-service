package com.docuitservice.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class SaveDocumentRequest {
	
	private String documentName;
	
	private String documentUrl;
	
	private String categoryId;
	
	private String familyId;
	
	private String uploadedBy;
	
	//private String documentType;
	
	private String documentSize;
	
	private List<String> sharedMembers;
	
	
}
