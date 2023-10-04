package com.docuitservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.ExternalInvite;

@Repository
public interface ExternalInviteRepository extends JpaRepository<ExternalInvite, String> {

	ExternalInvite findByPhone(String phone);

	ExternalInvite findByEmail(String email);

	List<ExternalInvite> findByPhoneAndStatus(String phone, boolean b);

	List<ExternalInvite> findByEmailAndStatus(String email, boolean b);
	
}
