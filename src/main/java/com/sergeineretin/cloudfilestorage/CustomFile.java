package com.sergeineretin.cloudfilestorage;

import io.minio.messages.Item;
import lombok.*;

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
        path = item.objectName();
        size = item.size();
    }
}
