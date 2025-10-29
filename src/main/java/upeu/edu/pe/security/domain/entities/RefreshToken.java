package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Comment("Tabla de tokens de refresco para renovación de sesiones de usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class RefreshToken extends AuditableEntity {

    @Comment("Identificador único del token de refresco")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("Token JWT de refresco único para renovar el access token")
    @Column(nullable = false, unique = true, length = 1000)
    private String token; // No normalizar - es un JWT

    @Comment("Fecha y hora de expiración del token")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Comment("Usuario propietario del token de refresco")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Comment("Indica si el token ha sido revocado manualmente")
    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}