package com.docuitservice.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "family")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Family {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name", unique = true, length = 50)	
	private String name;

	@Column(name = "status")
	private Boolean status;

	@ManyToOne
	@JoinColumn(name = "admin_id")
	@JsonIgnore
	private User user;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;
	
	@Transient
	private String createdBy;

}
