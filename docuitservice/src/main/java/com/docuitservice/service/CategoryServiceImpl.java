package com.docuitservice.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.Category;
import com.docuitservice.model.Document;
import com.docuitservice.model.Family;
import com.docuitservice.model.Share;
import com.docuitservice.model.User;
import com.docuitservice.repository.CategoryRepository;
import com.docuitservice.repository.DocumentRepository;
import com.docuitservice.repository.FamilyRepository;
import com.docuitservice.repository.ShareRepository;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.request.CategoryRequest;
import com.docuitservice.request.EditCategoryRequest;
import com.docuitservice.response.CategoryDetails;
import com.docuitservice.response.CategoryResponse;
import com.docuitservice.response.DocumentResponse;
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
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	ShareRepository shareRepository;
	
	@Autowired
	FamilyRepository familyRepository;
	   

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
			CategoryDetails categoryResponseVO = ResponseHelper.setCategoryResponseVO(category);
			responseObjectsMap.put("CategoryDetails", categoryResponseVO);
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
		responseObjectsMap.put("totalCount", listOfCategories.size());
		logger.info("CategoryServiceImpl getAllCategories ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response getCategoriesByUserId(String userId) throws Exception {
		logger.info("CategoryServiceImpl getCategoriesByUserId ---Start---");
		Util.validateRequiredField(userId, ErrorConstants.USER_ID_IS_REQUIRED);
		User user = userRepository.findById(userId);
		Map<String, Object> responseObjectsMap = new HashMap<>();		
		if (user != null) {
			List<Object[]> listOfCategories = categoryRepository.getCategoryDetailsWithFileCounts(userId);
			List<CategoryResponse> listCategoryDetails = ResponseHelper.setCategoryDetailsByUserId(listOfCategories);
			responseObjectsMap.put("categoryDetails", listCategoryDetails);
			if (listOfCategories.isEmpty()) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_DETAILS_NOT_FOUND,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		} else {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("CategoryServiceImpl getCategoriesByUserId ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response userCategoryDocuments(String userId, String categoryId) throws Exception {
		logger.info("CategoryServiceImpl userCategoryDocuments ---Start---");
		Util.validateRequiredField(userId, ErrorConstants.USER_ID_IS_REQUIRED);
		Util.validateRequiredField(categoryId, ErrorConstants.CATEGORY_ID_IS_REQUIRED);
		User user = userRepository.findById(userId);
		Category category = categoryRepository.findById(categoryId);
		Map<String, Object> responseObjectsMap = new HashMap<>();
		if (user == null) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (category == null) {
			throw new BusinessException(DockItConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_DETAILS_NOT_FOUND,
					DockItConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		List<Map<String,Object>> documents = documentRepository.findByCategoryOrderByUpdatedAtDesc(category.getId(), userId);
        
        responseObjectsMap.put("documentDetailsList", documentResponseMapper(documents));
		responseObjectsMap.put("totalCount", documents.size());
		logger.info("CategoryServiceImpl userCategoryDocuments ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	
	private List<DocumentResponse> documentResponseMapper(List<Map<String,Object>> myDocumentList) {
		// TODO Auto-generated method stub
		logger.info("documentResponseMapper --->Begin");
		List<DocumentResponse> docResponse= new ArrayList<>();
		 
		for(Map<String,Object> documentMap : myDocumentList) {
			DocumentResponse documentResponse = documentResponseBuilder(documentMap);
			docResponse.add(documentResponse);
		}
		logger.info("documentResponseMapper --->End");
		return docResponse;
	}


	/**
	 * @param formatter
	 * @param document
	 * @return
	 */
	private DocumentResponse documentResponseBuilder(Map<String,Object> documentMap) {
		logger.info("documentResponseBuilder --->Begin");
		DocumentResponse documentResponse = new DocumentResponse();
		Optional<Family> familyOpt = null;
		Family family = null;
		
		documentResponse.setDocumentId(String.valueOf(documentMap.get("documetid")));
		documentResponse.setDocumentName(String.valueOf(documentMap.get("documentname")));
		if (null != documentMap.get("categoryid")) {
			Category category = categoryRepository.findById(String.valueOf(documentMap.get("categoryid")));
			if (null != category) {
				documentResponse.setCategoryName(category.getCategoryName());
			}
		}
		if (null != documentMap.get("uploadedby")) {
			documentResponse.setUploadedBy(String.valueOf(documentMap.get("uploadedby")));
		}
		if (null != documentMap.get("url")) {
			documentResponse.setDocumentUrl(String.valueOf(documentMap.get("url")));
		}		 
		if(null!=documentMap.get("familyid")) {
		documentResponse.setFamilyId(String.valueOf(documentMap.get("familyid")));
		familyOpt = familyRepository.findById(String.valueOf(documentMap.get("familyid")));
		family = familyOpt.get();
		documentResponse.setFamilyName(family.getName());
		}
		if(null!=documentMap.get("documentCreateDate")) {
			documentResponse.setCreatedDate(String.valueOf(documentMap.get("documentCreateDate")));
		}
		if(null!=documentMap.get("documentUpdatedDate")) {
			documentResponse.setUpdatedDate(String.valueOf(documentMap.get("documentUpdatedDate")));
		}
		if(null!=documentMap.get("pageCount")) {
			documentResponse.setPageCount(Integer.parseInt(String.valueOf(documentMap.get("pageCount"))));
		}
		if(null!=documentMap.get("documenttype")) {
			documentResponse.setDocumentType(String.valueOf(documentMap.get("documenttype")));
			}
		if(null!=documentMap.get("uplodedbyname")) {
			documentResponse.setUploadedByName(String.valueOf(documentMap.get("uplodedbyname")));
			}
		if(null!=documentMap.get("documentsize")) {
			  documentResponse.setDocumentSize(Long.parseLong(String.valueOf(documentMap.get("documentsize")))); 
			}
		if(null!=documentMap.get("shareId")) {
			  documentResponse.setShareId(String.valueOf(documentMap.get("shareId"))); 
			}
		if(null!=documentMap.get("shareDocumentId")) {
			  documentResponse.setShareDocumentId(String.valueOf(documentMap.get("shareDocumentId"))); 
			}
		if(null!=documentMap.get("shareMemberId")) {
			  documentResponse.setShareMemberId(String.valueOf(documentMap.get("shareMemberId"))); 
			}
		if(null!=documentMap.get("sharedBy")) {
			  documentResponse.setSharedBy(String.valueOf(documentMap.get("sharedBy"))); 
			}
		if(null!=documentMap.get("shareCreatedDate")) {
			  documentResponse.setShareCreatedDate(String.valueOf(documentMap.get("shareCreatedDate"))); 
			}
		if(null!=documentMap.get("sharedUpdatedDate")) {
			  documentResponse.setSharedUpdatedDate(String.valueOf(documentMap.get("sharedUpdatedDate"))); 
			}
		
		logger.info("documentResponseBuilder --->End");
		return documentResponse;
	}
	
	
}
