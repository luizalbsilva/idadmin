package br.com.tamanhofamilia.idadmin.models.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class GeneratedKey implements Serializable {
    @Column(name = "generator_id")
    private Long generatorId;
    @Column(name = "generated_number")
    private Long generated;
}
