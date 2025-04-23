package com.example.Bex.controller;

import com.example.Bex.model.FileDocument;
import com.example.Bex.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class FileController {

    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<FileDocument> files = storageService.list();
        model.addAttribute("files", files);
        return "index";
    }

    @GetMapping("/files")
    public String listFiles(Model model) {
        List<FileDocument> files = storageService.list();
        model.addAttribute("files", files);
        return "index";
    }

    @PostMapping("/files")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        storageService.upload(file);
        return "redirect:/files";
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id) {
        FileDocument doc = storageService.list().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found"));
        Resource resource = storageService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/files/{id}/delete")
    public String deleteFile(@PathVariable String id) {
        storageService.delete(id);
        return "redirect:/files";
    }
}
