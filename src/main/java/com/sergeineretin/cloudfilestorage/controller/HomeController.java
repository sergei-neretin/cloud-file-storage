package com.sergeineretin.cloudfilestorage.controller;

import com.sergeineretin.cloudfilestorage.dto.UserObjectDto;
import com.sergeineretin.cloudfilestorage.security.UserDetailsImpl;
import com.sergeineretin.cloudfilestorage.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {
    private final MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }
    @GetMapping("/home")
    public String homePage() {
        minioService.renameFile("user-1-files/pic.jpeg", "user-1-files/pic.jpg");
        return "home";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    private long getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getId();
    }
}
