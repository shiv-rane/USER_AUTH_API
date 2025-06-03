package com.example.auth.security;

import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RsaKeyUtil {

    public static PrivateKey getPrivateKey() throws Exception {
        String key = Files.readString(Paths.get("src/main/resources/keys/private.pem"))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static PublicKey getPublicKey() throws Exception {
        String key = Files.readString(Paths.get("src/main/resources/keys/public.pem"))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}