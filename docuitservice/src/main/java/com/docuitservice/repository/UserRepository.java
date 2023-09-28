package com.docuitservice.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docuitservice.model.User;

import jakarta.persistence.Tuple;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmailAndStatus(String email, String active);
	
	boolean existsByPhoneAndStatus(String phone, String active);

	boolean existsByDeviceIdAndStatus(String deviceId, String active);

	User findById(String id);

	Optional<User> findByEmail(String email);

	User findByPhone(String phone);

	User findByDeviceId(String deviceId);

	User findByDeviceIdAndPassword(String deviceId, String pinNumber);
	
	@Query("SELECT SUM(CASE WHEN u.accountVerified = TRUE OR u.accountVerified = FALSE THEN 1 ELSE 0 END) AS totalUser, "
			+ "SUM(CASE WHEN u.createdAt >= :thirtyDaysAgo AND (u.accountVerified = TRUE OR u.accountVerified = FALSE) THEN 1 ELSE 0 END) AS last30DaysUserCount, "
			+ "(SELECT COUNT(*) FROM Document d) AS totalDocuments, "
			+ "(SELECT COUNT(*) FROM Document d WHERE d.createdAt >= :thirtyDaysAgo) AS last30DaysDocumentCount "
			+ "FROM User u")
	Tuple getUserAndDocumentStatisticsWithinLast30Days(@Param("thirtyDaysAgo") Date thirtyDaysAgo);





	
}
