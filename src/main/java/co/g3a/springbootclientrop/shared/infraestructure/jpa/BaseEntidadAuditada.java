package co.g3a.springbootclientrop.shared.infraestructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntidadAuditada {

    // Getters
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String creadoPor;

    @LastModifiedDate
    @Column
    private Instant actualizadoEn;

    @LastModifiedBy
    @Column
    private String actualizadoPor;


}