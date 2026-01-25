package com.backend.lavugio.service.utils;

import org.springframework.core.io.Resource;

public interface FileService {
    Resource getFile(String filePath);
}
