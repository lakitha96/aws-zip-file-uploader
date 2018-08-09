package com.example.fileupload.demo.service;

/**
 * @author lakitha
 */
public interface S3Service {
    public void downloadFile(String keyName);
    public void uploadFile(String bucket_path);
    public void unzip(String source, String bucket_path);
}
