package com.docuitservice.response;

import lombok.Data;

@Data
public class UserAndDocumentStatistics {

	private Long totalUsers;	
	private Long last30DaysUserCount;
	private Long totalDocuments;
	private Long last30DaysDocumentCount;

}
