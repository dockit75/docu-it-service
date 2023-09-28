package com.docuitservice.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document")
public class Document {
	
		@Id
		@Column(name = "id")
		private String id;

		@Size(min = 1, max = 250)
		@Column(name = "document_name")
		private String documentName;
	
		@ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
		@JoinColumn(name = "category_id")
		private Category category;
	
		@ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
		@JoinColumn(name = "uploaded_by")
		private User user;
	
		@Column(name = "document_type")
		private String documentType;
		
		@Column(name = "url")
		private String url;
		
		@ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
		@JoinColumn(name = "family_id")
		private Family family;
		
		@Column(name = "document_size")
		private Long documentSize;
		
		@Column(name = "created_at")
		private Date createdAt;
				
		@Column(name = "updated_at")
		private Date updatedAt;
		
		@Column(name = "status")
		private boolean documentStatus;
}
