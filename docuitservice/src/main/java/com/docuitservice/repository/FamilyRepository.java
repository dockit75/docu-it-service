package com.docuitservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

	List<Family> findByUserId(String adminId);

	Family findByNameIgnoreCase(String name);

	Family findById(String familyId);

}
