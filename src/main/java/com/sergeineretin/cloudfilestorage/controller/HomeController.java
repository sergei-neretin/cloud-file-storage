package com.sergeineretin.cloudfilestorage.controller;

import com.sergeineretin.cloudfilestorage.CustomFile;
import com.sergeineretin.cloudfilestorage.exception.StorageException;
import com.sergeineretin.cloudfilestorage.security.UserDetailsImpl;
import com.sergeineretin.cloudfilestorage.service.MinioService;
import com.sergeineretin.cloudfilestorage.util.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
public class HomeController {
    private final MinioService minioService;


    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }
    @GetMapping("/home")
    public String homePage(@RequestParam(value = "path", required = false) String path, Model model) {
        String fullPath = getFullPath(path);
        if (minioService.isFolderExist(fullPath)) {
            List<CustomFile> items = minioService.getList(fullPath);
            model.addAttribute("items", items);
            Map<String,String> hierarchy = StorageUtils.getNavigationHierarchy(path);
            model.addAttribute("hierarchy", hierarchy);
            model.addAttribute("path", path);
            return "home";
        } else {
            model.addAttribute("message", "folder does not exist");
            return "error";
        }
    }

    @PostMapping("/home")
    public String submit(@RequestParam(value = "path", required = false) String path,
                         @RequestParam("file") MultipartFile file,  Model model) throws IOException {
        String fullPath = getFullPath(path);
        minioService.createFile(fullPath, file);
        return  "redirect:/home?path=" + path;
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @ExceptionHandler
    private String handleStorageException(StorageException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    private String getFullPath(String path) {
        long id = getUserDetails().getId();
        String userRootFolder = "user-"+ id +"-files/";
        if (path != null && path.startsWith(userRootFolder)) {
            return path;
        } else if (path != null) {
            return userRootFolder + path;
        } else {
            return userRootFolder;
        }
    }

    private UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
