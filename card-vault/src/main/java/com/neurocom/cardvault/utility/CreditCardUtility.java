package com.neurocom.cardvault.utility;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.DigestUtils;

public class CreditCardUtility {

    /* Luhn Algorithm implimentation */
    public static boolean isValidCreditCard(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Iterate from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));

            // Double every second digit
            if (alternate) {
                n *= 2;
                // If the result is greater than 9, subtract 9
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate; // Flip the alternate flag
        }

        // The number is valid if the total sum is a multiple of 10
        return (sum % 10 == 0);
    }
    
    public static String encrypt(String data, String key, String algorithm) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String key, String algorithm) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static String panLast4DigitHash(String pan) {
        final String panLast4 = pan.substring(pan.length() - 4);
        return DigestUtils.md5DigestAsHex(panLast4.getBytes(StandardCharsets.UTF_8));
    }

    public static String maskPan(String pan) {
        if (pan == null || pan.length() < 4) {
            return pan; 
        }

        int unmasked = 4;
        int maskedLength = pan.length() - unmasked;

        String maskedPart = "*".repeat(maskedLength);
        String visiblePart = pan.substring(pan.length() - unmasked);

        // Group digits in sets of 4
        String grouped = (maskedPart + visiblePart).replaceAll("(.{4})(?!$)", "$1 ");
        return grouped.trim();
    }

}
