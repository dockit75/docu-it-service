package com.docuitservice.model;

import java.util.Date;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "externalinvite")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExternalInvite {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;
	
	@Column(length = 60)
	@Size(min = 1, max = 60)
	private String email;

	@Column(name = "phone")
	@Size(min = 10, max = 11)
	private String phone;

	@ManyToOne
	@JoinColumn(name = "family_id")
	private Family family;
	
	@ManyToOne
	@JoinColumn(name = "invited_by")
	private User user;
	
	@Column(name = "status")
	private Boolean status;

}
