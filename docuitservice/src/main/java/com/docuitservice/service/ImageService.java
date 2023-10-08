package com.docuitservice.service;

import org.springframework.web.multipart.MultipartFile;
import com.docuitservice.util.Response;

public interface ImageService {

	Response saveImage(String userId, MultipartFile file) throws Exception;

}
