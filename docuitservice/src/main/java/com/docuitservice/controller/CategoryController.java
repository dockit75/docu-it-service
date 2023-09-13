package com.docuitservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.docuitservice.request.CategoryRequest;
import com.docuitservice.request.EditCategoryRequest;
import com.docuitservice.service.CategoryService;
import com.docuitservice.util.Response;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/category")
public class CategoryController {

	public static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	private CategoryService categoryService;
	
	@PostMapping("/addCategory")
	public Response addCategory(@RequestBody @Valid CategoryRequest categoryRequest) throws Exception {
		return categoryService.addCategory(categoryRequest);
	}
	
	@PutMapping("/editCategory")
	public Response editCategory(@RequestBody @Valid EditCategoryRequest editCategoryRequest) throws Exception {
		return categoryService.editCategory(editCategoryRequest);
	}

	@GetMapping("/listCategories")
	public Response getAllCategories() throws Exception {
		return categoryService.getAllCategories();
	}
}
