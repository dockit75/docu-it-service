package com.docuitservice.model;

import java.util.Date;
import org.hibernate.annotations.NaturalId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@Column(name = "id")
	private String id;

	@Column(length = 60)
	@Size(min = 1, max = 100)	
	private String name;

	@NaturalId
	@Column(length = 60, unique = true)
	@Size(min = 1, max = 60)
	private String email;

	@Column(name = "phone", unique = true)
	@Size(min = 10, max = 11)
	private String phone;

	@Column(name = "password")
	@Size(min = 4, max = 60)
	@JsonIgnore
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@Column(length = 5)
	@JsonIgnore
	private String otp;

	@Column(length = 6)
	private String gender;

	@Column(name = "device_id", length = 255, unique = true)
	private String deviceId;

	@Column(name = "account_verified", columnDefinition = "boolean default false")
	private boolean accountVerified;

	@Column(name = "is_admin", columnDefinition = "boolean default false")
	private boolean isAdmin;
	
}
