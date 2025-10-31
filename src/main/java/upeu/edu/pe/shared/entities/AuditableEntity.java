package upeu.edu.pe.shared.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class AuditableEntity {

    @Comment("Indica si el registro está activo (soft delete)")
    @Column(nullable = false)
    private Boolean active = true;

    @Comment("Fecha y hora de creación del registro")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Comment("Fecha y hora de la última actualización del registro")
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Comment("Usuario que creó el registro")
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Comment("Usuario que realizó la última actualización")
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.createdBy == null) {
            this.createdBy = "system";
        }
        if (this.updatedBy == null) {
            this.updatedBy = "system";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = "system";
    }
}