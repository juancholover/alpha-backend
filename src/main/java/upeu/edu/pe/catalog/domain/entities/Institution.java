package upeu.edu.pe.catalog.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "institutions")
@Comment("Tabla de instituciones educativas del sistema")
public class Institution {
    @Comment("Identificador único de la institución")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("Nombre oficial de la institución educativa")
    @Column(nullable = false)
    private String name;
    
    @Comment("Código único de identificación de la institución")
    @Column(nullable = false, unique = true)
    private String code;
    
    @Comment("País donde se ubica la institución")
    @Column(nullable = false)
    private String country;
    
    @Comment("Configuración personalizada de la institución en formato JSON")
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String configuration;
    
    @Comment("Fecha y hora de creación de la institución")
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Comment("Indica si la institución está activa")
    @Column(nullable = false)
    private boolean isActive;
    
    @Comment("Configuraciones específicas de la institución")
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InstitutionSetting> settings = new ArrayList<>();
}