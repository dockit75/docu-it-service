package com.docuitservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, String> {

	List<Family> findByUserId(String adminId);

	Family findByUserIdAndNameIgnoreCase(String userId, String name);

	Optional<Family> findById(String familyId);
	
	void deleteAllByIdInBatch(Iterable<String> ids);

	List<Family> findByUserIdAndStatus(String adminId, boolean b);
	
	/*
	 * @Modifying
	 * 
	 * @Query(value = "delete from family where id =:familyId ",nativeQuery = true)
	 * void deleteByFamilyId(@Param("familyId") String id);
	 */
}
