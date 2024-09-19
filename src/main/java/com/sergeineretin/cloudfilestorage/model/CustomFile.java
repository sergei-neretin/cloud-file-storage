package com.sergeineretin.cloudfilestorage.model;

import io.minio.messages.Item;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomFile {
    private String name;
    private String path;
    private long size;
    private boolean isDir;
    public CustomFile(Item item) {
        String fullPath = item.objectName();
        List<String> list = Arrays.stream(fullPath.split("/")).toList();
        if (fullPath.endsWith("/")) {
            name = list.get(list.size() - 1) + "/";
            isDir = true;
        } else {
            name = list.get(list.size() - 1);
            isDir = false;
        }
        String pathWithUserFolder = item.objectName();
        List<String> list1 = new ArrayList<>(Arrays.stream(pathWithUserFolder.split("/")).toList());
        list1.remove(0);

        path = isDir ? String.join("/", list1) : String.join("/", list1) + "/";

        size = item.size();
    }
}
