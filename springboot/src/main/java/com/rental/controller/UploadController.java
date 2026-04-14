package com.rental.controller;

import com.rental.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传接口")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "type", defaultValue = "common") String type) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String url = saveFile(file, type);
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/images")
    @Operation(summary = "上传多张图片")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files,
                                              @RequestParam(value = "type", defaultValue = "house") String type) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                String url = saveFile(file, type);
                urls.add(url);
            } catch (IOException e) {
                return Result.error("上传失败: " + e.getMessage());
            }
        }
        return Result.success(urls);
    }

    @PostMapping("/cover")
    @Operation(summary = "上传房源封面")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            String url = saveFile(file, "house/cover");
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/house-images")
    @Operation(summary = "上传房源图片")
    public Result<List<String>> uploadHouseImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            try {
                String url = saveFile(file, "house/images");
                urls.add(url);
            } catch (IOException e) {
                return Result.error("上传失败: " + e.getMessage());
            }
        }
        return Result.success(urls);
    }

    @PostMapping("/idcard")
    @Operation(summary = "上传身份证图片")
    public Result<String> uploadIdCard(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            String url = saveFile(file, "idcard");
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传头像")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            String url = saveFile(file, "avatar");
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/news")
    @Operation(summary = "上传资讯图片")
    public Result<String> uploadNewsImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            String url = saveFile(file, "news");
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String type) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFilename = UUID.randomUUID().toString() + suffix;

        String relativePath = type + "/" + datePath + "/";
        File dir = new File(uploadPath + relativePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(uploadPath + relativePath + newFilename);
        file.transferTo(dest);

        return "/uploads/" + relativePath + newFilename;
    }
}
