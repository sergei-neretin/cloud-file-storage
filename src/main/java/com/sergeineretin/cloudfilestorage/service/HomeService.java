package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.exception.*;
import com.sergeineretin.cloudfilestorage.model.CustomFile;
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
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(USER_FILES_BUCKET_NAME)
                                .build());
                log.info("Creating user files bucket with name: " + USER_FILES_BUCKET_NAME);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new StorageException("Error creating user files bucket", e);
        }
    }

    @Autowired
    public HomeService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createFile(String path, @NotNull MultipartFile multipartFile) {
        try (InputStream inputStream = getInputStream(multipartFile)) {
            checkIfPathExists(path);
            if (isObjectExist(path + multipartFile.getOriginalFilename())) {
                throw new FileAlreadyExistsException("Object '" + path + multipartFile.getOriginalFilename() + "' already exists");
            }
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path + multipartFile.getOriginalFilename())
                            .stream(inputStream, -1, 10485760)
                            .contentType(multipartFile.getContentType())
                    .build());
            log.info("File '{}' uploaded successfully", multipartFile.getOriginalFilename());
        } catch (Exception e) {
            throw new StorageException("Failed to store file '" + multipartFile.getOriginalFilename() + "'", e);
        }
    }

    private InputStream getInputStream(MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            log.error("Failed to open file '{}'", multipartFile.getOriginalFilename(), e);
            throw new BrokenFileException("The file is corrupted and cannot be downloaded");
        }
    }

    public void createFolder(String path, String folderName) {
        try {
            path = ensureTrailingSlash(path);
            checkIfPathExists(path);
            folderName = ensureTrailingSlash(folderName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object(path + folderName)
                            .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
        } catch (Exception e) {
            log.error("Error creating folder '{}'", folderName, e);
            throw new StorageException("Failed to store folder '" + folderName + "'", e);
        }
    }

    public void delete(String path) {
        try {
            checkIfPathExists(path);
            if (path.endsWith("/")) {
                Iterable<Result<Item>> iterable = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(USER_FILES_BUCKET_NAME)
                                .prefix(path)
                                .recursive(true)
                                .build());
                for (Result<Item> file : iterable) {
                    removeObject(file.get().objectName());
                }
            }
            removeObject(path);
        } catch (Exception e) {
            log.error("Failed to delete folder '{}'", path, e);
            throw new StorageException("Failed to delete file '" + path + "'", e);
        }
    }

    private void removeObject(String path) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(USER_FILES_BUCKET_NAME)
                        .object(path)
                        .build());
        log.info("Deleted: {}", path);
    }

    public void rename(String fullName, String newFullName) {
        try {
            checkIfPathExists(fullName);
            if (isObjectExist(newFullName)) {
                throw new FileAlreadyExistsException("Rename failed: Object '" + newFullName + "' already exists");
            }
            if (fullName.endsWith("/")) {
               Iterable<Result<Item>> iterable = minioClient.listObjects(
                       ListObjectsArgs.builder()
                               .bucket(USER_FILES_BUCKET_NAME)
                               .prefix(fullName)
                               .recursive(true)
                               .build());
               for (Result<Item> result : iterable) {
                   String newObjectName = result.get().objectName().replaceFirst(fullName, newFullName);
                   copyObject(result.get().objectName(), newObjectName);
               }
            } else {
                copyObject(fullName, newFullName);
            }
            delete(fullName);
        } catch (PathNotFoundException | FileAlreadyExistsException e) {
            log.info("Error renaming: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error renaming object '{}' to '{}'", fullName, newFullName, e);
            throw new StorageException("Failed to rename '" + fullName + "' to '" + newFullName + "'", e);
        }
    }

    private void copyObject(String objectName, String newObjectName) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(USER_FILES_BUCKET_NAME)
                        .object(newObjectName)
                        .source(
                                CopySource.builder()
                                        .bucket(USER_FILES_BUCKET_NAME)
                                        .object(objectName)
                                        .build())
                        .build());
        log.info("Renamed: {} -> {}",  objectName, newObjectName);
    }

    public List<CustomFile> getList(String path) {
        try {
            path = ensureTrailingSlash(path);
            checkIfPathExists(path);
            Iterable<Result<Item>> iterable = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .prefix(path)
                            .build());
            List<CustomFile> results = new ArrayList<>();
            for (Result<Item> result : iterable) {
                Item item = result.get();
                if (!item.objectName().equals(path)) {
                    results.add(new CustomFile(item));
                }
            }
            log.info("List (size = {}) of files found for path: {}",results.size(), path);
            return results;
        } catch (Exception e) {
            log.error("Error getting list", e);
            throw new StorageException("Failed to list files '" + path + "'", e);
        }
    }

    private String ensureTrailingSlash(String path) {
        return path.endsWith("/") ? path : path + "/";
    }

    private void checkIfPathExists(String path) {
        if (!isObjectExist(path)) {
            throw new PathNotFoundException("The specified path '" + path + "' does not exist.");
        }
    }

    public boolean isObjectExist(String objectName) {
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
            log.error("An error occurred while checking if the object '{}' exists", objectName, e);
            throw new StorageException("Failed to check if the object '" + objectName + "' exists", e);
        }
    }
}
