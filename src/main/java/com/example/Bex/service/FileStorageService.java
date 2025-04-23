package com.example.Bex.service;

import com.example.Bex.model.FileDocument;
import com.example.Bex.repository.FileRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;
    private final String bucketName;

    public FileStorageService(AmazonS3 s3Client,
            FileRepository fileRepository,
            @Value("${b2.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.fileRepository = fileRepository;
        this.bucketName = bucketName;
    }

    public FileDocument upload(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentType(file.getContentType());
        s3Client.putObject(bucketName, key, file.getInputStream(), meta);

        FileDocument doc = new FileDocument();
        doc.setFileName(file.getOriginalFilename());
        doc.setB2Key(key);
        doc.setUploadDate(new Date());
        doc.setSize(file.getSize());
        doc.setContentType(file.getContentType());
        return fileRepository.save(doc);
    }

    public Resource download(String id) {
        FileDocument doc = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        var s3obj = s3Client.getObject(bucketName, doc.getB2Key());
        return new InputStreamResource(s3obj.getObjectContent());
    }

    public List<FileDocument> list() {
        return fileRepository.findAll();
    }

    public void delete(String id) {
        FileDocument doc = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        s3Client.deleteObject(bucketName, doc.getB2Key());
        fileRepository.delete(doc);
    }
}
