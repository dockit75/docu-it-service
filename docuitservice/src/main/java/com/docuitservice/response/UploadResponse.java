package com.docuitservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
	
	 	private String fileName;
	    private String documentUrl;
	    private String fileType;
	    private long size;

	   /* public UploadResponse(String fileName, String documentUrl, String fileType, long size) {
	        this.fileName = fileName;
	        this.documentUrl = documentUrl;
	        this.fileType = fileType;
	        this.size = size;
	    }*/

}
