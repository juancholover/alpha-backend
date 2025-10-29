package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Comment("Tabla de usuarios del sistema con información de autenticación y perfil")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class User extends AuditableEntity {

    @Comment("Identificador único del usuario")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("Nombre de usuario único para iniciar sesión (normalizado a minúsculas)")
    @Column(nullable = false, unique = true, length = 50)
    @Normalize(Normalize.NormalizeType.LOWERCASE) // usernames en minúsculas
    private String username;

    @Comment("Correo electrónico único del usuario (normalizado a minúsculas)")
    @Column(nullable = false, unique = true, length = 100)
    @Normalize(Normalize.NormalizeType.LOWERCASE) // emails en minúsculas
    private String email;

    @Comment("Contraseña del usuario encriptada con BCrypt")
    @Column(nullable = false)
    private String password; // No normalizar - mantener como está

    @Comment("Primer nombre del usuario (formato Title Case)")
    @Column(name = "first_name", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE) // Primera letra mayúscula
    private String firstName;

    @Comment("Apellido del usuario (formato Title Case)")
    @Column(name = "last_name", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE) // Primera letra mayúscula
    private String lastName;

    @Comment("Número de teléfono del usuario")
    @Column(length = 15)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY) // Solo limpiar espacios
    private String phone;

    @Comment("Rol del usuario en el sistema (ADMIN, MANAGER, USER)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Comment("Estado del usuario (ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Comment("Fecha y hora del último inicio de sesión")
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}