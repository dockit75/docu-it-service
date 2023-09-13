package com.docuitservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findById(String categoryId);

	Category findByCategoryNameIgnoreCase(String categoryName);

}
