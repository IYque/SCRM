package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;


/**
 * 文件处理控制器
 */
@RestController
@Slf4j
@RequestMapping("file")
public class FileController {


    @Autowired
    IYqueParamConfig iYqueParamConfig;


    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public ResponseResult<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {

            // 检查文件大小
            long fileSize = file.getSize();
            if (fileSize < FileUtils.MIN_FILE_SIZE) {
                return new ResponseResult<>(HttpStatus.ERROR, "文件大小必须大于5个字节", null);
            }

            // 创建上传目录
            File uploadDir = new File(iYqueParamConfig.getUploadDir());
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            // 获取原始文件名及其扩展名
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

            // 根据文件类型进行检查和处理
            String fileType = FileUtils.getFileContentType(fileExtension);
            switch (fileType) {
                case IYqueMsgAnnex.MsgType.MSG_TYPE_FILE:
                    FileUtils.checkFileSize(file, FileUtils.FILE_MAX_SIZE);
                    break;
                case IYqueMsgAnnex.MsgType.MSG_TYPE_VIDES:
                    FileUtils.checkVideoSizeAndFormat(file, fileExtension);
                    break;
                case IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE:
                    //非png或jpg，则转化为jpg
                    if (!FileUtils.checkImageSizeAndFormat(file,fileExtension)) {
                        BufferedImage image = ImageIO.read(file.getInputStream());
                        String jpgFileName = UUID.randomUUID() + ".jpg";
                        Path jpgTargetPath = Paths.get(iYqueParamConfig.getUploadDir(), jpgFileName);
                        ImageIO.write(image, "jpg", jpgTargetPath.toFile());
                        return new ResponseResult<>(jpgFileName);
                    }
                    break;
                default:
                    return new ResponseResult<>(HttpStatus.ERROR, "不支持的文件类型", null);
            }

            // 获取文件名
            String fileName = UUID.randomUUID()+fileExtension;
            // 构建目标文件路径
            Path targetPath = Paths.get(iYqueParamConfig.getUploadDir(), fileName);
            // 将文件写入目标路径
            Files.write(targetPath, file.getBytes());

            return new ResponseResult<>(fileName);
        } catch (IOException e) {
             log.error("文件上传失败:"+e.getMessage());
            return new ResponseResult<>(HttpStatus.ERROR,e.getMessage(),null);
        }
    }





    @GetMapping("/fileView/{filename}")
    public ResponseEntity<Resource> readFile(@PathVariable String filename) {

            try {
            // 构建文件路径
            Path filePath = Paths.get(iYqueParamConfig.getUploadDir(), filename);
            // 获取文件资源
            Resource resource = new UrlResource(filePath.toUri());

            // 检查文件是否存在
            if (resource.exists() || resource.isReadable()) {
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                // 返回文件资源
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
