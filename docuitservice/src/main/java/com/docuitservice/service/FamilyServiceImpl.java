package com.docuitservice.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.ExternalInvite;
import com.docuitservice.model.Family;
import com.docuitservice.model.Member;
import com.docuitservice.model.User;
import com.docuitservice.repository.ExternalInviteRepository;
import com.docuitservice.repository.FamilyRepository;
import com.docuitservice.repository.MemberRepository;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.request.EditFamilyRequest;
import com.docuitservice.request.ExternalInviteAcceptRequest;
import com.docuitservice.request.ExternalInviteRequest;
import com.docuitservice.request.FamilyMemberInviteAcceptedRequest;
import com.docuitservice.request.FamilyMemberInviteRequest;
import com.docuitservice.request.FamilyRequest;
import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.ErrorConstants;
import com.docuitservice.util.Response;
import com.docuitservice.util.Util;

import jakarta.validation.Valid;

@Service
public class FamilyServiceImpl implements FamilyService {

	private static final Logger logger = LoggerFactory.getLogger(FamilyServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FamilyRepository familyRepository;

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private ExternalInviteRepository externalInviteRepository;
	
	@Autowired
	private UserService userService;

	@Override
	public Response addFamily(@Valid FamilyRequest familyRequest) throws Exception {
		logger.info("FamilyServiceImpl addFamily ---Start---");
		Util.validateRequiredField(familyRequest.getName(), ErrorConstants.FAMILY_NAME_IS_REQUIRED);
		Util.validateRequiredField(familyRequest.getAdminId(), ErrorConstants.ADMIN_ID_IS_REQUIRED);
		if (!Util.isValidNameFormat(familyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_FAMILY_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Family familyVO = familyRepository.findByNameIgnoreCase(familyRequest.getName());
		if (familyVO != null && familyVO.getName().equalsIgnoreCase(familyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_NAME_ALREADY_REGISTERED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		User user = userRepository.findById(familyRequest.getAdminId());
		if (user != null) {
			Family family = new Family();
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			family.setId(UUID.randomUUID().toString());
			family.setName(familyRequest.getName());
			family.setStatus(true);
			family.setCreatedAt(currentTimeStamp);
			family.setUpdatedAt(currentTimeStamp);
			family.setUser(user);
			familyRepository.save(family);

			Member member = new Member();
			member.setId(UUID.randomUUID().toString());
			member.setFamily(family);
			member.setUser(user);
			member.setInviteStatus(DockItConstants.INVITE_ACCEPTED);
			member.setStatus(true);
			member.setCreatedAt(currentTimeStamp);
			member.setUpdatedAt(currentTimeStamp);
			memberRepository.save(member);

		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("FamilyServiceImpl addFamily ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_REGISTERED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response editFamily(@Valid EditFamilyRequest editFamilyRequest) throws Exception {
		logger.info("FamilyServiceImpl editFamily ---Start---");
		if (editFamilyRequest == null) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Util.validateRequiredField(editFamilyRequest.getName(), ErrorConstants.FAMILY_NAME_IS_REQUIRED);
		Util.validateRequiredField(editFamilyRequest.getFamilyId(), ErrorConstants.FAMILY_ID_IS_REQUIRED);
		if (!Util.isValidNameFormat(editFamilyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_FAMILY_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Family familyVO = familyRepository.findByNameIgnoreCase(editFamilyRequest.getName());
		if (familyVO != null && familyVO.getName().equalsIgnoreCase(editFamilyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_NAME_ALREADY_REGISTERED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Family family = familyRepository.findById(editFamilyRequest.getFamilyId());
		if (family != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			family.setName(editFamilyRequest.getName());
			family.setUpdatedAt(currentTimeStamp);
			familyRepository.save(family);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("FamilyServiceImpl addFamily ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_NAME_CHANGED_SUCCESSFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response listFamily(String adminId) throws Exception {
		logger.info("FamilyServiceImpl listFamily ---Start---");
		if (adminId.isBlank() || adminId.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_NAME_IS_REQUIRED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		List<Family> familyList = familyRepository.findByUserId(adminId);
		if (familyList == null || familyList.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_DETAILS_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
		responseObjectsMap.put("familyList", familyList);
		logger.info("FamilyServiceImpl listFamily ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.RESPONSE_SUCCESS, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response familyMemberInvite(FamilyMemberInviteRequest familyMemberInviteRequest) {

		// TODO Auto-generated method stub
		User user=null;
		Family family=null;
		Optional<Family> familyOpt =  null;
		if(!StringUtils.hasLength(familyMemberInviteRequest.getFamilyId()) || !StringUtils.hasLength(familyMemberInviteRequest.getUserId())) {
			
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(null !=familyMemberInviteRequest.getUserId()) {
			user = userRepository.findById(familyMemberInviteRequest.getUserId());
			if(null ==user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		if(null !=familyMemberInviteRequest.getFamilyId()) {
			family = familyRepository.findById(familyMemberInviteRequest.getFamilyId());
			if(null ==family) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		//family = familyOpt.get();
		
		Member member = new Member();
		member.setId(UUID.randomUUID().toString());
		member.setFamily(family);
		member.setUser(user);
		member.setInviteStatus(DockItConstants.INVITE_REQUESTED);
		member.setStatus(true);
		member.setCreatedAt(currentTimeStamp);
		member.setUpdatedAt(currentTimeStamp);
		memberRepository.save(member);				
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_INVITED_SUCCESSFULY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	
	}

	@Override
	public Response familyMemberInviteAccept(FamilyMemberInviteAcceptedRequest familyMemberInviteAcceptedRequest) {
		// TODO Auto-generated method stub
		User user=null;
		Family family=null;
		Member member = null;
		Optional<Family> familyOpt =  null;
		if(!StringUtils.hasLength(familyMemberInviteAcceptedRequest.getFamilyId()) || !StringUtils.hasLength(familyMemberInviteAcceptedRequest.getUserId()) || !StringUtils.hasLength(familyMemberInviteAcceptedRequest.getInviteStatus())) {
			
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(null !=familyMemberInviteAcceptedRequest.getUserId()) {
			user = userRepository.findById(familyMemberInviteAcceptedRequest.getUserId());
			if(null ==user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		if(null !=familyMemberInviteAcceptedRequest.getFamilyId()) {
			family = familyRepository.findById(familyMemberInviteAcceptedRequest.getFamilyId());
			if(null ==family) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		
		if(StringUtils.hasLength(familyMemberInviteAcceptedRequest.getInviteStatus()) && (!familyMemberInviteAcceptedRequest.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_ACCEPTED) && !familyMemberInviteAcceptedRequest.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_REJECTED))) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		//family = familyOpt.get();
		member = memberRepository.findByUserAndFamily(user,family);
		if(member.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_ACCEPTED) || member.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_ACCEPTED)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ALREADY_RESPONDED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(familyMemberInviteAcceptedRequest.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_ACCEPTED)) {
		member.setInviteStatus(DockItConstants.INVITE_ACCEPTED);
		}else if(familyMemberInviteAcceptedRequest.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_REJECTED)){
			member.setInviteStatus(DockItConstants.INVITE_REJECTED);
		}
		member.setUpdatedAt(currentTimeStamp);
		memberRepository.save(member);
		
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_INVITE_RESPONDED, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response getFamilyMembersList(String familyId) {
		// TODO Auto-generated method stub
		Family family=null;
		List<Member> members = new ArrayList<>();
		if(!StringUtils.hasLength(familyId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		family = familyRepository.findById(familyId);
		if(null ==family) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		members = memberRepository.findByFamilyAndInviteStatus(family,DockItConstants.INVITE_ACCEPTED);
		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_MEMBERS_LIST, members, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	

	@Override
	public Response getUsersPendingInvites(String userId) {
		// TODO Auto-generated method stub
		User user = null;
		List<Member> members = new ArrayList<>();
		if(StringUtils.hasText(userId)) {
			user = userRepository.findById(userId);
			if(null ==user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		members = memberRepository.findByUserAndInviteStatus(user,DockItConstants.INVITE_REQUESTED);
		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_MEMBERS_LIST, members, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response externalInvite(ExternalInviteRequest externalInviteRequest) throws Exception {
		// TODO Auto-generated method stub
		Family family = null;
		User user = null;
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		if(!StringUtils.hasText(externalInviteRequest.getEmail()) && !StringUtils.hasText(externalInviteRequest.getPhone())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (StringUtils.hasText(externalInviteRequest.getPhone()) && ! Util.isValidPhoneNumberFormat(externalInviteRequest.getPhone())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (StringUtils.hasText(externalInviteRequest.getEmail()) && !Util.isValidEmailIdFormat(externalInviteRequest.getEmail())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(null !=externalInviteRequest.getFamilyId()) {
			family = familyRepository.findById(externalInviteRequest.getFamilyId());
			if(null == family) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		if(null !=externalInviteRequest.getInvitedBy()) {
			user = userRepository.findById(externalInviteRequest.getInvitedBy());
			if(null == user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		
		ExternalInvite externalInvite = new ExternalInvite();
		externalInvite.setId(UUID.randomUUID().toString());
		if(StringUtils.hasText(externalInviteRequest.getPhone())) {
		externalInvite.setPhone(externalInviteRequest.getPhone());
		}
		if(StringUtils.hasText(externalInviteRequest.getEmail())) {
			externalInvite.setEmail(externalInviteRequest.getEmail());
		}
		externalInvite.setFamily(family);
		externalInvite.setStatus(true);
		externalInvite.setUser(user);
		externalInvite.setCreatedAt(currentTimeStamp);
		externalInvite.setUpdatedAt(currentTimeStamp);
		externalInviteRepository.save(externalInvite);
		userService.sendEmailInvite(externalInvite.getEmail(), user.getName());
		if (externalInvite.getPhone() != null && !externalInvite.getPhone().isEmpty()) {
			userService.sendSmsInvite(externalInvite.getPhone(), user.getName());
		}
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_INVITED_SUCCESSFULY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response getExternalInvite(ExternalInviteRequest externalInviteRequest) throws Exception {
		List<ExternalInvite> externalInvites = new ArrayList<ExternalInvite>();
		
		if(StringUtils.hasText(externalInviteRequest.getPhone())){
			externalInvites = externalInviteRepository.findByPhoneAndStatus(externalInviteRequest.getPhone(),true);
			}
			if(StringUtils.hasText(externalInviteRequest.getEmail())){
				externalInvites = externalInviteRepository.findByEmailAndStatus(externalInviteRequest.getEmail(),true);
			}
			return ResponseHelper.getSuccessResponse(DockItConstants.USER_INVITE_DETAILS, externalInvites, 200,
					DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response externalInviteAccept(ExternalInviteAcceptRequest externalInviteAcceptRequest) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				Family family = null;
				User user = null;
				Optional<ExternalInvite> externalInviteOpt = null;
				ExternalInvite externalInvite = null;
				Date currentTimeStamp = new Date(System.currentTimeMillis());
				if(!StringUtils.hasText(externalInviteAcceptRequest.getExternalInviteId())) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
				
				if(StringUtils.hasText(externalInviteAcceptRequest.getExternalInviteId())) {
					externalInviteOpt = externalInviteRepository.findById(externalInviteAcceptRequest.getExternalInviteId());
				}
				if(externalInviteOpt.isEmpty()) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.EXTERNAL_INVITE_IS_INVALID,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
				if(null !=externalInviteAcceptRequest.getUserId()) {
					user = userRepository.findById(externalInviteAcceptRequest.getUserId());
					if(null == user) {
						throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
								ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
					}
				}
				externalInvite = externalInviteOpt.get();
				externalInvite.setStatus(false);
				externalInvite.setUpdatedAt(currentTimeStamp);
				externalInviteRepository.save(externalInvite);
				
				Member member = new Member();
				member.setId(UUID.randomUUID().toString());
				member.setFamily(externalInvite.getFamily());
				member.setUser(user);
				member.setInviteStatus(DockItConstants.INVITE_REQUESTED);
				member.setStatus(true);
				member.setCreatedAt(currentTimeStamp);
				member.setUpdatedAt(currentTimeStamp);
				memberRepository.save(member);
				
				return ResponseHelper.getSuccessResponse(DockItConstants.USER_INVITED_SUCCESSFULY, "", 200,
						DockItConstants.RESPONSE_SUCCESS);
	}
	
	

}
