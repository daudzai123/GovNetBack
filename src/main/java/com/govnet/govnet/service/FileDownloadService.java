package com.govnet.govnet.service;

import com.govnet.govnet.entity.Post;
import com.govnet.govnet.exception.FileStorageException;
import com.govnet.govnet.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FileDownloadService {

    private final Path fileStorageLocation;
    private final PostRepository PostRepository;

    @Autowired
    public FileDownloadService(PostRepository PostRepository) {
        this.fileStorageLocation = Paths.get("E:\\Full Stack GovNet\\attachments").toAbsolutePath().normalize();
        this.PostRepository = PostRepository;
    }

    public Resource loadFileById(Long id) {
        try {
            Optional<Post> fileRecordOpt = PostRepository.findById(id);
            if (fileRecordOpt.isEmpty()) {
                throw new FileStorageException("File with ID " + id + " not found in database.");
            }
    
            Post fileRecord = fileRecordOpt.get();
            String attachmentPath = fileRecord.getMedia();
    
            // Ensure the attachment path does not include the base directory if it already has it
            String fileName = attachmentPath.replace("E:\\Full Stack GovNet\\attachments", "");
    
            if (fileName.isEmpty()) {
                throw new FileStorageException("No attachment filename found for record ID: " + id);
            }
    
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
    
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File does not exist or is not readable: " + fileName);
            }
    
            return resource;
        } catch (Exception ex) {
            throw new FileStorageException("Error loading file: " + ex.getMessage(), ex);
        }
    }
}
