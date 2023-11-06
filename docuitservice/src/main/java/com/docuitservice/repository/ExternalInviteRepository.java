package com.docuitservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Document;
import com.docuitservice.model.ExternalInvite;
import com.docuitservice.model.Family;
import com.docuitservice.model.User;

@Repository
public interface ExternalInviteRepository extends JpaRepository<ExternalInvite, String> {

	ExternalInvite findByPhone(String phone);

	ExternalInvite findByEmail(String email);

	List<ExternalInvite> findByPhoneAndStatus(String phone, boolean b);

	List<ExternalInvite> findByEmailAndStatus(String email, boolean b);

	
	//@Query(value = "select distinct(ei) from ExternalInvite ei where (ei.invited_by = :userId and ei.family_id =:familyId and ei.status = true)", nativeQuery = true)
	@Query(value = "select distinct(ei.phone) from ExternalInvite ei where (ei.user.id = :userId and ei.family.id =:familyId and ei.status = true and ei.phone is not null)")
	List<String> findByStatusAndUserAndFamilyDistinctByPhone(@Param("userId") String userId,@Param("familyId") String familyId);

	List<ExternalInvite> findByFamily(Family family);
	
}
