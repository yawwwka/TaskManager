package com.yawka.tasks.config;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtConfig {

    @Getter(AccessLevel.NONE)
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiriationDate;

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