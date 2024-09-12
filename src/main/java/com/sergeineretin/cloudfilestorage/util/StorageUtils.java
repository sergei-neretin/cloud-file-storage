package com.sergeineretin.cloudfilestorage.util;

import java.util.*;

public class StorageUtils {
    private StorageUtils() {}

    public static String getRootFolderByUsername(String username) {
        return "user-" + username + "files";
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
            for (int i = 1; i < subFolders.size(); i++) {
                hierarchy.put(folders.get(i), subFolders.get(i));
            }

            return hierarchy;
        } else {
            return new LinkedHashMap<>();
        }
    }
}
