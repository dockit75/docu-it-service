package com.docuitservice.service;

import com.docuitservice.request.CommonInviteRequest;
import com.docuitservice.request.DeleteFamilyRequest;
import com.docuitservice.request.EditFamilyRequest;
import com.docuitservice.request.ExternalInviteAcceptRequest;
import com.docuitservice.request.ExternalInviteRequest;
import com.docuitservice.request.FamilyMemberInviteAcceptedRequest;
import com.docuitservice.request.FamilyMemberInviteRequest;
import com.docuitservice.request.FamilyRequest;
import com.docuitservice.request.MemberOperationRequest;
import com.docuitservice.util.Response;

import jakarta.validation.Valid;

public interface FamilyService {

	Response addFamily(@Valid FamilyRequest familyRequest) throws Exception;

	Response editFamily(@Valid EditFamilyRequest editFamilyRequest) throws Exception;

	Response listFamily(String adminId)  throws Exception;

	Response familyMemberInvite(FamilyMemberInviteRequest familyMemberInviteCreation)throws Exception;
	
	Response familyMemberInviteAccept(FamilyMemberInviteAcceptedRequest familyMemberInviteAcceptedRequest)throws Exception;

	Response externalInvite(ExternalInviteRequest externalInviteRequest)throws Exception;

	Response externalInviteAccept(ExternalInviteAcceptRequest externalInviteAcceptRequest);

	Response getExternalInvite(ExternalInviteRequest externalInviteRequest) throws Exception;

	Response getFamilyMembersList(String familyId) throws Exception;

	Response getUsersPendingInvites(String userId);
	
	Response familyMemberCommonInvite(CommonInviteRequest commonInviteRequest)throws Exception;

	Response removeFamilyMemebers(MemberOperationRequest memberOperationRequest);
	
	public Response deleteFamily(@Valid DeleteFamilyRequest deleteFamilyRequest) throws Exception;

	//public Response getExternalInviteByInviter(String invitedBy,String familyId) throws Exception;
	
	
}
