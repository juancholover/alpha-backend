package upeu.edu.pe.catalog.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "custom_field_values", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"custom_field_id", "entity_id"},
                      name = "uk_customfield_entity")
})
@Comment("Tabla de valores para campos personalizados asociados a entidades específicas")
public class CustomFieldValue extends PanacheEntityBase {
    
    @Comment("Identificador único del valor del campo personalizado")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("Campo personalizado al que pertenece este valor")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    private CustomField customField;
    
    @Comment("ID de la entidad a la que se asocia este valor")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Comment("Valor del campo personalizado en formato JSON")
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String value;
    
    @Comment("Fecha y hora de creación del valor")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Comment("Fecha y hora de la última actualización del valor")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // unique constraint moved to the entity @Table declaration above
}