package com.docuitservice.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.docuitservice.model.Category;
import com.docuitservice.model.Document;
import com.docuitservice.model.Family;
import com.docuitservice.model.Member;
import com.docuitservice.response.CategoryDetails;
import com.docuitservice.response.CategoryResponse;
import com.docuitservice.response.DocumentResponse;
import com.docuitservice.response.FamilyDetails;
import com.docuitservice.util.Response;

public class ResponseHelper {

	private ResponseHelper() {

	}

	public static Response getErrorResponse(String message, Object data, Integer code, String status) {
		Response response = new Response();
		response.setMessage(message);
		response.setResponse(data);
		response.setCode(code);
		response.setStatus(status);
		return response;
	}

	public static Response getSuccessResponse(String message, Object data, Integer code, String status) {
		Response response = new Response();
		response.setMessage(message);
		response.setResponse(data);
		response.setCode(code);
		response.setStatus(status);
		return response;
	}

	public static CategoryDetails setCategoryResponseVO(Category category) {
		CategoryDetails categoryDetails = new CategoryDetails();
		categoryDetails.setCategoryId(category.getId());
		categoryDetails.setCategoryName(category.getCategoryName());
		categoryDetails.setDescription(category.getDescription());
		categoryDetails.setStatus(category.getStatus());
		categoryDetails.setCreatedAt(category.getCreatedAt());
		categoryDetails.setUpdatedAt(category.getUpdatedAt());
		return categoryDetails;
	}

	public static List<CategoryResponse> setCategoryDetailsByUserId(List<Object[]> listOfCategories) {
		List<CategoryResponse> categoryResponseList = new ArrayList<>();
		for (Object[] rowData : listOfCategories) {
			CategoryResponse categoryResponse = new CategoryResponse();
			categoryResponse.setCategoryId(String.valueOf(rowData[0]));
			categoryResponse.setCategoryName(String.valueOf(rowData[1]));
			categoryResponse.setStatus(Boolean.valueOf(String.valueOf(rowData[2])));
			Long fileCount = (Long) rowData[3];
			categoryResponse.setFileCount(fileCount != null ? fileCount : 0L);
			categoryResponseList.add(categoryResponse);
		}
		return categoryResponseList;
	}

	public static List<DocumentResponse> setDocumentResponsetList(List<Document> documents, Category category) {
		List<DocumentResponse> documentDetailsList = new ArrayList<>();
		for (Document document : documents) {
			DocumentResponse documentResponse = new DocumentResponse();
			documentResponse.setDocumentId(document.getId());
			documentResponse.setDocumentName(document.getDocumentName());
			documentResponse.setCategoryName(category.getCategoryName());
			documentResponse.setUploadedBy(document.getUser().getName());
			documentResponse.setDocumentUrl(document.getUrl());
			if(document.getFamily() != null) {
				documentResponse.setFamilyId(document.getFamily().getId());
				documentResponse.setFamilyName(document.getFamily().getName());
			}else {
				documentResponse.setFamilyId(null);
				documentResponse.setFamilyName(null);
			}
			documentResponse.setCreatedDate(document.getCreatedAt().toString());
			documentResponse.setUpdatedDate(document.getUpdatedAt().toString());
			documentDetailsList.add(documentResponse);
		}
		return documentDetailsList;
	}

	public static FamilyDetails setFamilyDetailsResponse(Family family, List<Member> familyMembers) {
		FamilyDetails familyDetails = new FamilyDetails();
		familyDetails.setId(family.getId());
		familyDetails.setName(family.getName());
		familyDetails.setStatus(family.getStatus());
		familyDetails.setAdminId(family.getUser().getId());
		familyDetails.setCreatedAt(family.getCreatedAt());
		familyDetails.setUpdatedAt(family.getUpdatedAt());
		familyDetails.setMember(familyMembers);
		return familyDetails;
	}

}

