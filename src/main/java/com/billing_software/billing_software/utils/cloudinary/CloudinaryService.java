package com.billing_software.billing_software.utils.cloudinary;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class CloudinaryService {

    @Value("${cloudinary.url}")
    private String cloudinaryURL;

    private Cloudinary cloudinaryClient;

    @PostConstruct
    public void cloudinaryInitilization() {
        cloudinaryClient = new Cloudinary(cloudinaryURL);
    }

    public String uploadImage(MultipartFile file, String folderPath, String fileName) {
        try {
            Map<String, String> uploadResult = cloudinaryClient.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", folderPath,
                            "public_id", fileName));
            String imageUrl = uploadResult.get("secure_url");
            return imageUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid upload details");
        }
    }

    public String uploadImage(byte[] file, String folderPath, String fileName) {
        try {
            Map<String, String> uploadResult = cloudinaryClient.uploader().upload(file,
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", folderPath,
                            "public_id", fileName));
            String imageUrl = uploadResult.get("secure_url");
            return imageUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid upload details");
        }
    }

    public void deleteImage(String publicUrl) {
        try {
            // Call the destroy method with the public_id, not the full URL
            Map<String, String> result = cloudinaryClient.uploader().destroy(extractPublicIdFromUrl(publicUrl),
                    ObjectUtils.emptyMap());

            // Check the result to confirm the image is deleted
            if ("ok".equals(result.get("result"))) {
                System.out.println("Image deleted successfully.");
            } else {
                System.out.println("Failed to delete image. Result: " + result.get("result"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete image from Cloudinary");
        }
    }

    // Helper function to extract public_id from the public URL
    private String extractPublicIdFromUrl(String publicUrl) {
        // Remove the "https://res.cloudinary.com/..." part and extract after "upload/"
        String withoutBase = publicUrl.substring(publicUrl.indexOf("upload/") + 7);

        // Remove the version number (e.g., "v1726375461/") and file extension (e.g.,
        // ".png")
        withoutBase = withoutBase.substring(withoutBase.indexOf("/") + 1, withoutBase.lastIndexOf("."));

        return withoutBase; // Return the correct public_id
    }

}
