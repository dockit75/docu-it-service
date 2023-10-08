package com.docuitservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.docuitservice.model.Images;

@Repository
public interface ImageRepository extends JpaRepository<Images, Long> {

	Images findByUserId(String userId);

}
