package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.model.CustomFile;
import com.sergeineretin.cloudfilestorage.exception.BrokenFileException;
import io.minio.*;
import io.minio.messages.Item;
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
import java.util.ArrayList;
import java.util.List;

import static com.sergeineretin.cloudfilestorage.util.StorageUtils.USER_FILES_BUCKET_NAME;

@Service
public class HomeService {

    private static final Logger log = LoggerFactory.getLogger(HomeService.class);
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
            log.error(e.getMessage());
        }
    }

    @Autowired
    public HomeService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createFile(String path, @NotNull MultipartFile multipartFile) {
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

    private InputStream getInputStream(MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new BrokenFileException("The file is corrupted and cannot be downloaded");
        }
    }

    public void createFolder(String path, String folderName) {
        try {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (!folderName.endsWith("/")) {
                folderName = folderName + "/";
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path + folderName)
                            .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e); // TO DO
        }
    }

    public void delete(String path) {
        try {
            if (path.endsWith("/")) {
                Iterable<Result<Item>> iterable = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(USER_FILES_BUCKET_NAME)
                                .prefix(path)
                                .recursive(true)
                                .build());
                for (Result<Item> file : iterable) {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(USER_FILES_BUCKET_NAME)
                                    .object(file.get().objectName())
                                    .build());
                    System.out.println(file.get().objectName());
                }
            }
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFolderExist(String objectName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .prefix(objectName)
                            .maxKeys(1)
                            .build()
            );
            return results.iterator().hasNext();
        } catch (Exception e) {
            log.error("An unexpected error occurred while checking if the folder '{}' exists", objectName, e);
            return false;
        }
    }
    public void rename(String fullName, String newFullName) {
        try {
           if (fullName.endsWith("/")) {
               Iterable<Result<Item>> iterable = minioClient.listObjects(
                       ListObjectsArgs.builder()
                               .bucket(USER_FILES_BUCKET_NAME)
                               .prefix(fullName)
                               .recursive(true)
                               .build());
               for (Result<Item> result : iterable) {
                   String newObjectName = result.get().objectName().replaceFirst(fullName, newFullName);
                   minioClient.copyObject(
                           CopyObjectArgs.builder()
                                   .bucket(USER_FILES_BUCKET_NAME)
                                   .object(newObjectName)
                                   .source(
                                           CopySource.builder()
                                                   .bucket(USER_FILES_BUCKET_NAME)
                                                   .object(result.get().objectName())
                                                   .build())
                                   .build());
               }
           }
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(newFullName)
                            .source(
                                    CopySource.builder()
                                            .bucket(USER_FILES_BUCKET_NAME)
                                            .object(fullName)
                                            .build())
                            .build());
            delete(fullName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<CustomFile> getList(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        try {
            Iterable<Result<Item>> iterable = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .prefix(path)
                            .build());
            List<CustomFile> results = new ArrayList<>();
            String finalPath = path;
            iterable.forEach(x -> {
                try {
                    Item item = x.get();
                    if (!item.objectName().equals(finalPath)) {
                        results.add(new CustomFile(item));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("List (size = {}) of files found for path: {}",results.size(), path);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

