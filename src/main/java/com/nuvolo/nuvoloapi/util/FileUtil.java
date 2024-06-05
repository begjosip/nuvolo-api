package com.nuvolo.nuvoloapi.util;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUtil {

    private static final List<String> ACCEPTED_CONTENT_TYPES = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/jpg"
    );

    private FileUtil() throws IllegalAccessException {
        throw new IllegalAccessException("File Utility class constructor.");
    }

    public static boolean checkIfFileContentTypeIsValid(MultipartFile file) {
        return ACCEPTED_CONTENT_TYPES.contains(file.getContentType());
    }

}