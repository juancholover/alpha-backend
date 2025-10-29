package upeu.edu.pe.catalog.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "academic_structures")
@Comment("Tabla de estructuras académicas que define períodos y ciclos de las instituciones")
public class AcademicStructure {
    @Comment("Identificador único de la estructura académica")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("Institución a la que pertenece esta estructura académica")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @Comment("Tipo de estructura académica (semester, trimester, quarter, etc.)")
    @Column(nullable = false)
    private String structureType;  // semester, trimester, quarter, etc.
    
    @Comment("Número de períodos académicos por año")
    @Column(nullable = false)
    private Integer periodsPerYear;
    
    @Comment("Configuración detallada de la estructura en formato JSON")
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String configuration;  // JSON con detalles específicos
    
    @Comment("Indica si la estructura académica está activa")
    @Column(nullable = false)
    private boolean isActive;
    
    @Comment("Fecha y hora de creación de la estructura académica")
    @Column(nullable = false)
    private LocalDateTime createdAt;
}