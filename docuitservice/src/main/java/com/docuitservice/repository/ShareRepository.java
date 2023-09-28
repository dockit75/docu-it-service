package com.docuitservice.repository;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Document;
import com.docuitservice.model.Member;
import com.docuitservice.model.Share;


@Repository
public interface ShareRepository extends JpaRepository<Share, String>  {
	
	List<Share> findByDocumentId(String documentId);

	List<Share> findByMemberIdIn(List<String> ids);

	//List<Share> findByUser(Long memberId);

	List<Share> findByUserId(String id);
	
	//List<Share> findAllByDocumentIdandMemberId(long documentId,int memberId);
	
	//void deleteAllByDocumentIdandMemberId(long documentId,long memberId);
	
	List<Share> findAllByDocumentAndMemberIn(Document documentId,List<Member> memberId);
	
	void deleteAllByIdInBatch(Iterable<String> ids);
}
