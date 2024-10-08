package com.sergeineretin.cloudfilestorage.util;

import com.sergeineretin.cloudfilestorage.security.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

@UtilityClass
public class StorageUtils {

    public final static String USER_FILES_BUCKET_NAME="user-files";

    public static String getFullDirectoryPath(String path) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            long id = userDetails.getId();
            String userRootFolder = "user-" + id + "-files/";

            if (path == null || path.isBlank()) {
                return userRootFolder;
            } else if (!path.endsWith("/")) {
                return userRootFolder + path + "/";
            } else {
                return userRootFolder + path;
            }
        } else {
            throw new IllegalStateException("User is not authenticated or invalid principal type.");
        }
    }

    public static String getUserRootFolder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        long id = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return  "user-" + id + "-files/";
    }

    public static Map<String, String> getNavigationHierarchy(String path) {
        if (path != null) {
            List<String> folders = Arrays.asList(path.split("/"));
            List<String> subFolders = new ArrayList<>();
            subFolders.add(folders.get(0));
            for (int i = 1; i < folders.size(); i++) {
                subFolders.add(subFolders.get(i - 1) + "/" + folders.get(i));
            }

            Map<String, String> hierarchy = new LinkedHashMap<>();
            for (int i = 0; i < subFolders.size(); i++) {
                hierarchy.put(folders.get(i), subFolders.get(i));
            }

            return hierarchy;
        } else {
            return new LinkedHashMap<>();
        }
    }
}
