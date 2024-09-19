package com.sergeineretin.cloudfilestorage.controller;

import com.sergeineretin.cloudfilestorage.exception.StorageException;
import com.sergeineretin.cloudfilestorage.service.HomeService;
import com.sergeineretin.cloudfilestorage.util.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class HomeController {
    private final HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }
    @GetMapping("/home")
    public String homePage(@RequestParam(value = "path", required = false) String path, Model model) {
        String fullPath = StorageUtils.getFullDirectoryPath(path);
        if (homeService.isFolderExist(fullPath)) {
            model.addAttribute("items", homeService.getList(fullPath));
            model.addAttribute("hierarchy",  StorageUtils.getNavigationHierarchy(path));
            model.addAttribute("path", path);
            return "home";
        } else {
            model.addAttribute("message", "folder does not exist");
            return "error";
        }
    }

    @PostMapping("/home/new-file")
    public String submit(@RequestParam(value = "path", required = false) String path,
                         @RequestParam("file") MultipartFile file) {
        homeService.createFile(StorageUtils.getFullDirectoryPath(path), file);
        return  redirectToHomePage(path);
    }

    @PostMapping("/home/new-folder")
    public String newFolder(@RequestParam(value = "path", required = false) String path,
                            @RequestParam(value = "name") String name) {
        homeService.createFolder(StorageUtils.getFullDirectoryPath(path), name);
        return redirectToHomePage(path);
    }

    @PostMapping("/home/delete") String deletePath(@RequestParam(value = "path") String path,
                                                     @RequestParam(value = "objectName") String name) {
        homeService.delete( StorageUtils.getFullDirectoryPath(path) + name);
        return redirectToHomePage(path);
    }

    @PostMapping("/home/rename") String rename(@RequestParam(value = "path", required = false) String path,
                                               @RequestParam(value = "name") String name,
                                               @RequestParam(value = "newName") String newName) {
        homeService.rename( StorageUtils.getFullDirectoryPath(path) + name,
                StorageUtils.getFullDirectoryPath(path) + newName);
        return redirectToHomePage(path);
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

    private String redirectToHomePage(String path) {
        return "redirect:/home?path=" + (path != null ? path : "");
    }

}
