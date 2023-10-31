package com.docuitservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.request.SaveDocumentRequest;
import com.docuitservice.request.ShareDocumentRequest;
import com.docuitservice.response.UploadResponse;
import com.docuitservice.service.DocumentService;
import com.docuitservice.util.Response;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/document")
public class DocumentController {
	
	@Autowired
	DocumentService documentService;
	
	@RequestMapping(value = "/saveDocument", method = RequestMethod.POST)
	public Response saveDocument(@RequestBody @Valid SaveDocumentRequest saveDocumentRequest) throws Exception {
		Response documentResponse = documentService.saveDocumentDetails(saveDocumentRequest);
		 return documentResponse;
	}
	
	@RequestMapping(value = "/uploadDocument",  method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public UploadResponse uploadDocument(@RequestParam("file") MultipartFile file,@RequestParam String userId) throws Exception {
		UploadResponse response  = documentService.uploadDocument(file,userId);
		 return response;
	}
	
	/* @RequestMapping(value = "/shareDocument", method = RequestMethod.POST)
	public Response shareDocument(@RequestBody @Valid ShareDocumentRequest shareDocumentRequest) throws Exception {
		return documentService.shareDocument(shareDocumentRequest);
	}
	@RequestMapping(value = "/revokeDocumentAccess", method = RequestMethod.DELETE)
	public Response revokeDocumentAccess(@RequestBody @Valid ShareDocumentRequest shareDocumentRequest) throws Exception {
		return documentService.shareDocument(shareDocumentRequest);
	}
	
	@RequestMapping(value = "/getDocumentSharedList", method = RequestMethod.GET)
	public Response getDocumentShared(@RequestParam String documentId) throws Exception {
		return documentService.getDocumentShared(documentId);
	}*/
	
	@RequestMapping(value = "/getDocumentDetails", method = RequestMethod.GET)
	public Response getDocumentDetails(@RequestParam String documentId) throws Exception {
		return documentService.getDocumentDetails(documentId);
	}
	@RequestMapping(value = "/getUserDocumentList", method = RequestMethod.GET)
	public Response getUserDocumentList(@RequestParam String userId) throws Exception {
		return documentService.getDocumentList(userId);
	}
	
	@RequestMapping(value = "/deleteDocument", method = RequestMethod.PUT)
	public Response deleteDocument(@RequestParam String documentId) throws Exception {
		return documentService.deleteDocument(documentId);
	}
	
	@RequestMapping(value = "/getUserLastDocumentActivity", method = RequestMethod.GET)
	public Response getUserLastDocumentActivity(@RequestParam String userId) throws Exception {
		return documentService.getUserLastDocumentActivity(userId);
	}
	
	/*@RequestMapping(value = "/editDocumentCategory", method = RequestMethod.PUT)
	public Response updateDocumentCategory(@RequestBody @Valid ShareDocumentRequest shareDocumentRequest) throws Exception {
		return documentService.updateDocumentCategory(shareDocumentRequest);
	}*/
	
	@RequestMapping(value = "/updateDocument", method = RequestMethod.PUT)
	public Response updateDocument(@RequestBody @Valid ShareDocumentRequest shareDocumentRequest) throws Exception {
		return documentService.shareDocument(shareDocumentRequest);
	}
	

}
