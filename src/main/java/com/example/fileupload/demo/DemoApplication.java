package com.example.fileupload.demo;

import com.example.fileupload.demo.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

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

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("---------------- START UPLOAD FILE ----------------");
		s3Services.unzip("c:/s3/3.zip", bucket_path+game+version);
//		System.out.println("---------------- START DOWNLOAD FILE ----------------");
//		s3Services.downloadFile(key);
	}
}
