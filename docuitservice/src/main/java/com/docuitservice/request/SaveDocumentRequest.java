package com.docuitservice.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SaveDocumentRequest {
	
	private List<DocumentDetails> documentDetails;
	
	private String categoryId;
	
	private String familyId;
	
	private String uploadedBy;
		
	private List<String> sharedMembers;
	
	
}
