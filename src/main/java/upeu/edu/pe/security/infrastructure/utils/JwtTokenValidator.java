package upeu.edu.pe.security.infrastructure.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@ApplicationScoped
public class JwtTokenValidator {

    private static final String SECRET_KEY = "mySecretKey1234567890abcdefghij"; // Misma clave que en JwtTokenGenerator
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            // Remover "Bearer " si está presente
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            // Verificar la firma
            String data = header + "." + payload;
            String expectedSignature = hmacSha256(data, SECRET_KEY);

            if (!signature.equals(expectedSignature)) {
                System.out.println("Invalid signature");
                return false;
            }

            // Verificar expiración
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            JsonNode payloadNode = objectMapper.readTree(decodedPayload);

            long exp = payloadNode.get("exp").asLong();
            long now = Instant.now().getEpochSecond();

            if (now >= exp) {
                System.out.println("Token expired");
                return false;
            }

            // Verificar issuer y audience
            String issuer = payloadNode.get("iss").asText();
            String audience = payloadNode.get("aud").asText();

            if (!"https://upeu.edu.pe".equals(issuer) || !"upeu-sis".equals(audience)) {
                System.out.println("Invalid issuer or audience");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String[] parts = token.split("\\.");
            String payload = parts[1];
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            JsonNode payloadNode = objectMapper.readTree(decodedPayload);

            return payloadNode.get("sub").asText();
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String[] parts = token.split("\\.");
            String payload = parts[1];
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            JsonNode payloadNode = objectMapper.readTree(decodedPayload);

            return payloadNode.get("userId").asLong();
        } catch (Exception e) {
            return null;
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}