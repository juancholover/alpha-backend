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
@Table(name = "custom_field_values", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"custom_field_id", "entity_id"},
                      name = "uk_customfield_entity")
})
public class CustomFieldValue extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    private CustomField customField;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String value;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // unique constraint moved to the entity @Table declaration above
}