package io.github.kheynov.bicyclepark.Util;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static io.github.kheynov.bicyclepark.Util.Constants.infoTag;

public class Encryption {
    public static String stringToHash(String input) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert messageDigest != null;
        byte[] hashInBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));//Кодируем сообщение
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));//собираем строку
        }
        return sb.toString();//возвращаем строку с хешем
    }

    public static String generatePassword() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder str = new StringBuilder();
        /*for (int i = 0; i < 16; i++) {
            double a = secureRandom.nextDouble();
            String shape = Encryption.stringToHash(String.valueOf(a)).substring(4, 8);
            str.append(shape);
        }*/
        for (int i = 0; i < 16; i++) {
            double a = secureRandom.nextDouble();
            a *= 10;
            str.append((int) a);
        }
        Log.i(infoTag, "Password: "+str.toString());
        return str.toString();
    }
}
