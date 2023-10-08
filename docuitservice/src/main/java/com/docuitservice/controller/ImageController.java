package com.docuitservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.service.ImageService;
import com.docuitservice.util.Response;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/image")
public class ImageController {

	@Autowired
	private ImageService imageService;

	@PostMapping("/save")
	public Response saveImage(@RequestParam String userId,
			@RequestParam(value = "image", required = false) MultipartFile file) throws Exception {
		return imageService.saveImage(userId, file);
	}

}
