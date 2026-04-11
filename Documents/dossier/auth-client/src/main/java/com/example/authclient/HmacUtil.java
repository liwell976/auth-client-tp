package com.example.authclient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Utilitaire pour le calcul HMAC-SHA256.
 */
public class HmacUtil {

    private HmacUtil() {}

    /**
     * Calcule un HMAC-SHA256.
     * @param key clé secrète (mot de passe)
     * @param data données à signer (email:nonce:timestamp)
     * @return signature hexadécimale
     */
    public static String compute(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hmacBytes = mac.doFinal(
                data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hmacBytes);
    }
}