package com.docuitservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "user_ranking")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserRanking {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "insurance_document")
	private Integer insuranceDocument;

	@Column(name = "health_document")
	private Integer healthDocument;

	@Column(name = "assert_document")
	private Integer assertDocument;

	@Column(name = "finance_document")
	private Integer financeDocument;

	@Column(name = "referral_invite")
	private Integer referralInvite;

}