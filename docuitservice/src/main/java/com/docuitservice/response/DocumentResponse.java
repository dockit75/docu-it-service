package com.docuitservice.response;

import java.util.List;

import com.docuitservice.model.Share;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
	
	private String documentId;
	
	private String documentName;
	
	private String categoryName;
	
	private String uploadedBy;
	
	private String documentUrl;
	
	private String familyId;
	
	private String familyName;
	
	private String createdDate;
	
	private String updatedDate;

	private Integer pageCount;

	private Long documentSize;

}
