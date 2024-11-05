package com.ecommerce.g58.service;

import com.ecommerce.g58.exception.SpringBootFileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileS3Service {
    String uploadFile(MultipartFile multipartFile) throws SpringBootFileUploadException, IOException;

    Object downloadFile(String fileName) throws SpringBootFileUploadException, IOException;

    boolean delete(String fileName);
}
