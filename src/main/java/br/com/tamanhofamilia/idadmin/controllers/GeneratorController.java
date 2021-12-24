package br.com.tamanhofamilia.idadmin.controllers;

import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorRequestDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorResponseDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.NextNumberRequestDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.NextNumberResponseDto;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/generator")
public class GeneratorController {
    @Autowired
    private IIdGeneratorService service;

    @Autowired
    private Converter<Generator, GeneratorResponseDto> converter;

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

    @PostMapping("/{id}/next")
    public ResponseEntity<NextNumberResponseDto> nextNumber(@PathVariable("id") long generatorId, @RequestBody NextNumberRequestDto body) {
        return ResponseEntity.ok(new NextNumberResponseDto(service.next(generatorId, body.getOwner())));
    }
}
