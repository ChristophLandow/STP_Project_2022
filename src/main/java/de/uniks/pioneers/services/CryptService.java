package de.uniks.pioneers.services;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import static de.uniks.pioneers.Constants.encryptKeySeed;

public class CryptService {
    @Inject
    public CryptService(){}

    public String encrypt(String str) {
        try {

            SecretKey secretKey = new SecretKeySpec(encryptKeySeed, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryption = cipher.doFinal(str.getBytes());

            return Base64.getEncoder().encodeToString(encryption);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Encryption error: " + e.getMessage());
        }

        return null;
    }

    public String decrypt(String str) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(str);

            SecretKey secretKey = new SecretKeySpec(encryptKeySeed, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(cipher.doFinal(encrypted));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Encryption error: " + e.getMessage());
        }

        return null;
    }
}


