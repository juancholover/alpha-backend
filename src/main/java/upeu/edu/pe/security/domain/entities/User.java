package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Normalize(Normalize.NormalizeType.LOWERCASE) // usernames en minúsculas
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @Normalize(Normalize.NormalizeType.LOWERCASE) // emails en minúsculas
    private String email;

    @Column(nullable = false)
    private String password; // No normalizar - mantener como está

    @Column(name = "first_name", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE) // Primera letra mayúscula
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE) // Primera letra mayúscula
    private String lastName;

    @Column(length = 15)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY) // Solo limpiar espacios
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}