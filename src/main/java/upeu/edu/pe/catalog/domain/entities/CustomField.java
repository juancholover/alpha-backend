package upeu.edu.pe.catalog.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
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
public class CustomField extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType;  
    
    @Column(name = "field_name", nullable = false)
    private String fieldName;
    
    @Column(name = "field_type", nullable = false)
    private String fieldType;  
    
    @Column(name = "is_required", nullable = false)
    private boolean isRequired;
    
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String options; 
    
    @Column(name = "validation_rules", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String validationRules;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
  
    // unique constraint moved to the entity @Table declaration above
}