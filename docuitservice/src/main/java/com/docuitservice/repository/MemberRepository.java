package com.docuitservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Family;
import com.docuitservice.model.Member;
import com.docuitservice.model.User;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

	List<Member> findByIdIn(List<String> docSharedList);

	List<Member> findByUserId(String userId);

	List<Member> findAllById(Iterable<String> iterator);

	Member findByUserAndFamily(User user, Family family);

	List<Member> findByFamily(Family family);

	List<Member> findByFamilyAndInviteStatus(Family family, String inviteAccepted);
	
	List<Member> findByFamilyAndInviteStatusNot(Family family, String inviteAccepted);

	List<Member> findByUserAndInviteStatus(User user, String inviteRequested);

	List<Member> findByUserAndFamilyAndInviteStatusNot(User user, Family family,String inviteAccepted);

	List<Member> findByUserIdAndInviteStatus(String adminId, String inviteAccepted);
	
	List<Member> findByUserAndFamilyIn(User user, List<Family> family);
}
