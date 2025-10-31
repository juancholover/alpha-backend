package upeu.edu.pe.catalog.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

@Entity
@Table(name = "categories")
@Comment("Tabla de categorías para clasificación de elementos del catálogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class Category extends AuditableEntity {

    @Comment("Identificador único de la categoría")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("Nombre único de la categoría (normalizado a mayúsculas)")
    @Column(nullable = false, unique = true, length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String name;

    @Comment("Descripción detallada de la categoría (normalizada a mayúsculas)")
    @Column(length = 255)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String description;
}