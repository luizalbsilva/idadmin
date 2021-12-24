package br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator;

import lombok.Data;

@Data
public class GeneratorRequestDto {
    private String owner;
    private String name;
    private long start;
    private long end;
}
