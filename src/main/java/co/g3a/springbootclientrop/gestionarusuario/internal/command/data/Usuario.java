package co.g3a.springbootclientrop.gestionarusuario.internal.command.data;

import co.g3a.springbootclientrop.shared.infraestructure.jpa.BaseEntidadAuditada;
import jakarta.persistence.*;

import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@AllArgsConstructor // Constructor completo
@NoArgsConstructor // Requerido por JPA
@SQLDelete(sql = "UPDATE usuario SET deleted = true WHERE id=?")
@FilterDef(name = "usuarioBorradoFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "usuarioBorradoFilter", condition = "deleted = :isDeleted")
@SQLRestriction("deleted=false")
public class Usuario extends BaseEntidadAuditada {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_seq_db", allocationSize = 50)
    private Long id;
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private Integer age;
    private boolean deleted = Boolean.FALSE;
} 