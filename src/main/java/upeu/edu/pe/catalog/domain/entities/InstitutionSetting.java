package upeu.edu.pe.catalog.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "institution_settings")
@Comment("Tabla de configuraciones específicas por módulo para cada institución")
public class InstitutionSetting {
    @Comment("Identificador único de la configuración")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("Institución a la que pertenece esta configuración")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @Comment("Módulo del sistema al que pertenece esta configuración")
    @Column(nullable = false)
    private String module;
    
    @Comment("Clave única de la configuración dentro del módulo")
    @Column(nullable = false)
    private String settingKey;
    
    @Comment("Valor de la configuración")
    @Column(columnDefinition = "text")
    private String settingValue;
    
    @Comment("Tipo de dato del valor (string, number, boolean, json, etc.)")
    @Column(nullable = false)
    private String dataType;
    
    @Comment("Descripción del propósito de esta configuración")
    @Column
    private String description;
    
    @Comment("Indica si esta configuración es visible públicamente")
    @Column(nullable = false)
    private Boolean isPublic;
    
    @Comment("Fecha y hora de creación de la configuración")
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Comment("Fecha y hora de la última actualización de la configuración")
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}