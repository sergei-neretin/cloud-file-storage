package com.sergeineretin.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileDownloadRequest {

    @NotBlank(message = "File name must not be blank")
    private String name;

    @NotBlank(message = "File path must not be blank")
    private String path;

    @NotBlank(message = "Owner must not be blank")
    private String owner;
}
