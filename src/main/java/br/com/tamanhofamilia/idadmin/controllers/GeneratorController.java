package br.com.tamanhofamilia.idadmin.controllers;

import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorRequestDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorResponseDto;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/generator")
@Tag(description = "Generator's Administration", name = "Generator Administration")
public class GeneratorController {
    @Autowired
    private IIdGeneratorService service;

    @Autowired
    private Converter<Generator, GeneratorResponseDto> converter;

    @Operation(description = "Creates a new Generator")
    @PostMapping
    public ResponseEntity<GeneratorResponseDto> createGenerator(@RequestBody GeneratorRequestDto body) {
        return ResponseEntity.ok(
                converter.convert(
                        service.createGenerator(
                                body.getOwner(),
                                body.getName(),
                                body.getStart(),
                                body.getEnd()
                        )
                )
        );
    }
}
