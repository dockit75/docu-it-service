package com.docuitservice.service;

import com.docuitservice.request.CategoryRequest;
import com.docuitservice.request.EditCategoryRequest;
import com.docuitservice.util.Response;

import jakarta.validation.Valid;

public interface CategoryService {

	Response addCategory(@Valid CategoryRequest categoryRequest) throws Exception;

	Response getAllCategories() throws Exception;

	Response editCategory(@Valid EditCategoryRequest editCategoryRequest) throws Exception;

	Response getCategoriesByUserId(String userId) throws Exception;

	Response userCategoryDocuments(String userId, String categoryId) throws Exception;

}
