package com.docuitservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findById(String categoryId);

	Category findByCategoryNameIgnoreCase(String categoryName);
		
	@Query("SELECT c.id, c.categoryName, c.status, COUNT(d.id) AS fileCount " + "FROM Category c "
			+ "LEFT JOIN Document d ON c.id = d.category.id AND d.user.id = :userId " + "GROUP BY c.id")
	List<Object[]> getCategoryDetailsWithFileCounts(@Param("userId") String userId);
	

}
