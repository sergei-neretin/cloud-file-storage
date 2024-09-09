package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.dto.UserObjectDto;
import com.sergeineretin.cloudfilestorage.exception.BrokenFileException;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

@Service
public class MinioService {
    private final String USER_FILES_BUCKET_NAME="user-files";

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    MinioClient minioClient;

    @PostConstruct
    public void initRootBucket() {
        try {
            boolean exist = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .build());
            if (!exist) {
                log.info("Creating user files bucket with name: " + USER_FILES_BUCKET_NAME);
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(USER_FILES_BUCKET_NAME)
                                .build());
            }
        } catch (Exception e) {

        }
    }

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createFile(String path, @NotNull MultipartFile multipartFile) throws IOException {
        InputStream inputStream = getInputStream(multipartFile);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path + multipartFile.getOriginalFilename())
                            .stream(inputStream, -1, 10485760)
                            .contentType(multipartFile.getContentType())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e); // TO DO
        }
    }

    private InputStream getInputStream(MultipartFile multipartFile) throws IOException {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new BrokenFileException("The file is corrupted and cannot be downloaded");
        }
    }

    public void createFolder(String path) throws IOException {
        try {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path)
                            .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e); // TO DO
        }
    }

    public void deleteFile(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void renameFile(String source, String dest) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(dest)
                            .source(
                                    CopySource.builder()
                                            .bucket(USER_FILES_BUCKET_NAME)
                                            .object(source)
                                            .build())
                            .build());
            deleteFile(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

