package com.docuitservice.service;

import com.docuitservice.request.EditFamilyRequest;
import com.docuitservice.request.FamilyMemberInviteAcceptedRequest;
import com.docuitservice.request.FamilyMemberInviteRequest;
import com.docuitservice.request.FamilyRequest;
import com.docuitservice.util.Response;

import jakarta.validation.Valid;

public interface FamilyService {

	Response addFamily(@Valid FamilyRequest familyRequest) throws Exception;

	Response editFamily(@Valid EditFamilyRequest editFamilyRequest) throws Exception;

	Response listFamily(String adminId)  throws Exception;

	Response familyMemberInvite(FamilyMemberInviteRequest familyMemberInviteCreation)throws Exception;
	
	Response familyMemberInviteAccept(FamilyMemberInviteAcceptedRequest familyMemberInviteAcceptedRequest)throws Exception;


}
