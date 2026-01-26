package com.backend.lavugio.service.utils.impl;

import com.backend.lavugio.config.StorageConfig;
import com.backend.lavugio.service.utils.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

    private final StorageConfig storageProperties;

    @Autowired
    public FileServiceImpl(StorageConfig storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public Resource getFile(String filename) {
        try {
            Path rootPath = Paths.get(storageProperties.getRoot()).toAbsolutePath().normalize();
            Path filePath = rootPath.resolve(filename).normalize();

            if (!filePath.startsWith(rootPath)) {
                throw new RuntimeException("Cannot access file outside root directory: " + filename);
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File doesn't exist or isn't readable: " + filename);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Error while reading file: " + filename, e);
        }
    }
}
