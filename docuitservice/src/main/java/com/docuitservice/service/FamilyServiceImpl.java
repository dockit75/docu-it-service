package com.docuitservice.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.docuitservice.request.CommonInviteRequest;
import com.docuitservice.request.DeleteFamilyRequest;
import com.docuitservice.request.EditFamilyRequest;
import com.docuitservice.request.ExternalInviteAcceptRequest;
import com.docuitservice.request.ExternalInviteRequest;
import com.docuitservice.request.FamilyMemberInviteAcceptedRequest;
import com.docuitservice.request.FamilyMemberInviteRequest;
import com.docuitservice.request.FamilyRequest;
import com.docuitservice.request.MemberOperationRequest;
import com.docuitservice.response.FamilyDetails;
import com.docuitservice.response.FamilyAndMemberResponse;
import com.docuitservice.response.MemberResponse;
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
	
	@Autowired
	DocumentService documentService;

	@Override
	public Response addFamily(@Valid FamilyRequest familyRequest) throws Exception {
		logger.info("FamilyServiceImpl addFamily ---Start---");
		Util.validateRequiredField(familyRequest.getName(), ErrorConstants.FAMILY_NAME_IS_REQUIRED);
		Util.validateRequiredField(familyRequest.getAdminId(), ErrorConstants.ADMIN_ID_IS_REQUIRED);
		if (!Util.isValidNameFormat(familyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_FAMILY_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Family familyVO = familyRepository.findByUserIdAndNameIgnoreCase(familyRequest.getAdminId(),
				familyRequest.getName());
		if (familyVO != null && familyVO.getName().equalsIgnoreCase(familyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_NAME_ALREADY_REGISTERED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Map<String, Object> responseObjectsMap = new HashMap<>();
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
			member.setInvitedBy(user);
			memberRepository.save(member);
			List<Member> familyMembers = memberRepository.findByFamily(family);
			FamilyDetails familyDetails = ResponseHelper.setFamilyDetailsResponse(family,familyMembers);
			responseObjectsMap.put("familyDetails", familyDetails);
		} else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("FamilyServiceImpl addFamily ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_REGISTERED_SUCCESSFULLY, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response editFamily(@Valid EditFamilyRequest editFamilyRequest) throws Exception {
		logger.info("FamilyServiceImpl editFamily ---Start---");
		Optional<Family> familyOpt = null;
		if (editFamilyRequest == null) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Util.validateRequiredField(editFamilyRequest.getName(), ErrorConstants.FAMILY_NAME_IS_REQUIRED);
		Util.validateRequiredField(editFamilyRequest.getFamilyId(), ErrorConstants.FAMILY_ID_IS_REQUIRED);
		Util.validateRequiredField(editFamilyRequest.getAdminId(), ErrorConstants.ADMIN_ID_IS_REQUIRED);
		if (!Util.isValidNameFormat(editFamilyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_FAMILY_NAME,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Family familyVO = familyRepository.findByUserIdAndNameIgnoreCase(editFamilyRequest.getAdminId(),
				editFamilyRequest.getName());
		if (familyVO != null && familyVO.getName().equalsIgnoreCase(editFamilyRequest.getName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_NAME_ALREADY_REGISTERED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		familyOpt = familyRepository.findById(editFamilyRequest.getFamilyId());
		if(null== familyOpt || (null!= familyOpt && familyOpt.isEmpty())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Family family = familyOpt.get();
		
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
//		List<Family> familyList = familyRepository.findByUserIdAndStatus(adminId,true);
		List<Member> memberList = memberRepository.findByUserIdAndInviteStatus(adminId,DockItConstants.INVITE_ACCEPTED);
		if (memberList == null || memberList.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_NOT_FOUND_IN_FAMILY,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		List<Family> familyList = ResponseHelper.setFamilyVO(memberList);
		Map<String, Object> responseObjectsMap = new HashMap<>();
		responseObjectsMap.put("familyList", familyList);
		logger.info("FamilyServiceImpl listFamily ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.RESPONSE_SUCCESS, responseObjectsMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response familyMemberInvite(FamilyMemberInviteRequest familyMemberInviteRequest) {
		logger.info("FamilyServiceImpl familyMemberInvite ---Start---");
		// TODO Auto-generated method stub
		User user=null;
		Family family=null;
		List<String> userIds = null;
		User inviteBy = null;
		Optional<Family> familyOpt = null;
		List<Member> memberList = new ArrayList<Member>();
		if(!StringUtils.hasLength(familyMemberInviteRequest.getFamilyId()) || null == familyMemberInviteRequest.getUserIds() || (null != familyMemberInviteRequest.getUserIds() && familyMemberInviteRequest.getUserIds().isEmpty()) || !StringUtils.hasText(familyMemberInviteRequest.getInvitedBy())) {
			
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(null !=familyMemberInviteRequest.getFamilyId()) {
		
			
			familyOpt = familyRepository.findById(familyMemberInviteRequest.getFamilyId());
			if(null== familyOpt || (null!= familyOpt && familyOpt.isEmpty())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			family = familyOpt.get();
		}
		if(null !=familyMemberInviteRequest.getUserIds()) {
			userIds = familyMemberInviteRequest.getUserIds();
			if(userIds.contains(familyMemberInviteRequest.getInvitedBy())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INVITE,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		if(StringUtils.hasText(familyMemberInviteRequest.getInvitedBy())) {
			inviteBy = userRepository.findById(familyMemberInviteRequest.getInvitedBy());
			if(null ==inviteBy) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_USER_ID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		for(String userId : userIds) {
			if(StringUtils.hasText(userId)) {
			user = userRepository.findById(userId);
			if(null ==user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			//family = familyOpt.get();
			memberList = memberRepository.findByUserAndFamilyAndInviteStatusNot(user, family, DockItConstants.INVITE_REJECTED);
			if(!memberList.isEmpty()) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL,user.getName()+"("+"Phone-"+user.getPhone() +")"+" "+ErrorConstants.MEMBER_ALREADY_IN_THIS_FAMILY,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
				
			Member member = new Member();
			member.setId(UUID.randomUUID().toString());
			member.setFamily(family);
			member.setUser(user);
			member.setInviteStatus(DockItConstants.INVITE_REQUESTED);
			member.setStatus(true);
			member.setCreatedAt(currentTimeStamp);
			member.setUpdatedAt(currentTimeStamp);
			member.setInvitedBy(inviteBy);
			memberRepository.save(member);
			}
		}
		logger.info("FamilyServiceImpl familyMemberInvite ---Start---");
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
			familyOpt = familyRepository.findById(familyMemberInviteAcceptedRequest.getFamilyId());
			if(null ==familyOpt || (null !=familyOpt && familyOpt.isEmpty())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			family = familyOpt.get();
		}
		
		if(StringUtils.hasLength(familyMemberInviteAcceptedRequest.getInviteStatus()) && (!familyMemberInviteAcceptedRequest.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_ACCEPTED) && !familyMemberInviteAcceptedRequest.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_REJECTED))) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		//family = familyOpt.get();
		member = memberRepository.findByUserAndFamily(user,family);
		//member = memberRepository.findById(id);
		if(null == member) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(member.getInviteStatus().equalsIgnoreCase(DockItConstants.INVITE_ACCEPTED)) {
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
	public Response getFamilyMembersList(String familyId) throws Exception {
		// TODO Auto-generated method stub
		Family family = null;
		Optional<Family> familyOpt =  null;
		Map<String, Object> response = new HashMap<>();

		if (!StringUtils.hasLength(familyId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		familyOpt = familyRepository.findById(familyId);
		if(null ==familyOpt || (null !=familyOpt && familyOpt.isEmpty())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		family = familyOpt.get();
		response.put("MemberList",
				memberRepository.findByFamilyAndInviteStatusNot(family, DockItConstants.INVITE_REJECTED));
		response.put("ExternalInvites", getExternalInviteByInviter(family.getUser().getId(), family.getId()));

		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_MEMBERS_LIST, response, 200,
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
		return ResponseHelper.getSuccessResponse(DockItConstants.INVITES_PENDING_LIST, members, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response externalInvite(ExternalInviteRequest externalInviteRequest) throws Exception {
		// TODO Auto-generated method stub
		logger.info("FamilyServiceImpl externalInvite ---Start---");
		Family family = null;
		Optional<Family> familyOpt =  null;
		User user = null;
		User checkUserbyPhone = null;
		Optional<User> checkUserbyEmail = null;
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		if(StringUtils.hasText(externalInviteRequest.getEmail()) || StringUtils.hasText(externalInviteRequest.getPhone())) {
			logger.info("FamilyServiceImpl externalInvite ---Either Email or Phone number is present ---");
		}else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			
		}
		if (StringUtils.hasText(externalInviteRequest.getPhone()) && ! Util.isValidPhoneNumberFormat(externalInviteRequest.getPhone())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_PHONE_NUMBER,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		else if (StringUtils.hasText(externalInviteRequest.getEmail()) && !Util.isValidEmailIdFormat(externalInviteRequest.getEmail())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_EMAIL_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(null !=externalInviteRequest.getFamilyId()) {
			familyOpt =  familyRepository.findById(externalInviteRequest.getFamilyId());
			if(null ==familyOpt || (null !=familyOpt && familyOpt.isEmpty())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			family = familyOpt.get();
		}
		if(null !=externalInviteRequest.getInvitedBy()) {
			user = userRepository.findById(externalInviteRequest.getInvitedBy());
			if(null == user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		if (StringUtils.hasText(externalInviteRequest.getPhone())) {
			checkUserbyPhone= userRepository.findByPhone(externalInviteRequest.getPhone());
		}
		if (StringUtils.hasText(externalInviteRequest.getEmail())) {
			checkUserbyEmail= userRepository.findByEmail(externalInviteRequest.getEmail());
		}
		if(StringUtils.hasText(externalInviteRequest.getPhone()) && null != checkUserbyPhone) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_WITH_PHONE_EXISTS,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (StringUtils.hasText(externalInviteRequest.getEmail()) && checkUserbyEmail.isPresent()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_WITH_EMAIL_EXISTS,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
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
		if(StringUtils.hasText(externalInviteRequest.getEmail())) {
		userService.sendEmailInvite(externalInvite.getEmail(), user.getName());
		}
		if (externalInvite.getPhone() != null && !externalInvite.getPhone().isEmpty()) {
			userService.sendSmsInvite(externalInvite.getPhone(), user.getName());
		}
		logger.info("FamilyServiceImpl externalInvite ---End---");
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
	public List<Member> externalInviteAccept(ExternalInviteAcceptRequest externalInviteAcceptRequest) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				Family family = null;
				User user = null;
				List<Member> memberList =  new ArrayList<>();
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
				family = externalInvite.getFamily();
				memberList = memberRepository.findByUserAndFamilyAndInviteStatusNot(user, family, DockItConstants.INVITE_REJECTED);
				if(null == memberList || (null != memberList && memberList.isEmpty())) {
				Member member = new Member();
				member.setId(UUID.randomUUID().toString());
				member.setFamily(externalInvite.getFamily());
				member.setUser(user);
				member.setInviteStatus(DockItConstants.INVITE_REQUESTED);
				member.setStatus(true);
				member.setCreatedAt(currentTimeStamp);
				member.setUpdatedAt(currentTimeStamp);
				member.setInvitedBy(externalInvite.getUser());
				memberRepository.save(member);
				}
				return memberList;
	}

	@Override
	public Response familyMemberCommonInvite(CommonInviteRequest commonInviteRequest) throws Exception {
		User user = null;
		User invitedBy = null;
		Family family = null;
		Optional<Family> familyOpt =  null;
		FamilyMemberInviteRequest familyMemberInviteRequest = new FamilyMemberInviteRequest();
		List<String> phoneNoList =  new ArrayList<String>();
		List<String> userList =  new ArrayList<String>();
		if(null !=commonInviteRequest.getPhoneNumbers() && commonInviteRequest.getPhoneNumbers().isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(null !=commonInviteRequest.getFamilyId()) {
			familyOpt = familyRepository.findById(commonInviteRequest.getFamilyId());
			if(null ==familyOpt || (null !=familyOpt && familyOpt.isEmpty())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			family = familyOpt.get();
		}
		if(null !=commonInviteRequest.getInvitedBy()) {
			invitedBy = userRepository.findById(commonInviteRequest.getInvitedBy());
			if(null == invitedBy) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVITED_BY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		phoneNoList = commonInviteRequest.getPhoneNumbers();
		
		for(String phoneNo : phoneNoList) {
			if(StringUtils.hasText(phoneNo)) {
			user = userRepository.findByPhone(phoneNo);
			}
			if(null != user && null !=user.getId()) {
				userList.add(user.getId());
			}else {
				ExternalInviteRequest externalInviteRequest = new ExternalInviteRequest();
				externalInviteRequest.setPhone(phoneNo);
				externalInviteRequest.setFamilyId(family.getId());
				externalInviteRequest.setInvitedBy(invitedBy.getId());
				externalInvite(externalInviteRequest);
			}
		}
		if(null != userList && !userList.isEmpty()) {
		familyMemberInviteRequest.setUserIds(userList);
		familyMemberInviteRequest.setFamilyId(family.getId());
		familyMemberInviteRequest.setInvitedBy(invitedBy.getId());
		familyMemberInvite(familyMemberInviteRequest);
		}
	
		return ResponseHelper.getSuccessResponse(DockItConstants.USER_INVITED_SUCCESSFULY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	@Override
	public Response removeFamilyMemebers(MemberOperationRequest memberOperationRequest) {
		logger.info("FamilyServiceImpl removeFamilyMemebers ---Start---");
		List<String> memberIds = new ArrayList<String>();
		List<Member> memberList = new ArrayList<>();
		
		if(null == memberOperationRequest.getMemberIds() || (null != memberOperationRequest.getMemberIds() && memberOperationRequest.getMemberIds().isEmpty())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		if(!memberOperationRequest.getMemberIds().isEmpty()) {
			memberIds = memberOperationRequest.getMemberIds();
		}
		memberList.addAll(memberRepository.findAllById(iteratorToIterable(memberIds.iterator())));
		if(null!=memberList && memberList.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		documentService.removeDocumentAccessByMemberIds(memberIds);
		//documentService.deleteDocumentsBasedonMemberAndFamily(memberList);
		 memberRepository.flush();
	    if(!memberIds.isEmpty()) {
	    	//memberRepository.deleteAllByIdInBatch(iteratorToIterable(memberIds.iterator()));
	    	memberRepository.deleteAllByIdInBatch(iteratorToIterable(memberIds.iterator()));
	    }
	    logger.info("FamilyServiceImpl removeFamilyMemebers ---End---");
	    return ResponseHelper.getSuccessResponse(DockItConstants.MEMBERS_DELETED_SUCCESSFULY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
		
	}
	
	
	@Override
	public Response deleteFamily(@Valid DeleteFamilyRequest deleteFamilyRequest) throws Exception {
		logger.info("FamilyServiceImpl deleteFamily ---Start---");
		User user =  null;
		Optional<Family> familyOpt = null;
		Family family = null;
		List<Member> memberList = new ArrayList<Member>();
		List<String> memberIdList = null;
		List<ExternalInvite> externalInviteList = new ArrayList<ExternalInvite>();
		List<String> externalIdList = null;
		String familyId = deleteFamilyRequest.getFamilyId();
		MemberOperationRequest memberOperationRequest =  new MemberOperationRequest();
		if (deleteFamilyRequest == null) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_REQUEST,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(!StringUtils.hasText(deleteFamilyRequest.getFamilyId())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_ID_IS_REQUIRED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if(!StringUtils.hasText(deleteFamilyRequest.getAdminId())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.ADMIN_ID_IS_REQUIRED,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		user = userRepository.findById(deleteFamilyRequest.getAdminId());
		if(null== user) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_ADMIN_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		familyOpt = familyRepository.findById(familyId);
		if(null== familyOpt || (null!= familyOpt && familyOpt.isEmpty())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		family = familyOpt.get();
		memberList = memberRepository.findByFamily(family);
		user = userRepository.findById(deleteFamilyRequest.getAdminId());
		memberIdList = memberList.stream().map(x->x.getId()).collect(Collectors.toList());
		if(null !=memberIdList && !memberIdList.isEmpty()) {
		memberOperationRequest.setMemberIds(memberIdList);
		removeFamilyMemebers(memberOperationRequest);
		}
		externalInviteList = externalInviteRepository.findByFamily(family);
		externalIdList = externalInviteList.stream().map(x->x.getId()).collect(Collectors.toList());
		if(null != externalIdList && !externalIdList.isEmpty()) {
			externalInviteRepository.deleteAllByIdInBatch(iteratorToIterable(externalIdList.iterator()));
		}
		//familyRepository.deleteByFamilyId(familyId);
		familyRepository.deleteById(familyId);
		
		logger.info("FamilyServiceImpl deleteFamily ---End---");
		return ResponseHelper.getSuccessResponse(DockItConstants.FAMILY_DELETED_SUCCESSFULY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	
	public List<String> getExternalInviteByInviter(String invitedBy,String familyId) throws Exception {
		logger.info("FamilyServiceImpl getExternalInviteByInviter ---Begin---");
		User user = null;
		Family family = null;
		Optional<Family> familyOpt = null;
		String userId = null;
		List<String> ExternalInvite = null;
		if(!StringUtils.hasText(invitedBy) && !StringUtils.hasText(familyId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		user = userRepository.findById(invitedBy);
		if(null== user) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_USER_ID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		familyOpt = familyRepository.findById(familyId);
		if(null ==familyOpt || (null !=familyOpt && familyOpt.isEmpty())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		family= familyOpt.get();
	
		userId=user.getId();
		ExternalInvite = externalInviteRepository.findByStatusAndUserAndFamilyDistinctByPhone(userId,familyId);
	//	ExternalInvite = externalInviteRepository.findDistinctByPhone();
		logger.info("FamilyServiceImpl getExternalInviteByInviter ---End---"+ExternalInvite);
		return ExternalInvite;
		
	}
	

	public static<T> Iterable<T> iteratorToIterable(Iterator<T> iterator)
    {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

	@Override
	public Response getFamilyWithMembers(String adminId) throws Exception {
		logger.info("FamilyServiceImpl getFamilyWithMembers ---Start---");
		Util.validateRequiredField(adminId, ErrorConstants.ADMIN_ID_IS_REQUIRED);
		User user = userRepository.findById(adminId);
		if (user == null) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_DETAIL_NOT_FOUND,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		List<Member> memberList = memberRepository.findByInvitedBy_IdAndInviteStatus(adminId,
				DockItConstants.INVITE_ACCEPTED);
		List<FamilyAndMemberResponse> familyListWithMembers = new ArrayList<>();
		Set<String> processedFamilyIds = new HashSet<>();
		for (Member member : memberList) {
			Family family = member.getFamily();
			if (family != null && !processedFamilyIds.contains(family.getId())) {
				FamilyAndMemberResponse familyAndMemberResponseVO = new FamilyAndMemberResponse();
				familyAndMemberResponseVO.setId(family.getId());
				familyAndMemberResponseVO.setName(family.getName());
				familyAndMemberResponseVO.setStatus(family.getStatus());
				familyAndMemberResponseVO.setCreatedBy(family.getUser().getId());
				List<MemberResponse> memberResponses = memberList.stream()
						.filter(m -> m.getFamily().getId().equals(family.getId()))
						.map(this::convertMemberToMemberResponse).collect(Collectors.toList());
				familyAndMemberResponseVO.setMembersList(memberResponses);
				familyListWithMembers.add(familyAndMemberResponseVO);
				processedFamilyIds.add(family.getId());
			}
		}
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("familyListWithMembers", familyListWithMembers);
		return ResponseHelper.getSuccessResponse(DockItConstants.RESPONSE_SUCCESS, responseMap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	private MemberResponse convertMemberToMemberResponse(Member member) {
		MemberResponse memberResponse = new MemberResponse();
		memberResponse.setId(member.getId());
		memberResponse.setInviteStatus(member.getInviteStatus());
		memberResponse.setStatus(member.getStatus());
		memberResponse.setUser(member.getUser());
		return memberResponse;
	}

}
