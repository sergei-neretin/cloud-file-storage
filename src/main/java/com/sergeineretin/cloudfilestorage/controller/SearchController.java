package com.sergeineretin.cloudfilestorage.controller;

import com.sergeineretin.cloudfilestorage.model.CustomFile;
import com.sergeineretin.cloudfilestorage.service.SearchService;
import com.sergeineretin.cloudfilestorage.util.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "query") String query, Model model) {
        List<CustomFile> files = searchService.searchFile(StorageUtils.getUserRootFolder(), query);
        model.addAttribute("query", query);
        model.addAttribute("files", files);
        return "search";
    }
}
