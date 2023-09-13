package com.docuitservice.service;

import org.springframework.web.multipart.MultipartFile;
import com.docuitservice.request.SaveDocumentRequest;
import com.docuitservice.request.ShareDocumentRequest;
import com.docuitservice.response.UploadResponse;
import com.docuitservice.util.Response;

public interface DocumentService {
	
	public UploadResponse uploadDocument(MultipartFile file,String userId) throws Exception;
	
	public Response saveDocumentDetails(SaveDocumentRequest saveDocumentRequest) throws Exception;
	
	public Response shareDocument(ShareDocumentRequest shareDocumentRequest) throws Exception;
		
	public Response getDocumentShared(String documentId) throws Exception;
	
	public Response getDocumentDetails(String documentId) throws Exception;

	public Response getDocumentList(String userId);

	public Response deleteDocument(String documentId);

}
