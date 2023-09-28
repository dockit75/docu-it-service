/**
 * 
 */
package com.docuitservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Category;
import com.docuitservice.model.Document;

/**
 * @author srira
 *
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

	/* List<Document> findByUploadedBy(Long id); */

	/*
	 * List<Document> findByUserUploadedBy(long parseLong);
	 * 
	 * List<Document> findByUserUploadedById(long parseLong);
	 */

	/* List<Document> findByUploadedById(long parseLong); */

	/* List<Document> findByUserById(long parseLong); */

	List<Document> findByUserId(String id);
	
	Optional<Document> findById(String id);

	List<Document> findByUserIdAndDocumentStatus(String userId, boolean b);

	List<Document> findByCategory(Category category);

}
