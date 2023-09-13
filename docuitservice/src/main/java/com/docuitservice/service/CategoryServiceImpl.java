package com.docuitservice.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.Category;
import com.docuitservice.repository.CategoryRepository;
import com.docuitservice.request.CategoryRequest;
import com.docuitservice.request.EditCategoryRequest;
import com.docuitservice.response.CategoryVO;
import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.ErrorConstants;
import com.docuitservice.util.Response;
import com.docuitservice.util.Util;
import jakarta.validation.Valid;

@Service
public class CategoryServiceImpl implements CategoryService {

	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Response addCategory(@Valid CategoryRequest categoryRequest) throws Exception {
		logger.info("CategoryServiceImpl addCategory ---Start---");
		Util.validateRequiredField(categoryRequest.getCategoryName(), ErrorConstants.CATEGORY_NAME_IS_REQUIRED);		
		Map<String, Object> responseObjectsMap = new HashMap<>();
		if (!Util.isValidNameFormat(categoryRequest.getCategoryName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CATEGORY_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Category categoryVO = categoryRepository.findByCategoryNameIgnoreCase(categoryRequest.getCategoryName());
		if (categoryVO != null && categoryVO.getCategoryName().equalsIgnoreCase(categoryRequest.getCategoryName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_NAME_ALREADY_REGISTERED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (categoryRequest.getCategoryName() != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			Category category = new Category();
			category.setId(UUID.randomUUID().toString());
			category.setCategoryName(categoryRequest.getCategoryName());
			category.setDescription(
					categoryRequest.getDescription() == null || categoryRequest.getDescription().isBlank()
							|| categoryRequest.getDescription().isEmpty() ? "" : categoryRequest.getDescription());
			category.setStatus(true);
			category.setCreatedAt(currentTimeStamp);
			category.setUpdatedAt(currentTimeStamp);
			categoryRepository.save(category);
			CategoryVO categoryResponseVO = ResponseHelper.setCategoryResponseVO(category);
			responseObjectsMap.put("categoryVO", categoryResponseVO);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("CategoryServiceImpl addCategory ---End---");		
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response editCategory(@Valid EditCategoryRequest editCategoryRequest) throws Exception {
		logger.info("CategoryServiceImpl editCategory ---Start---");
		Util.validateRequiredField(editCategoryRequest.getCategoryId(), ErrorConstants.CATEGORY_ID_IS_REQUIRED);
		Util.validateRequiredField(editCategoryRequest.getCategoryName(), ErrorConstants.CATEGORY_NAME_IS_REQUIRED);
		if (!Util.isValidNameFormat(editCategoryRequest.getCategoryName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_CATEGORY_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Category categoryVO = categoryRepository.findByCategoryNameIgnoreCase(editCategoryRequest.getCategoryName());
		if (categoryVO != null && categoryVO.getCategoryName().equalsIgnoreCase(editCategoryRequest.getCategoryName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_NAME_ALREADY_REGISTERED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Category category = categoryRepository.findById(editCategoryRequest.getCategoryId());
		if (category != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			category.setCategoryName(editCategoryRequest.getCategoryName());
			category.setDescription(
					editCategoryRequest.getDescription() == null || editCategoryRequest.getDescription().isBlank()
							|| editCategoryRequest.getDescription().isEmpty() ? ""
									: editCategoryRequest.getDescription());
			category.setUpdatedAt(currentTimeStamp);
			categoryRepository.save(category);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("CategoryServiceImpl editCategory ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.CATEGORY_UPDATED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response getAllCategories() throws Exception {
		logger.info("CategoryServiceImpl getAllCategories ---Start---");
		List<Category> listOfCategories = categoryRepository.findAll();
		if (listOfCategories.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		responseObjectsMap.put("categoryList", listOfCategories);
		logger.info("CategoryServiceImpl getAllCategories ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

}
