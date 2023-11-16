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

	@Query(value = "SELECT c.id, c.category_name, c.status, COUNT(d.id) AS fileCount " +
            "FROM category c " +
            "LEFT JOIN document d ON c.id = d.category_id AND (d.uploaded_by = :userId " +
            "OR (d.id IN (SELECT document_id FROM share WHERE member_id IN " +
            "(SELECT id FROM member WHERE user_id = :userId)) AND d.category_id = c.id)) " +
            "GROUP BY c.id", nativeQuery = true)
	List<Object[]> getCategoryDetailsWithFileCounts(@Param("userId") String userId);


}
