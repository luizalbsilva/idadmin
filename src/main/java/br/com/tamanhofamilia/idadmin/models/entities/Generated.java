package br.com.tamanhofamilia.idadmin.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(schema = Generator.SEQUENCE, name = "generated_numbers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Generated {
    @Id
    private GeneratedKey id;

    private GeneratedStatus status;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "last_change")
    private Timestamp lastChange;

    @Version
    private Long version;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "generator_id", updatable = false, insertable = false)
    private Generator generator;
}
