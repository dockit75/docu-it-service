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
import lombok.Data;

@Entity
@Data
@Table(name = "member")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "invite_status")
	private String inviteStatus;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@ManyToOne
	@JoinColumn(name = "family_id")
	private Family family;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
}
