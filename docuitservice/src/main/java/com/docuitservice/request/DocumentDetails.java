package com.docuitservice.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDetails {
	
	
	private String documentName;
	
	private String documentUrl;
		
	private String documentSize;
	
	private String documentType;

	private Integer pageCount;

}
