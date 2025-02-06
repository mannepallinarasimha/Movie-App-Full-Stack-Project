package com.moviemix.MovieApi.controllers;

import ch.qos.logback.core.util.StringUtil;
import com.moviemix.MovieApi.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(path="/file/")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;

    @PostMapping(path="upload")
    public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException{

        return ResponseEntity.ok("File Uploaded : "+fileService.uploadFile(path, file));
    }

    @GetMapping("/{fileName}")
    public void serveFileHandler(@PathVariable("fileName") String fileName, HttpServletResponse httpServletResponse) throws IOException {
        InputStream resourceFile = fileService.getResourceFile(path, fileName);
        httpServletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, httpServletResponse.getOutputStream());
    }
}
