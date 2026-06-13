package com.ecommerce.commonlib.data;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

/**
 * Mapped superclass for entities that need {@code createdOn/Off + createdBy/lastModifiedBy} columns.
 *
 * <p>Auditing wiring:</p>
 * <ul>
 *   <li>Timestamps populated by Hibernate ({@code @CreationTimestamp}, {@code @UpdateTimestamp}).
 *       These run inside the persistence layer and do not need a Spring context.</li>
 *   <li>Subjects populated by Spring Data's {@link AuditingEntityListener} which delegates
 *       to whatever {@code AuditorAware<String>} bean is registered (see {@code AuditorAwareImpl}).</li>
 * </ul>
 *
 * <p>We deliberately use the stock {@link AuditingEntityListener} rather than a custom
 * AspectJ-weaved listener — it works without compile-time weaving and survives
 * AOT/native-image processing without extra hints.</p>
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdOn;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    @LastModifiedBy
    private String lastModifiedBy;
}
