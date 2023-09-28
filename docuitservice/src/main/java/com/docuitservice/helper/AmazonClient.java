package com.docuitservice.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import jakarta.annotation.PostConstruct;

@Service
public class AmazonClient {
	
	 private AmazonS3 s3client;
	 
	 private static final Logger logger = LoggerFactory.getLogger(AmazonClient.class);
	 
	 	@Value("${aws.endpointUrl}")
	    private String endpointUrl;
	    @Value("${aws.region}")
	    private String region;
	    @Value("${aws.s3.bucket}")
	    private String bucketName;
	    @Value("${aws.accessKey}")
	    private String accessKey;
	    @Value("${aws.secretKey}")
	    private String secretKey;
	    
	    private static String forwardSlash = "/";

	  //  @Value("${aws.s3.document.folder}")
		//private String documentFolder;

	 
	 @PostConstruct
	    private void initializeAmazon() {
	        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
	        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
	        AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
	                this.endpointUrl, this.region);
	        this.s3client = AmazonS3ClientBuilder
	                .standard()
	                .withEndpointConfiguration(endpointConfig)
	                .withCredentials(credentialsProvider)
	                .enablePathStyleAccess()
	                .build();
	    }
	 
	 public String uploadFile(MultipartFile multipartFile,String documentPath,String fileName) {
	        String fileUrl = "";
	        try {
	            File file = convertMultiPartToFile(multipartFile);
	            String docBucketName=bucketName+forwardSlash+documentPath;
	            fileUrl = endpointUrl+forwardSlash+docBucketName+forwardSlash+fileName;
	            uploadFileTos3bucket(docBucketName,fileName, file);
	            file.delete();
	        } catch (Exception e) {
	           e.printStackTrace();
	        }
	        return fileUrl;
	    }
	 
	 private void uploadFileTos3bucket(String docBucketName, String fileName, File file) {
	    	//String docBucketName=bucketName+"/"+documentPath;
	        s3client.putObject(new PutObjectRequest(docBucketName, fileName, file)
	                .withCannedAcl(CannedAccessControlList.PublicRead));
	    }
	 
	private File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
		  if(null !=multipartFile) {
	    	File convFile = new File(multipartFile.getOriginalFilename());
	        FileOutputStream fos = new FileOutputStream(convFile);
	        fos.write(multipartFile.getBytes());
	        fos.close();
	    	return convFile;
	   	 }
		return null;
	   	 
	   	 }
	  

	   public Object downloadFile(String fileName) throws Exception, IOException {
	       if (bucketIsEmpty()) {
	           throw new Exception("Requested bucket does not exist or is empty");
	       }
	       S3Object object = s3client.getObject(bucketName, fileName);
	       try (S3ObjectInputStream s3is = object.getObjectContent()) {
	           try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
	               byte[] read_buf = new byte[1024];
	               int read_len = 0;
	               while ((read_len = s3is.read(read_buf)) > 0) {
	                   fileOutputStream.write(read_buf, 0, read_len);
	               }
	           }
	           Path pathObject = Paths.get(fileName);
	           Resource resource = new UrlResource(pathObject.toUri());

	           if (resource.exists() || resource.isReadable()) {
	               return resource;
	           } else {
	               throw new Exception("Could not find the file!");
	           }
	       }
	   }
	   
	   private boolean bucketIsEmpty() {
	       ListObjectsV2Result result = s3client.listObjectsV2(this.bucketName);
	       if (result == null){
	           return false;
	       }
	       List<S3ObjectSummary> objects = result.getObjectSummaries();
	       return objects.isEmpty();
	   }
	 
}
