package com.docuitservice.helper;

import com.docuitservice.model.Category;
import com.docuitservice.response.CategoryVO;
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

	public static CategoryVO setCategoryResponseVO(Category category) {
		CategoryVO categoryVO = new CategoryVO();
		categoryVO.setCategoryId(category.getId());
		categoryVO.setCategoryName(category.getCategoryName());
		categoryVO.setDescription(category.getDescription());
		categoryVO.setStatus(category.getStatus());
		categoryVO.setCreatedAt(category.getCreatedAt());
		categoryVO.setUpdatedAt(category.getUpdatedAt());
		return categoryVO;
	}
}
