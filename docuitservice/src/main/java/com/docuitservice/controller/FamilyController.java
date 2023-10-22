package com.docuitservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docuitservice.request.EditFamilyRequest;
import com.docuitservice.request.ExternalInviteAcceptRequest;
import com.docuitservice.request.ExternalInviteRequest;
import com.docuitservice.request.FamilyMemberInviteAcceptedRequest;
import com.docuitservice.request.FamilyMemberInviteRequest;
import com.docuitservice.request.FamilyRequest;
import com.docuitservice.service.FamilyService;
import com.docuitservice.service.UserService;
import com.docuitservice.util.Response;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/family")
public class FamilyController {

	public static final Logger logger = LoggerFactory.getLogger(FamilyController.class);

	@Autowired
	private FamilyService familyService;
	
	@Autowired
	private UserService userService;
	
	

	@PostMapping("/addFamily")
	public Response addFamily(@RequestBody @Valid FamilyRequest familyRequest) throws Exception {
		return familyService.addFamily(familyRequest);
	}

	@PutMapping("/editFamily")
	public Response editFamily(@RequestBody @Valid EditFamilyRequest editFamilyRequest) throws Exception {
		return familyService.editFamily(editFamilyRequest);
	}

	@GetMapping("/listFamily")
	public Response listFamily(@RequestParam String adminId) throws Exception {
		return familyService.listFamily(adminId);
	}

	@RequestMapping(value = "/inviteDocultUser", method = RequestMethod.POST)
	public Response addFamilyMember(@RequestBody FamilyMemberInviteRequest familyMemberInviteRequest) throws Exception {
		return familyService.familyMemberInvite(familyMemberInviteRequest);
	}

	@RequestMapping(value = "/listFamilyMembers", method = RequestMethod.GET)
	public Response getFamilyMembers(@RequestParam String familyId) throws Exception {
		return familyService.getFamilyMembersList(familyId);
	}
	
	@RequestMapping(value = "/listPendingInvites", method = RequestMethod.GET)
	public Response getUsersActiveInvites(@RequestParam String userId) throws Exception {
		return familyService.getUsersPendingInvites(userId);
	}
	
	@RequestMapping(value = "/acceptInvite", method = RequestMethod.POST)
	public Response acceptFamilyMemberInvite(@RequestBody FamilyMemberInviteAcceptedRequest familyMemberInviteAcceptedRequest) throws Exception {
		return familyService.familyMemberInviteAccept(familyMemberInviteAcceptedRequest);
	}
	
	@RequestMapping(value = "/externalInvite", method = RequestMethod.POST)
	public Response addexternalInvite(@RequestBody ExternalInviteRequest externalInviteRequest) throws Exception {
		return familyService.externalInvite(externalInviteRequest);
	}
	
	@RequestMapping(value = "/getExternalInvite", method = RequestMethod.GET)
	public Response getExternalInvite(@RequestBody ExternalInviteRequest externalInviteRequest) throws Exception {
		return familyService.getExternalInvite(externalInviteRequest);
	}
	
	@RequestMapping(value = "/acceptExternalInvite", method = RequestMethod.POST)
	public Response addexternalInvite(@RequestBody ExternalInviteAcceptRequest externalInviteAcceptRequest) throws Exception {
		return familyService.externalInviteAccept(externalInviteAcceptRequest);
	}
	
	@RequestMapping(value = "/listDocultUsers", method = RequestMethod.GET)
	public Response addexternalInvite() throws Exception {
		return userService.getUserDetails();
	}
}
