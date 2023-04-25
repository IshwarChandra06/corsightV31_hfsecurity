package com.eikona.mata.util;
import java.io.*;
import java.nio.file.*;
 
import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.constants.MessageConstants;
 
public class FileUploadUtil {
     
    public static void saveFile(String uploadDir, String fileName,
            MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
         
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
         
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new IOException(MessageConstants.NOT_SAVE_IMAGE_FILE + fileName, ioe);
        }      
    }
}
