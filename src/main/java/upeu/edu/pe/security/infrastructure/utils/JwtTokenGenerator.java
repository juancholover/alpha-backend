package upeu.edu.pe.security.infrastructure.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import upeu.edu.pe.security.domain.entities.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@ApplicationScoped
public class JwtTokenGenerator {

    @ConfigProperty(name = "jwt.duration", defaultValue = "3600")
    Long jwtDuration;

    @ConfigProperty(name = "jwt.refresh.duration", defaultValue = "604800")
    Long refreshDuration;

    private static final String SECRET_KEY = "mySecretKey1234567890abcdefghij"; // 32 caracteres

    public String generateAccessToken(User user) {
        try {
            long expirationTime = Instant.now().plusSeconds(jwtDuration).getEpochSecond();

            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = String.format(
                    "{\"iss\":\"https://upeu.edu.pe\"," +
                            "\"aud\":\"upeu-sis\"," +
                            "\"sub\":\"%s\"," +
                            "\"userId\":%d," +
                            "\"email\":\"%s\"," +
                            "\"firstName\":\"%s\"," +
                            "\"lastName\":\"%s\"," +
                            "\"role\":\"%s\"," +
                            "\"exp\":%d," +
                            "\"iat\":%d}",
                    user.getUsername(),
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName() != null ? user.getFirstName() : "",
                    user.getLastName() != null ? user.getLastName() : "",
                    user.getRole().name(),
                    expirationTime,
                    Instant.now().getEpochSecond()
            );

            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            String data = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(data, SECRET_KEY);

            return data + "." + signature;
        } catch (Exception e) {
            System.out.println("Error generando access token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generando access token", e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            long expirationTime = Instant.now().plusSeconds(refreshDuration).getEpochSecond();

            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = String.format(
                    "{\"iss\":\"https://upeu.edu.pe\"," +
                            "\"aud\":\"upeu-sis\"," +
                            "\"sub\":\"%s\"," +
                            "\"userId\":%d," +
                            "\"type\":\"refresh\"," +
                            "\"exp\":%d," +
                            "\"iat\":%d}",
                    user.getUsername(),
                    user.getId(),
                    expirationTime,
                    Instant.now().getEpochSecond()
            );

            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            String data = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(data, SECRET_KEY);

            return data + "." + signature;
        } catch (Exception e) {
            System.out.println("Error generando refresh token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generando refresh token", e);
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public Long getDuration() {
        return jwtDuration;
    }

    public Long getRefreshDuration() {
        return refreshDuration;
    }
}