package com.SmoothSailing.services;

import java.io.*;
import java.nio.file.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    public void saveFile(String uploadDir, String fileName, MultipartFile file)throws IOException{
        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = file.getInputStream()){
            Path filePath = uploadPath.resolve(fileName);
            System.out.println("Image path: " + filePath);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe){
            throw new IOException("Could not save image file: " + fileName, ioe);
        }

    }
}
