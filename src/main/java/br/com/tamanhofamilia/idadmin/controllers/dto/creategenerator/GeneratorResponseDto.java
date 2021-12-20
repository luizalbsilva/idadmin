package br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator;

import lombok.Data;

@Data
public class GeneratorResponseDto {
    private Long id;

    private String name;

    private Long rangeInitial;

    private Long rangeFinal;

    private Long actualPosition;
}
