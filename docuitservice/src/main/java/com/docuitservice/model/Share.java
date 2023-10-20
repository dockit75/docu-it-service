package com.docuitservice.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "share")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Share {
	
	
		@Id
		@Column(name = "id")
		private String id;
	
		@ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
		@JoinColumn(name = "document_id")
		private Document document;
	
		@ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
		@JoinColumn(name = "shared_by")
		private User user;
		
		@ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
		@JoinColumn(name = "member_id")
		private Member member;
		
		@Column(name = "created_at")
		private Date createdAt;
		@Column(name = "updated_at")
		private Date updatedAt;
				
}
