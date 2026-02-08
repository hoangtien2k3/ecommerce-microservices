package com.hoangtien2k3.tax.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_rate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class TaxRate extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rate;

    @Column(length = 25)
    private String zipCode;

    @Column
    private Long stateOrProvinceId;

    @Column(nullable = false)
    private Long countryId;

    @ManyToOne
    @JoinColumn(name = "tax_class_id", nullable = false)
    private TaxClass taxClass;
}
