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
	
	@Query(value = "SELECT d.id as documetId, d.document_name as documentName, d.document_size as documentSize, d.status as status, d.document_type as documentType, d.updated_at as documentUpdatedDate, d.url as url, d.category_id as categoryId, d.uploaded_by as uploadedBy, d.page_count as pageCount, d.created_at as documentCreateDate, s.id as shareId, s.document_id as shareDocumentId, s.member_id as shareMemberId, s.shared_by as sharedBy, s.created_at as shareCreatedDate, s.updated_at as sharedUpdatedDate, u.name as uplodedbyname FROM document AS d INNER JOIN share AS s ON s.document_id = d.id INNER JOIN member AS m ON s.member_id = m.id INNER JOIN users u ON u.id = d.uploaded_by WHERE d.category_id = :categoryId AND m.user_id = :userId union select di.id as documetId, di.document_name as documentName, di.document_size as documentSize, di.status as status, di.document_type as documentType, di.updated_at as documentUpdatedDate, di.url as url, di.category_id as categoryId, di.uploaded_by as uploadedBy, di.page_count as pageCount, di.created_at as documentCreateDate, sh.id as shareid, sh.document_id as shareDocumentId, sh.member_id as shareMemberId, sh.shared_by as sharedby, sh.created_at as shareCreatedDate, sh.updated_at as sharedUpdatedDate, u.name as uplodedbyname from document AS di left outer JOIN share AS sh ON sh.document_id = di.id INNER JOIN users u ON u.id = di.uploaded_by INNER JOIN member m ON m.id = sh.member_id where m.user_id = u.id AND di.uploaded_by = :userId and di.category_id = :categoryId", nativeQuery = true)
	List<Map<String,Object>> findByCategoryOrderByUpdatedAtDesc(@Param("categoryId") String categoryId, @Param("userId") String userId);

	int countByUserIdAndCategoryId(String userId, String categoryId);
	
	//List<Document> findByFamilyIdAndUserId(String familyId,String userId);

	//List<Document> findByUserAndFamily(User user, Family family);
	
	//List<Document> findByUserIdAndFamilyId(String familyId,String userId);
	
	//List<Document> findByFamily(Family family);

}
