package com.nuvolo.nuvoloapi.minio;

import com.nuvolo.nuvoloapi.exceptions.ProductException;
import com.nuvolo.nuvoloapi.model.entity.ProductImage;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    private final MinioComponent minioComponent;

    public void uploadProductImages(List<ProductImage> productImages, MultipartFile[] images) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioComponent.getProductsBucket()).build());
            if (!found) {
                log.debug("Could not find bucket. Creating bucket");
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioComponent.getProductsBucket()).build());
            }
            log.debug("Saving product images to bucket {}", minioComponent.getProductsBucket());

            for (int counter = 0; counter < images.length; counter++) {
                String imagePath = productImages.get(counter).getImageNo().concat(".").concat(productImages.get(counter).getExtension());
                minioClient.putObject(
                        PutObjectArgs.builder().bucket(minioComponent.getProductsBucket()).object(imagePath).stream(
                                        images[counter].getInputStream(), images[counter].getInputStream().available(), -1)
                                .contentType(images[counter].getContentType())
                                .build());
                log.debug("Successfully saved file {} to bucket.", imagePath);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ProductException(ex.getMessage());
        }
    }

    public String getImageUrl(String imageUrl) {
        try {
            log.debug("Returning image url.");
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioComponent.getProductsBucket())
                            .object(imageUrl)
                            .expiry(7, TimeUnit.DAYS) // URL valid for 7 days
                            .build());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ProductException(ex.getMessage());
        }
    }

    public void deleteProductImages(List<ProductImage> productImages) {
        log.debug("Deleting all product images.");
        try {
            for (ProductImage productImage : productImages) {
                String imagePath = productImage.getImageNo().concat(".").concat(productImage.getExtension());
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioComponent.getProductsBucket())
                        .object(imagePath).build());
            }
            log.debug("Successfully deleted all product images.");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ProductException(ex.getMessage());
        }
    }
}
