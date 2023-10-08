package com.docuitservice.service;

import java.sql.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.AmazonClient;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.Images;
import com.docuitservice.model.User;
import com.docuitservice.repository.ImageRepository;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.ErrorConstants;
import com.docuitservice.util.Response;
import com.docuitservice.util.Util;

@Service
public class ImageServiceImpl implements ImageService {

	private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
	

	@Value("${profile.upload.image}")
	private String profileUpload;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	AmazonClient amazonClient;

	@Override
	public Response saveImage(String userId, MultipartFile file) throws Exception {
		logger.info("ImageServiceImpl saveImage ---- starts ---- {}", userId);
		try {
			Util.validateRequiredField(userId, ErrorConstants.USER_ID_IS_REQUIRED);
			if (file == null || file.isEmpty()) {
	            throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.PROFILE_IMAGE_FILE_IS_REQUIRED,
	                    ErrorConstants.RESPONSE_EMPTY_DATA, 1002);
	        }
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			User user = userRepository.findById(userId);
			Images imageVO = imageRepository.findByUserId(userId);
			UUID uuid = UUID.randomUUID();
			String filename = file.getOriginalFilename();
			logger.info("Uploaded File Name: {}", filename);
			String documentPath = profileUpload + userId;
			String documentUrl = amazonClient.uploadFile(file, documentPath, filename);
			if (imageVO == null) {				
				Images image = new Images();
				image.setId(uuid.toString());
				image.setStatus(true);
				image.setUrl(documentUrl);
				image.setCreatedAt(currentTimeStamp);
				image.setUpdatedAt(currentTimeStamp);
				image.setUser(user);
				imageRepository.save(image);
				return ResponseHelper.getSuccessResponse(DockItConstants.IMAGE_UPLOADED_SUCCESSFULLY, image, 200,
						DockItConstants.RESPONSE_SUCCESS);
			} else {
				imageVO.setId(imageVO.getId());
				imageVO.setStatus(true);
				imageVO.setUrl(documentUrl);
				imageVO.setUpdatedAt(currentTimeStamp);
				imageRepository.save(imageVO);
				return ResponseHelper.getSuccessResponse(DockItConstants.IMAGE_UPLOADED_SUCCESSFULLY, imageVO, 200,
						DockItConstants.RESPONSE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Error while saving image for user: " + userId, e);
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.IMAGE_UPLOAD_FAILED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
	}

}
