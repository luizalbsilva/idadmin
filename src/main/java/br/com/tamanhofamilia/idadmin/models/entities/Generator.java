package br.com.tamanhofamilia.idadmin.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(schema = Generator.SCHEMA, name = "id_generator")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "SEQUENCE_IDGEN", schema = Generator.SCHEMA,
        sequenceName = "GEN_IDGEN", allocationSize = 1)
public class Generator {
    public static final String SCHEMA ="idgenerator";
    @Id
    @Column(name = "generator_id", columnDefinition = "bigint")
    @GeneratedValue(generator = "SEQUENCE_IDGEN")
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(50)", nullable = false, length = 50)
    private String name;

    @Column(name = "owner", columnDefinition = "varchar(50)", nullable = false, length = 50)
    private String owner;

    @Column(name = "initial", columnDefinition = "bigint", nullable = false)
    private Long rangeInitial;

    @Column(name = "final", columnDefinition = "bigint", nullable = false)
    private Long rangeFinal;

    @Column(name = "actual", columnDefinition = "bigint", nullable = false)
    private long actualPosition;

    @Version
    private Long version;

    @Column(name = "enabled", columnDefinition = "boolean")
    private boolean enabled;
}
