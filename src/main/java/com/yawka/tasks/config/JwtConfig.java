package com.yawka.tasks.config;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@Getter
public class JwtConfig {

    @Getter(AccessLevel.NONE)
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationDate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationDate;

    private Key hmacKey;

    public Key getHmacKey() {
        if (hmacKey == null) {
            hmacKey = new SecretKeySpec(
                    Base64.getDecoder().decode(secret),
                    SignatureAlgorithm.HS256.getJcaName()
            );
        }
        return hmacKey;
    }
}