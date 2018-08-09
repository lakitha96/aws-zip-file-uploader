package com.example.fileupload.demo.controller;

import com.example.fileupload.demo.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author lakitha
 */
@Controller
public class FileUploadController {
    @Autowired
    S3Service s3Services;

    @Value("${jsa.s3.uploadfile}")
    private String uploadFilePath;

    @Value("${jsa.s3.key}")
    private String key;

    @Value("${bucket.path}")
    private String bucket_path;

    @Value("${game.name}")
    private String game;

    @Value("${game.version}")
    private String version;

    @PostMapping(value = "/upload")
    public void uploadZipFile(){
        System.out.println("---------------- START UPLOAD FILE ----------------");
        s3Services.unzip("c:/s3/3.zip", bucket_path+game+version);
//		System.out.println("---------------- START DOWNLOAD FILE ----------------");
//		s3Services.downloadFile(key);
    }
}
