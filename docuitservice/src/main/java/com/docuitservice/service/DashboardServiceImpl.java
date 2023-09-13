package com.docuitservice.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.response.UserAndDocumentStatistics;
import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.Response;

import jakarta.persistence.Tuple;

@Service
public class DashboardServiceImpl implements DashboardService {

	private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public Response getDetails() throws Exception {
		logger.info("DashboardServiceImpl getDetails ---Start---");
		Map<String, Object> responseObjectsMap = new HashMap<>();
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Date thirtyDaysAgoDate = java.sql.Timestamp.valueOf(thirtyDaysAgo);
		Tuple result = userRepository.getUserAndDocumentStatisticsWithinLast30Days(thirtyDaysAgoDate);
		if (result != null) {			
			Long totalUsers = result.get("totalUser", Long.class);			
			Long last30DaysUserCount = result.get("last30DaysUserCount", Long.class);
			long last30DaysDocumentCount = 0;
			long totalDocuments = 0;
			UserAndDocumentStatistics statistics = new UserAndDocumentStatistics();
			statistics.setTotalUsers(totalUsers);			
			statistics.setLast30DaysUserCount(last30DaysUserCount);
			statistics.setLast30DaysDocumentCount(last30DaysDocumentCount);
			statistics.setTotalDocuments(totalDocuments);
			responseObjectsMap.put("userAndDocumentStatistics", statistics);
		} else {
			responseObjectsMap.put("userAndDocumentStatistics", "UserAndDocumentStatistics details not found");
		}
		logger.info("DashboardServiceImpl getDetails ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

}
