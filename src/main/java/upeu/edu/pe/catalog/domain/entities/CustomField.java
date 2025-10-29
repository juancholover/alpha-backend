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
@Table(name = "custom_fields", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"institution_id", "entity_type", "field_name"},
                      name = "uk_institution_entity_fieldname")
})
@Comment("Tabla de campos personalizados configurables por institución para diferentes entidades")
public class CustomField extends PanacheEntityBase {
    
    @Comment("Identificador único del campo personalizado")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("Institución a la que pertenece este campo personalizado")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @Comment("Tipo de entidad a la que se aplica este campo (ej: Student, Course, etc.)")
    @Column(name = "entity_type", nullable = false)
    private String entityType;  
    
    @Comment("Nombre del campo personalizado")
    @Column(name = "field_name", nullable = false)
    private String fieldName;
    
    @Comment("Tipo de dato del campo (text, number, date, select, etc.)")
    @Column(name = "field_type", nullable = false)
    private String fieldType;  
    
    @Comment("Indica si el campo es obligatorio")
    @Column(name = "is_required", nullable = false)
    private boolean isRequired;
    
    @Comment("Opciones disponibles para campos tipo select en formato JSON")
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String options; 
    
    @Comment("Reglas de validación del campo en formato JSON")
    @Column(name = "validation_rules", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String validationRules;
    
    @Comment("Orden de visualización del campo en formularios")
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Comment("Fecha y hora de creación del campo")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Comment("Fecha y hora de la última actualización del campo")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
  
    // unique constraint moved to the entity @Table declaration above
}