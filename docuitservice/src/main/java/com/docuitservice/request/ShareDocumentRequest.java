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
public class ShareDocumentRequest {
		
	private List<String> memberIds;
	
	private String familyId;
	
	private String documentId;
	
	private String categoryId;
	
	private String action;
	
	private String updatedBy;

}
