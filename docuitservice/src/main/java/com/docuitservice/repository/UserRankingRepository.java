package com.docuitservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.UserRanking;

@Repository
public interface UserRankingRepository extends JpaRepository<UserRanking, Integer> {

	UserRanking findByUserId(String userId);

	@Query("SELECT COALESCE(SUM(ur.insuranceDocument), 0) + COALESCE(SUM(ur.healthDocument), 0) + " +
		       "COALESCE(SUM(ur.assertDocument), 0) + COALESCE(SUM(ur.financeDocument), 0) + " +
		       "COALESCE(SUM(ur.referralInvite), 0) FROM UserRanking ur WHERE ur.userId = :userId")
	Integer calculateUserRanking(@Param("userId") String userId);


}
