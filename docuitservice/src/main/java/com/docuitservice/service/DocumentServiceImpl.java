package com.docuitservice.service;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.docuitservice.exception.BusinessException;
import com.docuitservice.helper.AmazonClient;
import com.docuitservice.helper.ResponseHelper;
import com.docuitservice.model.Category;
import com.docuitservice.model.Document;
import com.docuitservice.model.Family;
import com.docuitservice.model.Member;
import com.docuitservice.model.Share;
import com.docuitservice.model.User;
import com.docuitservice.repository.CategoryRepository;
import com.docuitservice.repository.DocumentRepository;
import com.docuitservice.repository.FamilyRepository;
import com.docuitservice.repository.MemberRepository;
import com.docuitservice.repository.ShareRepository;
import com.docuitservice.repository.UserRepository;
import com.docuitservice.request.DocumentDetails;
import com.docuitservice.request.SaveDocumentRequest;
import com.docuitservice.request.ShareDocumentRequest;
import com.docuitservice.response.DocumentResponse;
import com.docuitservice.response.UploadResponse;
import com.docuitservice.util.DockItConstants;
import com.docuitservice.util.ErrorConstants;
import com.docuitservice.util.Response;
import com.docuitservice.util.Util;

import jakarta.validation.Valid;


@Service
public class DocumentServiceImpl implements DocumentService{
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	
	@Value("${image.upload.documentFolderName}")
	private String documentFolder;
	
	@Value("${aws.endpointUrl}")
    private String endpointUrl;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	ShareRepository shareRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	FamilyRepository familyRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	AmazonClient amazonClient;
	
	
	    
    static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
   
	
	@Override
	public UploadResponse uploadDocument(MultipartFile file, String userId) throws Exception {
		// TODO Auto-generated method stub
		logger.info("uploadDocument --->starts"+file.getOriginalFilename(),userId);
		long filesize =  file.getSize();
		String fileContentType = file.getContentType();
		//UUID uuid = UUID.randomUUID();
		//String filename = uuid+file.getOriginalFilename();// commented as this is handled in React
		String filename = file.getOriginalFilename();
		String documentPath="";
		String documentUrl= null;
		User user = null;
		
		if (!file.isEmpty() && filesize>0) {
			if (filename.contains("..")) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_FILE,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
            }
			if(!StringUtils.hasLength(userId)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			user = userRepository.findById(userId);
			if(null == user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			//UUID uuid = UUID.randomUUID();
			//String docBucketName=bucketName+"/"+documentPath;
			documentPath = documentFolder +userId;
			documentUrl = amazonClient.uploadFile(file, documentPath,filename);
			
		}else {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.EMPTY_FILE,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		logger.info("uploadDocument --->end");
		return new UploadResponse(filename,documentUrl,fileContentType, filesize);
	}
	
	public Response saveDocumentDetails(SaveDocumentRequest saveDocumentRequest) throws IOException {
		logger.info("saveDocumentDetails --->starts");
		//String documentName = saveDocumentRequest.getDocumentName();
		//String documentUrl=saveDocumentRequest.getDocumentUrl();
		String categoryId = saveDocumentRequest.getCategoryId();
		String familyId = saveDocumentRequest.getFamilyId();
		//String documentType = saveDocumentRequest.getDocumentType();
		String documentUploadedBy = saveDocumentRequest.getUploadedBy();
		//String documentSize = saveDocumentRequest.getDocumentSize();
		List<String> sharedUsers = saveDocumentRequest.getSharedMembers();
		
		Optional<Family> familyOpt = null;
		
		List<DocumentDetails> documentDetails = saveDocumentRequest.getDocumentDetails();
		for(DocumentDetails documentDetail : documentDetails) {
		String documentName = documentDetail.getDocumentName();
		String documentUrl=documentDetail.getDocumentUrl();
		String documentType = documentDetail.getDocumentType();
		String documentSize = documentDetail.getDocumentSize();
		Integer documentPageCount = documentDetail.getPageCount();
		User user =null;
		Document doc =null;  
		Family family = null;
		Category category = null;
		boolean memberMatch = false;
		Member adminMember = null;
		List<Member> sharedList = new ArrayList<>();
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		
			if(!StringUtils.hasLength(documentName)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_NAME_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}/*else if(documentName.length()>DockItConstants.DOCUMENT_NAME_LENGTH || !Util.isAlphaNumeric(documentName)){
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_NAME_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}*/
			if(!StringUtils.hasLength(documentUrl)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_URL_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}else if(!documentUrl.contains(endpointUrl) ){
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_URL_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if(!StringUtils.hasLength(documentSize)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_SIZE_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}if(Long.valueOf(documentSize)<=0) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_SIZE_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}if(!StringUtils.hasLength(documentType)) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_TYPE_IS_NULL,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if(StringUtils.hasText(documentUploadedBy)) {
				user = userRepository.findById(documentUploadedBy);
			}if(null==user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if(StringUtils.hasText(categoryId)) {
				category = categoryRepository.findById(categoryId);
			}if(null== category) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			if(StringUtils.hasText(familyId) && null != sharedUsers && !sharedUsers.isEmpty() && sharedUsers.size()>0 && StringUtils.hasText(sharedUsers.get(0))) {
				familyOpt = familyRepository.findById(familyId);
				family = familyOpt.get();
			}/*if(null== family){
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}*/
			if(StringUtils.hasText(familyId) && null != sharedUsers && !sharedUsers.isEmpty() && sharedUsers.size()>0 && StringUtils.hasText(sharedUsers.get(0))) {
				List<String> docSharedList = new ArrayList<>();
				/*if(sharedUsers.isEmpty()) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}*/
				for(String sharedUser : sharedUsers) {
					if(!StringUtils.hasText(sharedUser)) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
					}
					docSharedList.add(sharedUser);
				}
				//Adding member id of the uploader Begin
				adminMember = memberRepository.findByUserAndFamily(user, family);
				if(null !=adminMember) {
				docSharedList.add(adminMember.getId());
				}
				//Adding member id of the uploader End
				sharedList =  memberRepository.findByIdIn(docSharedList);
				/*if(sharedUsers.size()!=sharedList.size()) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}*/
				for(Member shareMember: sharedList) {
					if(shareMember.getFamily().getId().equalsIgnoreCase(family.getId())) {
						memberMatch = true;
					}if(!memberMatch) {
						throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_NOT_FOUND_IN_FAMILY,
								ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
					}
				}
			}
				 Document docModel = new Document(); 
				 docModel.setId(UUID.randomUUID().toString());
				 docModel.setDocumentName(documentName);
				 docModel.setDocumentType(documentType);
				 docModel.setUrl(documentUrl);
				 docModel.setCategory(category);
				 docModel.setFamily(family);
				 docModel.setDocumentStatus(true);
				 docModel.setDocumentSize(Long.valueOf(documentSize));
				 docModel.setUser(user);
				 docModel.setCreatedAt(currentTimeStamp);
				 docModel.setUpdatedAt(currentTimeStamp);
				 docModel.setPageCount(documentPageCount);
				 doc =  documentRepository.save(docModel);
				 if(!sharedList.isEmpty()) {
				 saveShareDetails(doc,sharedList);
				 }
		}
				 logger.info("saveDocumentDetails --->End");
				 return ResponseHelper.getSuccessResponse(DockItConstants.DOCUMENT_SAVED_SUCCESFULLY, "", 200,
							DockItConstants.RESPONSE_SUCCESS);
	}
	
	public Response shareDocument(ShareDocumentRequest shareDocumentRequest) throws Exception {
		// TODO Auto-generated method stub
		logger.info("shareDocument --->Begin");
		
		Optional<Family> familyOpt = null; 
		Optional<Member> memberOpt= null;
		Optional<Document> documentOpt= null;
		List<Member> memberList = new ArrayList<>();
		List<Member> validMemberList = new ArrayList<>();
		List<String> provideAccessmemberIds = new ArrayList<>();
		List<String> revokeAccessmemberIds = new ArrayList<>();
		List<Member> validProvideAccessmembers = new ArrayList<>();
		List<Member> validRevokeAccessmembers = new ArrayList<>();
		Family family = null;
		String familyId = null;
		User user = null;
		if((StringUtils.hasText(shareDocumentRequest.getCategoryId())|| StringUtils.hasText(shareDocumentRequest.getDocumentName())) && (null==shareDocumentRequest.getProvideAccess() || (null!=shareDocumentRequest.getProvideAccess() && shareDocumentRequest.getProvideAccess().isEmpty())) && (null==shareDocumentRequest.getRevokeAccess() || (null!=shareDocumentRequest.getRevokeAccess() && shareDocumentRequest.getRevokeAccess().isEmpty()))  && !StringUtils.hasText(shareDocumentRequest.getFamilyId())) {
			return updateDocumentCategory(shareDocumentRequest);
		}else {
		//Document document =  shareDocumentRequest.getDocumentId();
		if(null ==shareDocumentRequest.getDocumentId() || null==shareDocumentRequest.getFamilyId() || 
				((null==shareDocumentRequest.getProvideAccess() || ((null!=shareDocumentRequest.getProvideAccess() && shareDocumentRequest.getProvideAccess().isEmpty()) 
				|| (null!=shareDocumentRequest.getProvideAccess() && !shareDocumentRequest.getProvideAccess().isEmpty() && shareDocumentRequest.getProvideAccess().size()==0 )
				|| (null!=shareDocumentRequest.getProvideAccess() && !shareDocumentRequest.getProvideAccess().isEmpty() && shareDocumentRequest.getProvideAccess().size()>0 && !StringUtils.hasText(shareDocumentRequest.getProvideAccess().get(0)))))
				&& (null==shareDocumentRequest.getRevokeAccess() || (null!=shareDocumentRequest.getRevokeAccess() && shareDocumentRequest.getRevokeAccess().isEmpty())
				|| (null!=shareDocumentRequest.getRevokeAccess() && !shareDocumentRequest.getRevokeAccess().isEmpty() && shareDocumentRequest.getRevokeAccess().size()==0)
				|| (null!=shareDocumentRequest.getRevokeAccess() && !shareDocumentRequest.getRevokeAccess().isEmpty() && shareDocumentRequest.getRevokeAccess().size()>0 && !StringUtils.hasText(shareDocumentRequest.getRevokeAccess().get(0)))))) {
			
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
	
		if(null !=shareDocumentRequest.getProvideAccess() && !shareDocumentRequest.getProvideAccess().isEmpty() && shareDocumentRequest.getProvideAccess().size()>0 && StringUtils.hasText(shareDocumentRequest.getProvideAccess().get(0))) {
			for(String memberId : shareDocumentRequest.getProvideAccess()) {
				provideAccessmemberIds.add(memberId);
			}
			Iterator<String> iterator = 	provideAccessmemberIds.iterator();
			memberList = memberRepository.findAllById(iteratorToIterable(iterator));
			if(memberList.isEmpty() || memberList.size()!= shareDocumentRequest.getProvideAccess().size()) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			for(Member members: memberList) {
				if(members.getInviteStatus().equalsIgnoreCase("accepted") ) {
					validProvideAccessmembers.add(members);
				}
			}
			}
		
		if(null !=shareDocumentRequest.getRevokeAccess() && !shareDocumentRequest.getRevokeAccess().isEmpty() && shareDocumentRequest.getRevokeAccess().size()>0 && StringUtils.hasText(shareDocumentRequest.getRevokeAccess().get(0))) {
			for(String memberId : shareDocumentRequest.getRevokeAccess()) {
				revokeAccessmemberIds.add(memberId);
			}
			Iterator<String> iterator = 	revokeAccessmemberIds.iterator();
			memberList = memberRepository.findAllById(iteratorToIterable(iterator));
			if(memberList.isEmpty() || memberList.size()!= shareDocumentRequest.getRevokeAccess().size()) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_ID_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			for(Member members: memberList) {
				if(members.getInviteStatus().equalsIgnoreCase("accepted") ) {
					validRevokeAccessmembers.add(members);
				}
			}
			}
		
		if(null !=shareDocumentRequest.getFamilyId()) {
			familyOpt = familyRepository.findById(shareDocumentRequest.getFamilyId());
			if(null ==familyOpt|| (null !=familyOpt && familyOpt.isEmpty())) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.FAMILY_NOT_FOUND,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
			family = familyOpt.get();
		}
		if(null !=shareDocumentRequest.getDocumentId()) {
			documentOpt = documentRepository.findById(shareDocumentRequest.getDocumentId());
			if(documentOpt.isEmpty()) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		
		if (null != family) {
			familyId =family.getId();
			for(Member member: memberList) {
				if(!familyId.equalsIgnoreCase(member.getFamily().getId())) {
					throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.MEMBER_NOT_FOUND_IN_FAMILY,
							ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
				}
			}
			
		}
		
		/*if(StringUtils.hasText(shareDocumentRequest.getUpdatedBy())) {
			user = userRepository.findById(shareDocumentRequest.getUpdatedBy());
		}if(null==user) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		Member member = memberRepository.findByUserAndFamily(user,family);
		if(null !=member && !familyId.equalsIgnoreCase(member.getFamily().getId())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.UPDATEDBY_NOT_FOUND_IN_FAMILY,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}*/
		
		if(null != shareDocumentRequest.getProvideAccess() && !shareDocumentRequest.getProvideAccess().isEmpty() && !validProvideAccessmembers.isEmpty()) {
			saveShareDetails(documentOpt.get(), validProvideAccessmembers);
		}if(null!=shareDocumentRequest.getRevokeAccess() && !shareDocumentRequest.getRevokeAccess().isEmpty() && !validRevokeAccessmembers.isEmpty()) {
			removeDocumentAccess(documentOpt.get(), validRevokeAccessmembers);
		}
		
	}
		logger.info("shareDocument --->End");
		return ResponseHelper.getSuccessResponse(DockItConstants.DOCUMENT_UPDATED_SUCCESFULLY, "", 200,
				DockItConstants.RESPONSE_SUCCESS);
	
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
	private void saveShareDetails(Document document, List<Member> memberList) {
		logger.info("saveShareDetails --->Begin");
		List<Share> shareList =new ArrayList<>();
		Date currentTimeStamp = new Date(System.currentTimeMillis());
		for(Member member : memberList) {
		Share share = new Share();
		share.setId(UUID.randomUUID().toString());
		share.setDocument(document);
		//share.setFamily(family);
		share.setMember(member);
	    share.setUser(document.getUser());
	    share.setCreatedAt(currentTimeStamp);
	    share.setUpdatedAt(currentTimeStamp);
	    shareList.add(share);
		}
	    shareRepository.saveAll(shareList);
	    logger.info("saveShareDetails --->End");
	}
	private void removeDocumentAccess(Document document, List<Member> memberList) {
		logger.info("saveShareDetails --->Begin");
		List<Share> shareList =new ArrayList<>(); 
		shareList = shareRepository.findAllByDocumentAndMemberIn(document,memberList);
		List<String> shareIds = new ArrayList<String>();
		for(Share shares :shareList) {
			shareIds.add(shares.getId());
			
		}
		shareRepository.flush();
	    if(!shareList.isEmpty()) {
		shareRepository.deleteAllByIdInBatch(iteratorToIterable(shareIds.iterator()));
	    }
	    logger.info("saveShareDetails --->End");
	}
	
	public void removeDocumentAccessByMemberIds(List<String> memberIds) {
		logger.info("removeDocumentAccessByMemberIds --->Begin");
		List<Share> shareList =new ArrayList<>(); 
		shareList = shareRepository.findByMemberIdIn(memberIds);
		List<String> shareIds = new ArrayList<String>();
		for(Share shares :shareList) {
			shareIds.add(shares.getId());
		}
		shareRepository.flush();
	    if(!shareList.isEmpty()) {
		shareRepository.deleteAllByIdInBatch(iteratorToIterable(shareIds.iterator()));
	    }
	    logger.info("removeDocumentAccessByMemberIds --->End");
	}
	
	public void deleteDocumentsBasedonMemberAndFamily(List<Member> members) {
		logger.info("deleteDocumentsBasedonMemberAndFamily --->Begin");
		List<Document> docList =new ArrayList<>(); 
	
		for(Member member : members) {
			if(null != member.getFamily() && null!= member.getUser()) {
				docList = documentRepository.findByFamilyIdAndUserId(member.getFamily().getId(),member.getUser().getId());
				deleteDocument(docList);
			}
		}
	
	    logger.info("deleteDocumentsBasedonMemberAndFamily --->End");
	}
	
	public void deleteDocument(List<Document> docList) {
		logger.info("deleteDocument --->Begin");
		
		List<String> documentIdList = new ArrayList<String>();
		documentIdList = docList.stream().map(x->x.getId()).collect(Collectors.toList());
		documentRepository.flush();
		if(!documentIdList.isEmpty()) {
			documentRepository.deleteAllByIdInBatch(iteratorToIterable(documentIdList.iterator()));
		}
		logger.info("deleteDocument --->End");
	}

	@Override
	public Response getDocumentShared(String documentId) throws Exception {
		logger.info("getDocumentShared --->Begin");
		Optional<Document> documentOpt= null;
		List<Share> shareList = new ArrayList<Share>();
		List<User> documentSharedToUsers = new ArrayList<User>();
		Map<String,Object> documentSharedDetails = new HashMap<String, Object>();
	
		if(!StringUtils.hasText(documentId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		documentOpt = documentRepository.findById(documentId);
		if(documentOpt.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		shareList = shareRepository.findByDocumentId(documentId);
		for(Share share: shareList) {
			documentSharedToUsers.add(share.getUser());
		}
		documentSharedDetails.put("documentDetails", documentResponseBuilder(formatter,documentOpt.get()));
		documentSharedDetails.put("documentSharedUserList",documentSharedToUsers);
		
		// TODO Auto-generated method stub
		logger.info("getDocumentShared --->End");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, documentSharedDetails , 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response getDocumentDetails(String documentId) throws Exception {
		logger.info("getDocumentDetails --->Begin");
		Optional<Document> documentOpt = null;
		Map<String, Object> documentDtlsmap = new HashMap<String, Object>();
		List<Share> shareList = new ArrayList<Share>();
		List<User> documentSharedToUsers = new ArrayList<User>();
		Map<String, Object> documentSharedDetails = new HashMap<String, Object>();

		if (!StringUtils.hasText(documentId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		documentOpt = documentRepository.findById(documentId);
		if (documentOpt.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		documentDtlsmap.put("documentDetails", documentOpt.get());
		documentDtlsmap.put("sharedDetails", getShareDetails(documentId));
		logger.info("getDocumentDetails --->End");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, documentDtlsmap, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}

	public List<Share> getShareDetails(String documentId) throws Exception {
		List<Share> shareDtls = new ArrayList<>();
		shareDtls = shareRepository.findByDocumentId(documentId);

		return shareDtls;
	}


	@Override
	public Response getDocumentList(String userId) {
		logger.info("getDocumentList --->Begin");
		User user = null;
		Long memberId = 0L;
		List<Document> myDocumentList = new ArrayList(); 
		List<Document> sharedDocumentList = new ArrayList(); 
		List<Share> shareList = new ArrayList(); 
		List<Member> members = null;
		List<String> memberIds = new ArrayList<String>();
		Map<String,List<Document>> documents = new HashMap();
		if(!StringUtils.hasText(userId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}else {
			user = userRepository.findById(userId);
			if(null == user) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.USER_ID_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}else {
				myDocumentList = documentRepository.findByUserIdAndDocumentStatus(userId,true);
				//documents.put("MyDocuments", documentResponseMapper(myDocumentList));
				documents.put("MyDocuments", myDocumentList);
				members =  memberRepository.findByUserId(userId);
				
				if(null != members && !members.isEmpty())
					for(Member member : members) {
						memberIds.add(member.getId());
					}
				shareList = shareRepository.findByMemberIdIn(memberIds);
				System.out.println(shareList);
				for(Share share :shareList) {
					if(share.getMember().getUser().getId().equalsIgnoreCase(userId) && share.getDocument().isDocumentStatus()){
						sharedDocumentList.add(share.getDocument());
					}
				}
				//documents.put("sharedDcoumets", documentResponseMapper(sharedDocumentList));
				documents.put("sharedDcoumets", sharedDocumentList);
				
			}
		}
		// TODO Auto-generated method stub
		logger.info("getDocumentList --->End");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, documents, 200,
				DockItConstants.RESPONSE_SUCCESS);
	}


	@Override
	public Response deleteDocument(String documentId) {
		logger.info("deleteDocument --->Begin");
		Optional<Document> documentOpt= null;
		List<Share> shareList = new ArrayList<Share>();
		List<User> documentSharedToUsers = new ArrayList<User>();
		Map<String,Object> documentSharedDetails = new HashMap<String, Object>();
		
		String responseStatus=null;
	
		if(!StringUtils.hasText(documentId)) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		documentOpt = documentRepository.findById(documentId);
		if(documentOpt.isEmpty()) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		
		Document document = documentOpt.get();
		shareList = shareRepository.findByDocumentId(documentId);
		
		List<String> shareIds = new ArrayList<String>();
		for(Share shares :shareList) {
			shareIds.add(shares.getId());
			
		}
		shareRepository.flush();
	    if(!shareList.isEmpty()) {
		shareRepository.deleteAllByIdInBatch(iteratorToIterable(shareIds.iterator()));
	    }
		
		// TODO Auto-generated method stub
	    //deleteing the document
		documentRepository.delete(document);
		
		documentOpt = documentRepository.findById(documentId);
		if(!documentOpt.isPresent()) {
			responseStatus=DockItConstants.DOCUMENT_DELETED_SUCCESFULLY;
		}else {
			responseStatus=DockItConstants.DOCUMENT_DELETION_UNSUCCESFULLY;
		}
		logger.info("deleteDocument --->End");
		return ResponseHelper.getSuccessResponse(responseStatus, "" , 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	@Override
	public Response getUserLastDocumentActivity(String userId) {
		logger.info("getUserLastDocumentActivity --->Begin");
		List<Document> documentList= new ArrayList<>();
		documentList = documentRepository.getMaxDateDocument(userId);
		
		logger.info("getUserLastDocumentActivity --->End");
		return ResponseHelper.getSuccessResponse(DockItConstants.FETCH_DATA, documentList , 200,
				DockItConstants.RESPONSE_SUCCESS);
	}
	
	

	@Override
	public Response updateDocumentCategory(@Valid ShareDocumentRequest shareDocumentRequest) {
		// TODO Auto-generated method stub
		Optional<Document> documentOpt = null;
		Category category = null;
		String responseStatus = null;
	
		Date currentTimeStamp = new Date(System.currentTimeMillis());

		if ((null == shareDocumentRequest.getDocumentId() || null == shareDocumentRequest.getCategoryId()) || null == shareDocumentRequest.getDocumentName()) {

			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.INVALID_INPUT,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}

		if (null != shareDocumentRequest.getDocumentId()) {
			documentOpt = documentRepository.findById(shareDocumentRequest.getDocumentId());
			if (documentOpt.isEmpty()) {
				throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_IS_INVALID,
						ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
			}
		}
		if (StringUtils.hasText(shareDocumentRequest.getCategoryId())) {
			category = categoryRepository.findById(shareDocumentRequest.getCategoryId());
		}
		if (null == category) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.CATEGORY_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
		if (!StringUtils.hasText(shareDocumentRequest.getDocumentName())) {
			throw new BusinessException(ErrorConstants.RESPONSE_FAIL, ErrorConstants.DOCUMENT_NAME_IS_INVALID,
					ErrorConstants.RESPONSE_EMPTY_DATA, 1001);
		}
				
		Document document = documentOpt.get();
		document.setCategory(category);
		if (StringUtils.hasText(shareDocumentRequest.getDocumentName())) {
			document.setDocumentName(shareDocumentRequest.getDocumentName());
		}
		document.setUpdatedAt(currentTimeStamp);
		documentRepository.save(document);
		responseStatus = DockItConstants.DOCUMENT_UPDATED_SUCCESFULLY;

		return ResponseHelper.getSuccessResponse(responseStatus, document, 200, DockItConstants.RESPONSE_SUCCESS);
	}

	private List<DocumentResponse> documentResponseMapper(List<Document> myDocumentList) {
		// TODO Auto-generated method stub
		logger.info("documentResponseMapper --->Begin");
		List<DocumentResponse> docResponse= new ArrayList<>();
		 
		for(Document document : myDocumentList) {
			DocumentResponse documentResponse = documentResponseBuilder(formatter, document);
			docResponse.add(documentResponse);
		}
		logger.info("documentResponseMapper --->End");
		return docResponse;
	}


	/**
	 * @param formatter
	 * @param document
	 * @return
	 */
	private DocumentResponse documentResponseBuilder(SimpleDateFormat formatter, Document document) {
		logger.info("documentResponseBuilder --->Begin");
		DocumentResponse documentResponse = new DocumentResponse();
		
		documentResponse.setDocumentId(document.getId());
		documentResponse.setDocumentName(document.getDocumentName());
		documentResponse.setDocumentUrl(document.getUrl());
		documentResponse.setFamilyId(document.getFamily().getId());
		documentResponse.setFamilyName(document.getFamily().getName());
		documentResponse.setCreatedDate(formatter.format(document.getCreatedAt()));
		documentResponse.setUploadedBy(String.valueOf(document.getUser().getId()));
		documentResponse.setCategoryName(document.getCategory().getCategoryName());
		logger.info("documentResponseBuilder --->End");
		return documentResponse;
	}
	
	
	

}
