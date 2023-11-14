/**
 * 
 */
package com.docuitservice.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Category;
import com.docuitservice.model.Document;
import com.docuitservice.model.Family;
import com.docuitservice.model.User;

/**
 * @author srira
 *
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

	List<Document> findByUserId(String id);
	
	Optional<Document> findById(String id);

	List<Document> findByUserIdAndDocumentStatus(String userId, boolean b);
	
	@Query(value = "select * from public.document where uploaded_by=:userId order by document.updated_at desc LIMIT 3 OFFSET 0", nativeQuery = true)
	public List<Document> getMaxDateDocument(@Param("userId") String userId);
	
	@Query(value = "SELECT d.id as documetid, d.family_id,d.document_name,d.document_size,d.status, d.document_type,d.updated_at as documentupdatedAt,d.url,d.category_id,d.uploaded_by,d.page_count,d.created_at as documentCreatedat, s.id as shareid,s.document_id as shareDocumentId,s.member_id as shareMemberId,s.shared_by as sharedby,s.created_at as shareCreatedAt,s.updated_at as sharedUpdatedAt FROM document AS d INNER JOIN share AS s ON s.document_id = d.id INNER JOIN member AS m ON s.member_id = m.id WHERE d.category_id = :categoryId AND m.user_id = :userId   union select di.id as documetid, di.family_id,di.document_name,di.document_size,di.status,di.document_type,di.updated_at as documentupdatedAt,di.url,di.category_id,di.uploaded_by,di.page_count,di.created_at as documentCreatedat,sh.id as shareid,sh.document_id as shareDocumentId,sh.member_id as shareMemberId,sh.shared_by as sharedby,sh.created_at as shareCreatedAt,sh.updated_at as sharedUpdatedAt from document AS di left outer JOIN share AS sh ON sh.document_id = di.id where di.uploaded_by= :userId and di.family_id is null", nativeQuery = true)
	List<Map<String, String>> findByCategoryOrderByUpdatedAtDesc(@Param("categoryId") String categoryId, @Param("userId") String userId);

	
	List<Document> findByFamilyIdAndUserId(String familyId,String userId);

	List<Document> findByUserAndFamily(User user, Family family);
	
	//List<Document> findByUserIdAndFamilyId(String familyId,String userId);
	
	List<Document> findByFamily(Family family);

}
