package ru.spbau.mit.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class Hasher {
    public static String getHash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }
}
