package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.model.CustomFile;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sergeineretin.cloudfilestorage.util.StorageUtils.USER_FILES_BUCKET_NAME;

@Service
public class SearchService {
    private static final Logger log = LoggerFactory.getLogger(HomeService.class);
    private final MinioClient minioClient;

    public SearchService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<CustomFile> searchFile(String directory, String query) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .prefix(directory)
                            .recursive(true)
                            .build()
            );

            List<CustomFile> list = new ArrayList<>();
            String lowerCaseQuery = query.toLowerCase();

            for (Result<Item> result : results) {
                CustomFile customFile = new CustomFile(result.get());
                if (customFile.getName().toLowerCase().contains(lowerCaseQuery)) {
                    list.add(customFile);
                }
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
