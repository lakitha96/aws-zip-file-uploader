package com.example.fileupload.demo.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.example.fileupload.demo.service.S3Service;
import com.example.fileupload.demo.util.Utility;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;


/**
 * @author lakitha
 */

@Service
public class S3ServiceImpl implements S3Service{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AmazonS3 s3client;

    @Value("${jsa.s3.bucket}")
    private String bucketName;

    private String destination = "c:/s3/unzip";
    private String password = "password";

    @Override
    public void downloadFile(String keyName) {
        System.out.println("Downloading an object");
        S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, keyName));
        System.out.println("Content-Type: "  + s3object.getObjectMetadata().getContentType());
        try {
            Utility.displayText(s3object.getObjectContent());
            logger.info("===================== Import File - Done! =====================");
        } catch (AmazonServiceException ase) {
            logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("HTTP Status Code: " + ase.getStatusCode());
            logger.info("AWS Error Code:   " + ase.getErrorCode());
            logger.info("Error Type:       " + ase.getErrorType());
            logger.info("Request ID:       " + ase.getRequestId());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        }
    }

    @Override
    public void uploadFile(String bucket_path) {
        try {
            File directory = new File(destination);
            List<File> files = new LinkedList<File>();

            File[] fList = directory.listFiles();
            TransferManager tm = new TransferManager(s3client);
            for (File file : fList) {
                if (file.getName().contains(".")){
                    s3client.putObject(new PutObjectRequest(bucketName, bucket_path, file));
                    logger.info(file.getName() + " UPLOAD DONE");
                }else{
                    MultipleFileUpload upload = tm.uploadDirectory(bucketName, bucket_path, file, true );
                    upload.waitForCompletion();
                    logger.info(file.getName() + " UPLOAD DONE");
                }
            }

            System.out.println("===================== Upload Complete ========================");
            tm.shutdownNow();

            String url = s3client.getUrl(bucketName, bucket_path+"/index.html").toString();
            System.out.println("URL: "+ url);

        } catch (AmazonServiceException ase) {
            logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("HTTP Status Code: " + ase.getStatusCode());
            logger.info("AWS Error Code:   " + ase.getErrorCode());
            logger.info("Error Type:       " + ase.getErrorType());
            logger.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unzip(String source, String bucket_path) {
        cleanUnzipFolders();
        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
            logger.info("Extraction completed....");
            uploadFile(bucket_path);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private void cleanUnzipFolders(){
        try {
            FileUtils.deleteDirectory(new File(destination));
            logger.info("previous files has been deleted.....!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
