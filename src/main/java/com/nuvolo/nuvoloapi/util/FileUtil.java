package com.nuvolo.nuvoloapi.util;

import com.nuvolo.nuvoloapi.exceptions.ProductException;
import com.nuvolo.nuvoloapi.model.entity.Product;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileUtil {

    private static final List<String> ACCEPTED_CONTENT_TYPES = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/jpg"
    );

    private static final String PRODUCT_IMAGES_DIR = "products/";

    private FileUtil() throws IllegalAccessException {
        throw new IllegalAccessException("File Utility class constructor.");
    }

    public static boolean checkIfFileContentTypeIsValid(MultipartFile file) {
        return ACCEPTED_CONTENT_TYPES.contains(file.getContentType());
    }

    public static void saveProductImages(Product product, MultipartFile[] images) throws IOException {
        Path uploadPath = Paths.get(PRODUCT_IMAGES_DIR + product.getId());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        for (int i = 0; i < images.length; i++) {
            String fileExtension = FilenameUtils.getExtension(images[i].getOriginalFilename());
            if (fileExtension == null || fileExtension.isBlank()) {
                throw new NoSuchFileException("Can't read file extension.");
            }
            Path filePath = uploadPath.resolve(i + "." + fileExtension);
            Files.copy(images[i].getInputStream(), filePath);
        }
    }

    public static void deleteProductImages(Long id) {
        Path path = Path.of(PRODUCT_IMAGES_DIR + id);
        if (!Files.exists(path)) {
            throw new ProductException("Path for product images does not exist");
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    throw new ProductException(exc.getMessage());
                }
            });
        } catch (Exception ex) {
            throw new ProductException(ex.getMessage());
        }
    }
}