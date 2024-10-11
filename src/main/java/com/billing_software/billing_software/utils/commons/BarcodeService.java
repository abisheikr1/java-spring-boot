package com.billing_software.billing_software.utils.commons;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer; // Change to use EAN13Writer
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D; 
@Service
public class BarcodeService {

    public byte[] generateBarcode(String barcodeString) {
        // Ensure barcodeString is a valid EAN-13 number (13 digits)
        if (barcodeString == null || !barcodeString.matches("\\d{13}")) {
            throw new IllegalArgumentException("Invalid EAN-13 code. Must be a 13-digit number.");
        }

        int width = 300; // Width of the barcode
        int height = 150; // Height of the barcode

        try {
            // Generate the EAN-13 barcode
            BitMatrix bitMatrix = new EAN13Writer().encode(barcodeString, BarcodeFormat.EAN_13, width, height);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();

            // Convert BitMatrix to BufferedImage
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return imageBytes;
            // Uncomment the following lines if you want to upload to Cloudinary
            // String imageUrl = cloudinaryService.uploadImage(new
            // MultipartFileWrapper(imageBytes, "barcode.png"),
            // folderPath, fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
