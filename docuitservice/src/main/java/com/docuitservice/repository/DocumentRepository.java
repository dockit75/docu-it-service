/**
 * 
 */
package com.docuitservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Category;
import com.docuitservice.model.Document;

/**
 * @author srira
 *
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

	List<Document> findByUserId(String id);
	
	Optional<Document> findById(String id);

	List<Document> findByUserIdAndDocumentStatus(String userId, boolean b);
	
	@Query(value = "select * from public.document where uploaded_by=:userId and status=true and date (\"document\".created_at)=(SELECT date(max(created_at)) FROM public.document where uploaded_by=:userId and status=true) order by document.id LIMIT 3 OFFSET 0", nativeQuery = true)
	public List<Document> getMaxDateDocument(@Param("userId") String userId);

	@Query(value = "select * from document where (category_id = :id and uploaded_by = :userId and status = true) order by updated_at desc", nativeQuery = true)
	List<Document> findByCategoryOrderByUpdatedAtDesc(@Param("id") String id, @Param("userId") String userId);

}
