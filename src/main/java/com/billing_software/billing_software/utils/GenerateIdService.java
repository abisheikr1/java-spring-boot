package com.billing_software.billing_software.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.Random;

public class GenerateIdService {

    public static String generateId(String prefix) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmssSSS");
        String date = formatter.format(new Date());
        String id = prefix + date + UUID.randomUUID().toString().substring(0, 4)
                + UUID.randomUUID().toString().substring(0, 4);
        return id;
    }

    public static String generateEAN13Barcode() {
        Random random = new Random();
        StringBuilder ean12 = new StringBuilder();

        // Generate a 12-digit base code
        for (int i = 0; i < 12; i++) {
            ean12.append(random.nextInt(10)); // Append a random digit (0-9)
        }

        // Calculate the check digit
        int checkDigit = calculateCheckDigit(ean12.toString());

        // Append the check digit to the 12-digit base code
        return ean12.append(checkDigit).toString(); // R
    }

    private static int calculateCheckDigit(String ean12) {
        int sumOdd = 0;
        int sumEven = 0;

        // Calculate the sums for odd and even positions
        for (int i = 0; i < ean12.length(); i++) {
            int digit = Character.getNumericValue(ean12.charAt(i));
            if (i % 2 == 0) { // Odd positions (0, 2, 4, ...)
                sumOdd += digit;
            } else { // Even positions (1, 3, 5, ...)
                sumEven += digit;
            }
        }

        int totalSum = sumOdd + (sumEven * 3);
        int checkDigit = (10 - (totalSum % 10)) % 10;

        return checkDigit; // Return the calculated check digit
    }
}
