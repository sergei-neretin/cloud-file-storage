package com.sergeineretin.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserObjectDto {
    private long id;
    private String folder;
    private String pathToFile;
    private InputStream inputStream;
    private long size;
    private String contentType;
}
