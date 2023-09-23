package org.t34.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class GeneralHelper {

    public static boolean isPasswordMatch(String password, String toCompareHashedPassword) {
        String hashPassword = hashPassword(password);
        return hashPassword.equals(toCompareHashedPassword);
    }

    public static String hashPassword(String password) {
        try {
//            String salt = System.getenv("PBKDF2_salt");
            String salt = "SALT";
            int iterations = 1000;
//            int iterations = Integer.parseInt(System.getenv("PBKDF2_iterations"));
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, 256);
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded();
            return Hex.encodeHexString(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeJWT(String subject) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("SECRET_KEY");
        LocalDateTime issueDate = LocalDateTime.now();
        LocalDateTime expiryDate = issueDate.plusHours(1);

        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(Date.from(issueDate.atZone(ZoneId.systemDefault()).toInstant()))
                .setSubject(subject)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant()));

        return builder.compact();
    }

    public static String decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("SECRET_KEY"))
                .parseClaimsJws(jwt).getBody();
        return claims.getSubject();
    }
}